package com.sumavision.talktv2.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.activity.UserLevelInfoActivity;
import com.sumavision.talktv2.adapter.ShakeProgramAdapter;
import com.sumavision.talktv2.bean.GeneralData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.FlowLayout;
import com.sumavision.talktv2.components.MarqueeTextView;
import com.sumavision.talktv2.components.StaticListView;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.GetShakePointRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShakeProgramParser;
import com.sumavision.talktv2.http.json.ShakeProgramRequest;
import com.sumavision.talktv2.http.json.ShakeProgramTagParser;
import com.sumavision.talktv2.http.json.ShakeProgramTagRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.ShakeListener;
import com.sumavision.talktv2.utils.ShakeListener.OnShakeListener;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 摇一摇
 * 
 * @author cx
 * 
 */
public class ShakeFragment extends BaseFragment implements OnClickListener {

	private MarqueeTextView marqueeText;
	private FlowLayout flowLayout;
	private ArrayList<TextView> listTag = new ArrayList<TextView>();
	private LinearLayout shakedProgram;
	
	private boolean loadData = true;
	
	private ShakeProgramTagParser tagParser = new ShakeProgramTagParser();
	private ShakeProgramParser shakeParser = new ShakeProgramParser();
	
	private Resources mResources;
	private ColorStateList mColorState;
	
	private RelativeLayout shakedShelter;
	private View shakedView;
	private StaticListView listView;
	private TextView shakedScore;
	private Button shakedGet;
	private LinearLayout scoreLayout;
	private TextView userText,loginText;
	
	private List<ProgramData> listProgram = new ArrayList<ProgramData>();
	private ShakeProgramAdapter adapter;
	private List<List<ProgramData>> listPrograms = new ArrayList<List<ProgramData>>();
	private int historyId;
	private int point;
	
	private ImageView shakeHand;
	private ImageView shakeSound;
	private boolean hasSound = true;
	private ShakeListener mShakeListener;
	private SoundPool sndPool;
	private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();
	private Handler uiHandler;
	
	private final static int UPDATE_SHAKE_RESULT = 1;
	private View lastTag;
	private PowerManager.WakeLock lock;

	public static ShakeFragment newInstance() {
		ShakeFragment fragment = new ShakeFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_shake);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		initHandler();
		hasSound = PreferencesUtils.getBoolean(getActivity(),null,"shake_sound",true);
		marqueeText = (MarqueeTextView) view.findViewById(R.id.marquee_text);
		marqueeText.setScrollWidth(600);
		marqueeText.setCurrentHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
		marqueeText.setCurrentPosition(50);
		marqueeText.setSpeed(5);
		marqueeText.setGravity(Gravity.BOTTOM);
		userText = (TextView) view.findViewById(R.id.shake_user_info);
		userText.setOnClickListener(this);
		loginText = (TextView) view.findViewById(R.id.shake_login);
		loginText.setOnClickListener(this);
		showUserInfo();
		layout_ads = (LinearLayout)view.findViewById(R.id.shake_ad_layout);
//		marqueeText.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				getShakeProgram();
//			}
//		});
		flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);
		shakedProgram = (LinearLayout) view.findViewById(R.id.shaked_program_layout);
		shakeHand = (ImageView) view.findViewById(R.id.shake_hand);
		shakeSound = (ImageView) view.findViewById(R.id.shake_sound);
		shakeSound.setOnClickListener(this);
		if (!canShake()){
			shakeHand.setOnClickListener(this);
		}
		if (!hasSound) {
			shakeSound.setImageResource(R.drawable.shake_sound_unable);
		} else {
			shakeSound.setImageResource(R.drawable.shake_sound_normal);
		}
		mResources = getActivity().getResources();
		mColorState = mResources.getColorStateList(R.color.color_filter_text);
		
		setLayoutTransition();
		initShakedProgramView();
		
		if (loadData) {
			getShakeTag();
		}
		
		initShakeListener();
		if (PreferencesUtils.getBoolean(getActivity(),null,Constants.kedaxunfei,false)){
			createBannerAd();
		}
		PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_BRIGHT_WAKE_LOCK,getClass().getSimpleName());
		EventBus.getDefault().register(this);
	}

	private void showUserInfo() {
		if (UserNow.current().userID>0){
			userText.setVisibility(View.VISIBLE);
			String s = "您的等级为 LV" + UserNow.current().level + "，点击查看";
			SpannableString ss = new SpannableString(s);
			ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.navigator_bg_color)),
					s.indexOf("LV"),s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			userText.setText(ss);
			loginText.setVisibility(View.GONE);
		} else {
			loginText.setVisibility(View.VISIBLE);
			userText.setVisibility(View.GONE);
		}
	}

	private void initShakedProgramView() {
		shakedView = getActivity().getLayoutInflater().inflate(R.layout.shaked_program, null);
		shakedScore = (TextView) shakedView.findViewById(R.id.score);
		shakedGet = (Button) shakedView.findViewById(R.id.get_score);
		scoreLayout = (LinearLayout) shakedView.findViewById(R.id.score_layout);
		listView = (StaticListView) shakedView.findViewById(R.id.shake_listview);
		adapter = new ShakeProgramAdapter(getActivity(), listPrograms);
		shakedShelter = (RelativeLayout) shakedView.findViewById(R.id.shaked_shelter);
		listView.setAdapter(adapter);
		shakedGet.setOnClickListener(this);
		shakedShelter.setOnClickListener(this);
	}
	
	private void updateShakedProgramView() {
		updateMarqueeText(shakeParser.scrollText);
		listProgram.clear();
		listProgram.addAll(shakeParser.listProgram);
		listPrograms.clear();
		addProgramList();
		adapter.notifyDataSetInvalidated();
		point = shakeParser.point;
		historyId = shakeParser.historyId;
		scoreLayout.setVisibility(View.GONE);
		if (point != 0 && historyId != 0) {
			String str = "人品大爆发了~获得  "+point + " 积分";
			SpannableString ss = new SpannableString(str);
			ss.setSpan(new ForegroundColorSpan(Color.RED),11,str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			shakedScore.setText(ss);
			scoreLayout.setVisibility(View.VISIBLE);
			scoreLayout.setOnClickListener(this);
		}
		if (listPrograms != null && listPrograms.size() > 0) {
			if (shakedView.getParent() != null){
				shakedProgram.removeView(shakedView);
			}
			shakedProgram.addView(shakedView);
			adapter.notifyDataSetChanged();
		}
	}
	
	private void addProgramList() {
		int length = listProgram.size();
		if(length>3){
			for (int i = 0; i < length; i += 2) {
				if (i < length) {
					List<ProgramData> list = new ArrayList<ProgramData>();
					if (listProgram.get(i) != null) {
						list.add(listProgram.get(i));
					}
					if (i+1<length){
						if (listProgram.get(i + 1) != null) {
							list.add(listProgram.get(i + 1));
						}
					}

					listPrograms.add(list);
				}
			}
		}else {
			ToastHelper.showToast(getActivity(), "请等待节目更新");
		}
	}
	
	private void resetProgramData() {
		point = 0;
		historyId = 0;
		
		shakedProgram.removeView(shakedView);
	}
	public boolean canShake(){
		SensorManager sensorManager = (SensorManager) getActivity()
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			// 获得重力传感器
			Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (sensor != null && sensor.getMinDelay()>0){
				return  true;
			}
		}
		return false;
	}
	private void initHandler() {
		uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == UPDATE_SHAKE_RESULT) {
					if (shakeParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						updateShakedProgramView();
					} else {
						ToastHelper.showToast(getActivity(), shakeParser.errMsg);
					}
				}
				super.handleMessage(msg);
			}
		};
	}
	@SuppressLint("NewApi")
	private void setLayoutTransition() {
		if (Build.VERSION.SDK_INT<11){
			return;
		}
		try {
			LayoutTransition mTransition = new LayoutTransition();  
			ObjectAnimator anim = ObjectAnimator.ofFloat(this, "y", 0f, 1f);
			anim.setDuration(500);
	        mTransition.setAnimator(LayoutTransition.APPEARING, anim);  
	        shakedProgram.setLayoutTransition(mTransition);
		} catch (Exception e) {
		}
	}
	
	private void getShakeTag() {
		showLoadingLayout();
		VolleyHelper.post(new ShakeProgramTagRequest().make(), new ParseListener(tagParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (tagParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					updateView();
					loadData = false;
					hideLoadingLayout();
				}
			}
		}, null);
	}
	
	private void getShakeProgram() {
		MobclickAgent.onEvent(getActivity(), "yjpshake");
		showLoadingLayout();
		VolleyHelper.post(new ShakeProgramRequest(getActivity(), getTagsId()).make(), new ParseListener(shakeParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				mShakeListener.start();
				uiHandler.sendEmptyMessage(UPDATE_SHAKE_RESULT);
				if (shakeParser.errCode == JSONMessageType.SERVER_CODE_OK){
					if (hasSound) {
						sndPool.play(soundPoolMap.get(1), (float) 1, (float) 1, 0, 0, (float) 1.0);
					}
					if (lock !=null){
						lock.acquire(10000);
					}
				}
				shakedGet.setClickable(true);
				shakedGet.setEnabled(true);
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				ToastHelper.showToast(getActivity(),"网络异常");
				hideLoadingLayout();
				mShakeListener.start();
				shakedGet.setClickable(true);
				shakedGet.setEnabled(true);
			}
		});
	}
	private void vibrate(Long time){
		((Vibrator)getActivity().getSystemService(Service.VIBRATOR_SERVICE)).vibrate(new long[]{100, time, 200, time}, -1);
	}
	private String getTagsId() {
		if (lastTag != null){
			Integer id = (Integer) lastTag.getTag();
			return String.valueOf(id);
		}else {
			return "";
		}
//		StringBuilder tagsId = new StringBuilder();
//		int selectedNumber = 0;
//		for (int i = 0; i < listTag.size(); i++) {
//			TextView text = listTag.get(i);
//			if (text.isSelected()) {
//				selectedNumber++;
//				tagsId.append(text.getTag() + ",");
//			}
//		}
//		if (selectedNumber == listTag.size() || selectedNumber == 0) {
//			return "";  //全选或不选返回空串，提高后台查询效率
//		} else {
//			return tagsId.substring(0, tagsId.length() - 1);
//		}
	}
	
	private void initShakeListener() {
		loadSound();
		mShakeListener = new ShakeListener(getActivity());
        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
				resetProgramData();
				mShakeListener.stop();
				if (hasSound) {
					sndPool.play(soundPoolMap.get(0), (float) 1, (float) 1, 0, 0, (float) 1.2);
//					vibrate((long) 300);
				}
				new Handler().postDelayed(new Runnable() {
					public void run() {
						getShakeProgram();
					}
				}, 800);
			}
		});
	}
	
	private void startAnim() {
		RotateAnimation rotateStart = new RotateAnimation(0, 20,
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 1f);
		rotateStart.setDuration(400);
		rotateStart.setRepeatMode(Animation.REVERSE);
		rotateStart.setRepeatCount(1);
		shakeHand.startAnimation(rotateStart);
	}
	
	private void loadSound() {
		sndPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 5);
		new Thread() {
			public void run() {
				try {
					soundPoolMap.put(
							0,
							sndPool.load(getActivity().getAssets().openFd(
											"sound/shake_sound_male.mp3"), 1));
					soundPoolMap.put(
							1,
							sndPool.load(getActivity().getAssets().openFd(
											"sound/shake_match.mp3"), 1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void updateView() {
		updateMarqueeText(tagParser.scrollText);
		int length = tagParser.list.size();
		length = length > 8 ? 8 : length;
		for (int i = 0; i < length; i++) {
			GeneralData data = tagParser.list.get(i);
			flowLayout.addView(getTagView(data.name, data.id));
		}
	}
	
	private void updateMarqueeText(String text) {
		if (!TextUtils.isEmpty(text)) {
			marqueeText.setText(text);
		}
	}
	
	private TextView getTagView(String str, final int id) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getActivity().getResources().getDisplayMetrics());
		params.setMargins(margin, margin, 0, 0);
		TextView text = new TextView(getActivity());
		text.setLayoutParams(params);
		text.setBackgroundResource(R.drawable.shake_tag_bg);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		text.setPadding(padding, padding, padding, padding);
		text.setGravity(Gravity.CENTER);
		text.setTextColor(mColorState);
		text.setText(str);
		text.setTag(id);
		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				selectTag(id);
				v.setSelected(!v.isSelected());
				if (lastTag != null && lastTag != v) {
					lastTag.setSelected(false);
				}
				if (v.isSelected()){
					lastTag = v;
				}else{
					lastTag = null;
				}
			}
		});
		listTag.add(text);
		return text;
	}
	public void selectTag(int id){
		Integer tempId;
		View tempView;
		for (int i = 0; i < tagParser.list.size(); i++) {
			GeneralData data = tagParser.list.get(i);
			tempView =  flowLayout.getChildAt(i);
			tempId = (Integer) tempView.getTag();
			if (tempId == id){
				tempView.setSelected(true);
			}else{
				tempView.setSelected(false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.get_score) {
			MobclickAgent.onEvent(getActivity(), "yjplingjifen");
			if (UserNow.current().userID <= 0){
				startActivity(new Intent(getActivity(),LoginActivity.class));
			}else{
				v.setEnabled(false);
				v.setClickable(false);
				getScore();
			}
		} else if (v.getId() == R.id.shake_sound) {
			if (hasSound) {
				shakeSound.setImageResource(R.drawable.shake_sound_unable);
			} else {
				shakeSound.setImageResource(R.drawable.shake_sound_normal);
			}
			hasSound = !hasSound;
			PreferencesUtils.putBoolean(getActivity(),null,"shake_sound",hasSound);
		} else if (v.getId() == R.id.shaked_shelter) {
			shakedProgram.removeView(shakedView);
		}else if(v.getId() == R.id.shake_hand){
			getShakeProgram();
		} else if (v.getId() == R.id.shake_user_info){
			MobclickAgent.onEvent(getActivity(),"yjpmylevel");
			startActivity(new Intent(getActivity(), UserLevelInfoActivity.class));
		} else if (v.getId() == R.id.shake_login){
			startActivity(new Intent(getActivity(),LoginActivity.class));
		}
	}

	
	ResultParser rparser = new ResultParser();
	private void getScore() {
		
		VolleyHelper.post(new GetShakePointRequest(getActivity(), historyId).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					ToastHelper.showToast(getActivity(), rparser.info);
					UserInfoEvent event = new UserInfoEvent();
					UserNow.current().totalPoint += 10;
					EventBus.getDefault().post(event);
				} else {
					ToastHelper.showToast(getActivity(), rparser.errMsg);
				}
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				ToastHelper.showToast(getActivity(),"网络异常");
				shakedGet.setClickable(true);
				shakedGet.setEnabled(true);
			}
		});
	}

	@Override
	public void reloadData() {
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mShakeListener != null){
			mShakeListener.start();
		}
//		if (null != lock && (!lock.isHeld())) {
//			lock.acquire();
//		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
		if (lock!=null && lock.isHeld()){
			lock.release();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest("shakeProgramTag");
		VolleyHelper.cancelRequest("shakeProgram");
		VolleyHelper.cancelRequest("getShakePoint");
		EventBus.getDefault().unregister(this);
	}
	public void onEvent(UserInfoEvent event){
		showUserInfo();
	}
	IFLYBannerAd bannerView;
	LinearLayout layout_ads;
	public void createBannerAd() {
		//此广告位为Demo专用，广告的展示不产生费用
		String adUnitId = "ECAEEB8439E3D544634FD6DC6F67ECAA";
		//创建旗帜广告，传入广告位ID
		bannerView = IFLYBannerAd.createBannerAd(getActivity(), adUnitId);
		//设置请求的广告尺寸
		bannerView.setAdSize(IFLYAdSize.BANNER);
		//设置下载广告前，弹窗提示
		bannerView.setParameter(AdKeys.DOWNLOAD_ALERT, "false");

		//请求广告，添加监听器
		bannerView.loadAd(mAdListener);
		//将广告添加到布局
		layout_ads.removeAllViews();
		layout_ads.addView(bannerView);

	}

	IFLYAdListener mAdListener = new IFLYAdListener(){

		/**
		 * 广告请求成功
		 */
		@Override
		public void onAdReceive() {
			//展示广告
			bannerView.showAd();
			AdStatisticsUtil.adCount(getActivity(), Constants.shakeBottom);
			Log.d("Ad_Android_Demo", "onAdReceive");
		}

		/**
		 * 广告请求失败
		 */
		@Override
		public void onAdFailed(AdError error) {
			Log.d("Ad_Android_Demo", "onAdFailed");
		}

		/**
		 * 广告被点击
		 */
		@Override
		public void onAdClick() {
			Log.d("Ad_Android_Demo", "onAdClick");
		}

		/**
		 * 广告被关闭
		 */
		@Override
		public void onAdClose() {
			Log.d("Ad_Android_Demo", "onAdClose");
		}
	};
}
