package com.sumavision.talktv2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.ReceiverAdapter;
import com.sumavision.talktv2.adapter.RecommendImageAdapter;
import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.FocusGallery;
import com.sumavision.talktv2.components.MarqueeTextView;
import com.sumavision.talktv2.components.StaticListView;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BadgeLotteryDetailParser;
import com.sumavision.talktv2.http.json.BadgeLotteryDetailRequest;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShareRequest;
import com.sumavision.talktv2.http.listener.OnNewActivityCompleteListener;
import com.sumavision.talktv2.http.listener.OnNewActivitygetBadgeListener;
import com.sumavision.talktv2.http.request.VolleyActivityRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 最新(3.0)徽章流程
 * 
 * @author suma-hpb
 * 
 */
public class ActivityActivity extends BaseActivity implements OnClickListener,
		OnSharedPreferenceChangeListener, OnNewActivitygetBadgeListener,
		OnNewActivityCompleteListener {

	public static final String ACTIVITY_PREFERENCE_NAME = "activities";
	public static final String ACTIVITY_PREFERENCE_KEY = "lotteryCount";
	public static final String ACTIVITY_PREFERENCE_KEY_PLAY = "play";
	public static final String ACTIVITY_PREFERENCE_KEY_SINA = "sina";
	public static final String ACTIVITY_PREFERENCE_KEY_WEIXIN = "weixin";
	public static final String ACTIVITY_PREFERENCE_KEY_CIRCLE = "circle";
	public static final String ACTIVITY_PREFERENCE_KEY_QZONE = "qzone";

	boolean isTemp;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity);
		isTemp = getIntent().getBooleanExtra("isTemp", false);
		if (isTemp && UserNow.current().userID<=0){
			finish();
		}
		if (isTemp){
			getSupportActionBar().setTitle("端午节抽奖");
		}else {
			getSupportActionBar().setTitle("徽章抽奖");
		}
		activityNewData.activityId = getIntent().getLongExtra("activityId", 0L);
		activityNewData.activityName = getIntent().getStringExtra(
				"activityName");
		activityNewData.state = getIntent().getIntExtra("state",
				ActivityData.STATE_ONGOING);
		activityNewData.joinStatus = getIntent().getIntExtra("joinStatus", 0);

		activitySp = getSharedPreferences(ACTIVITY_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		initViews();
		activitySp.registerOnSharedPreferenceChangeListener(this);
		EventBus.getDefault().register(this);
	};

	SharedPreferences activitySp;
	ImageView playImgView, lotteryImgView, shareImgView;
	MarqueeTextView marqueeTextView;
	TextView nameTxt, lotteryTimes, playTxt;
	TextView vipText,getVipText;
	ScrollView contentView;
	StaticListView userListView;
	RelativeLayout announcement;
	int chanceCount = 0;
	ActivityData activityNewData = new ActivityData();
	BadgeLotteryDetailParser mBadgeLotteryParser = new BadgeLotteryDetailParser();

	protected void initViews() {
		initLoadingLayout();
		contentView = (ScrollView) findViewById(R.id.content);
		nameTxt = (TextView) findViewById(R.id.tv_name);
		playTxt = (TextView) findViewById(R.id.tv_play);
		vipText = (TextView) findViewById(R.id.vip_award);
		getVipText = (TextView) findViewById(R.id.vip_intro);
		vipText.setOnClickListener(this);
		if (UserNow.current().userID<=0 || (UserNow.current().isVip && !PreferencesUtils.getBoolean(this,ACTIVITY_PREFERENCE_NAME,
				ACTIVITY_PREFERENCE_KEY + "_vip_"
						+ activityNewData.activityId, false))){
			vipText.setEnabled(true);
			vipText.setText("VIP抽奖 +1");
		} else {
			vipText.setEnabled(false);
			vipText.setText("VIP抽奖 +0");
		}
		getVipText.setOnClickListener(this);
		getVipText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		getVipText.getPaint().setAntiAlias(true);
		if (PreferencesUtils.getBoolean(this,null,Constants.vipActivity,false)){
			getVipText.setVisibility(View.VISIBLE);
		} else {
			getVipText.setVisibility(View.GONE);
		}
		playImgView = (ImageView) findViewById(R.id.imgv_play);
		lotteryImgView = (ImageView) findViewById(R.id.imgv_lottery);
		shareImgView = (ImageView) findViewById(R.id.imgv_share);
		lotteryTimes = (TextView) findViewById(R.id.tv_lottery_times);
		userListView = (StaticListView) findViewById(R.id.list_user);
		marqueeTextView = (MarqueeTextView) findViewById(R.id.marquee_text);
		announcement = (RelativeLayout) findViewById(R.id.announcement);
		findViewById(R.id.btn_gift).setOnClickListener(this);
		initStarsLayout();

		playImgView.setOnClickListener(this);
		getBadgeDetail();
		if (isTemp){
			findViewById(R.id.rlayou_arrow2).setVisibility(View.GONE);
			findViewById(R.id.rlayout_share).setVisibility(View.GONE);
		}
	}
	
	private void updateMarqueeText(String text) {
		if (TextUtils.isEmpty(text)) {
			return;
		}
		announcement.setVisibility(View.VISIBLE);
		marqueeTextView.setScrollWidth(800);
		marqueeTextView.setCurrentHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27, getResources().getDisplayMetrics()));
		marqueeTextView.setCurrentPosition(100);
		marqueeTextView.setSpeed(5);
		marqueeTextView.setText(text);
		marqueeTextView.setGravity(Gravity.CENTER_VERTICAL);
	}

	int canJoinCount;

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("ActivityActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("ActivityActivity");
		super.onPause();
	}

	private void getBadgeDetail() {
		showLoadingLayout();
		VolleyHelper.post(new BadgeLotteryDetailRequest(
				activityNewData.activityId, this).make(), new ParseListener(
				mBadgeLotteryParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (mBadgeLotteryParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					contentView.setVisibility(View.VISIBLE);
					activityNewData = mBadgeLotteryParser.activityData;
					int joinStatus = activityNewData.joinStatus;
//					activityNewData.joinStatus = joinStatus;
					picTitleList = mBadgeLotteryParser.picTitles;
					onUpdateUi();
					updateMarqueeText(activityNewData.announcement);
				} else {
					Toast.makeText(getApplicationContext(),
							mBadgeLotteryParser.errMsg, Toast.LENGTH_SHORT)
							.show();
					showErrorLayout();
				}

			}
		}, this);
	}

	private void onUpdateUi() {
		nameTxt.setText(activityNewData.activityName);
		if (activityNewData.receiverList.size() > 0) {
			userListView.setAdapter(new ReceiverAdapter(this,
					activityNewData.receiverList));
		}
		contentView.smoothScrollTo(0, 0);
		updateStarsLayout(activityNewData.pics);
		if (activityNewData.joinStatus == ActivityData.JOIN_STATUS_FINISHED) {
			playTxt.setText(R.string.play_again);
		}
		if (activityNewData.state == ActivityData.STATE_OVER
				|| activityNewData.joinedTimes == activityNewData.totalTimes) {
			lotteryImgView.setImageResource(R.drawable.activity_pressed);
			playImgView.setImageResource(R.drawable.activity_pressed);
			shareImgView.setImageResource(R.drawable.activity_pressed);
			playTxt.setText(R.string.play_again);
			lotteryTimes.setText(getString(R.string.activity_lottery, 0));
			vipText.setEnabled(false);
			vipText.setText("VIP抽奖 +0");
		} else {
			canJoinCount = activityNewData.totalTimes
					- activityNewData.joinedTimes;
			chanceCount = PreferencesUtils.getInt(this,
					ACTIVITY_PREFERENCE_NAME, ACTIVITY_PREFERENCE_KEY + "_"
							+ activityNewData.activityId);
			chanceCount = chanceCount > canJoinCount ? canJoinCount
					: chanceCount;
			if (activityNewData.joinStatus == ActivityData.JOIN_STATUS_FINISHED
					|| PreferencesUtils.getBoolean(this,
							ACTIVITY_PREFERENCE_NAME,
							ACTIVITY_PREFERENCE_KEY_PLAY + "_"
									+ activityNewData.activityId)) {
				playTxt.setText(R.string.play_again);
				playImgView.setImageResource(R.drawable.activity_pressed);
			}
			lotteryTimes.setText(getString(R.string.activity_lottery,
					chanceCount));
			lotteryImgView.setOnClickListener(this);
			shareImgView.setOnClickListener(this);
		}
		if (isTemp){
			if (activityNewData.joinStatus == ActivityData.JOIN_STATUS_FINISHED
					|| PreferencesUtils.getBoolean(this,"dw","duanwu_award"+UserNow.current().userID,false)){
				chanceCount = 0;
				lotteryImgView.setImageResource(R.drawable.activity_pressed);
				playTxt.setText(R.string.play_again);
				playImgView.setImageResource(R.drawable.activity_pressed);
				lotteryTimes.setText(getString(R.string.activity_lottery,
						chanceCount));
			}else if(activityNewData.joinStatus == ActivityData.JOIN_STATUS_UNFINISH){
				if (TextUtils.isEmpty(PreferencesUtils.getString(this,"dw","duanwu_goods",""))){
					chanceCount = 0;
					lotteryImgView.setImageResource(R.drawable.activity_pressed);
				}else {
					chanceCount = 1;
					lotteryImgView.setImageResource(R.drawable.activity_lottery_normal);
				}
				playTxt.setText(R.string.play_again);
				playImgView.setImageResource(R.drawable.activity_pressed);
				lotteryTimes.setText(getString(R.string.activity_lottery,
						chanceCount));
			}else if (activityNewData.joinStatus == ActivityData.JOIN_STATUS_UNJOIN){
				chanceCount = 0;
				if (PreferencesUtils.getBoolean(this,"dw","duanwu_play"+UserNow.current().userID,false)){
					chanceCount = 1;
					playTxt.setText(R.string.play_again);
					playImgView.setImageResource(R.drawable.activity_pressed);
				}else {
					playTxt.setText(R.string.activity_play);
					playImgView.setImageResource(R.drawable.activity_play_normal);
				}
				lotteryImgView.setImageResource(R.drawable.activity_lottery_normal);
				lotteryTimes.setText(getString(R.string.activity_lottery,
						chanceCount));
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_gift:
			if (UserNow.current().userID > 0) {
				Intent giftIntent = new Intent(this, MyGiftActivity.class);
				if (isTemp){
					giftIntent.putExtra("isTemp",isTemp);
				}
				startActivity(giftIntent);
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.imgv_play:
			openPlayerActivity();
			break;
		case R.id.imgv_lottery:
			if (UserNow.current().userID > 0) {
				if (canJoinCount < chanceCount) {
					chanceCount = canJoinCount;
				}
				if (chanceCount > 0) {
					if (!isTemp){
						chanceCount--;
						PreferencesUtils.putInt(this, ACTIVITY_PREFERENCE_NAME,
								ACTIVITY_PREFERENCE_KEY + "_"
										+ activityNewData.activityId, chanceCount);
					}
					goToGuaActivity();
				} else {
					Toast.makeText(this, "无抽奖机会", Toast.LENGTH_SHORT).show();
				}
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.imgv_share:
			if (canJoinCount > 0) {
				boolean sinaShared = PreferencesUtils.getBoolean(this,
						ACTIVITY_PREFERENCE_NAME, ACTIVITY_PREFERENCE_KEY_SINA
								+ "_" + activityNewData.activityId);
				boolean wxShared = PreferencesUtils.getBoolean(this,
						ACTIVITY_PREFERENCE_NAME,
						ACTIVITY_PREFERENCE_KEY_WEIXIN + "_"
								+ activityNewData.activityId);
				boolean cShared = PreferencesUtils.getBoolean(this,
						ACTIVITY_PREFERENCE_NAME,
						ACTIVITY_PREFERENCE_KEY_CIRCLE + "_"
								+ activityNewData.activityId);
				boolean qzoneShared = PreferencesUtils.getBoolean(this,
						ACTIVITY_PREFERENCE_NAME,
						ACTIVITY_PREFERENCE_KEY_QZONE + "_"
								+ activityNewData.activityId);
				Intent shareIntent = new Intent(this, ShareActivity.class);
				shareIntent.putExtra("activityId", activityNewData.activityId);
				shareIntent.putExtra("activityName",
						activityNewData.activityName);
				shareIntent.putExtra("content", nameTxt.getText().toString());
				shareIntent.putExtra("videoPath", activityNewData.videoPath);
				shareIntent.putExtra("totalTimes", activityNewData.totalTimes);
				shareIntent.putExtra("playPic", activityNewData.playPic);
				shareIntent
						.putExtra("activityPic", activityNewData.activityPic);
				shareIntent.putExtra("sina_canShare", !sinaShared);
				shareIntent.putExtra("weixin_canShare", !wxShared);
				shareIntent.putExtra("circle_canShare", !cShared);
				shareIntent.putExtra("qzone_canShare", !qzoneShared);
				startActivityForResult(shareIntent, REQUEST_SHARE);
			}
			break;
			case R.id.vip_award:
				if (UserNow.current().userID>0 && UserNow.current().isVip
						&& !PreferencesUtils.getBoolean(this, ACTIVITY_PREFERENCE_NAME,
					ACTIVITY_PREFERENCE_KEY + "_vip_"
							+ activityNewData.activityId, false)){
					PreferencesUtils.putBoolean(this, ACTIVITY_PREFERENCE_NAME,
							ACTIVITY_PREFERENCE_KEY + "_vip_"
									+ activityNewData.activityId, true);
					vipText.setEnabled(false);
					vipText.setText("VIP抽奖 +0");
					goToGuaActivity();
				} else if (UserNow.current().userID<=0){
					startActivity(new Intent(ActivityActivity.this,LoginActivity.class));
				}
				break;
			case R.id.vip_intro:
				startActivity(new Intent(this,MyVipInfoActivity.class));
				break;
		default:
			break;
		}

	}

	private void goToGuaActivity() {
		Intent intent = new Intent(this, ActivityGuaActivity.class);
		intent.putExtra("activityId", activityNewData.activityId);
		intent.putExtra("activityName",
                activityNewData.activityName);
		intent.putExtra("videoPath", activityNewData.videoPath);
		intent.putExtra("playPic", activityNewData.playPic);
		intent.putExtra("activityPic", activityNewData.activityPic);
		intent.putExtra("isTemp",isTemp);
		startActivity(intent);
	}

	private static final int REQUEST_PLAY = 1;
	private static final int REQUEST_SHARE = 2;

	private void openPlayerActivity() {
		if (activityNewData.activityId > 0) {
			Intent intent = new Intent();
			if (TextUtils.isEmpty(activityNewData.videoPath)) {
				intent.setClass(this, WebAvoidActivity.class);
				intent.putExtra("url", activityNewData.url);
			} else {
				intent.setClass(this, PlayerActivity.class);
				intent.putExtra("path", activityNewData.videoPath);
			}
			intent.putExtra("title", activityNewData.activityName);
			intent.putExtra("playType", 4);
			intent.putExtra("activityId", activityNewData.activityId);
			if (activityNewData.pics != null && activityNewData.pics.size() > 0) {
				intent.putExtra("activityPic", activityNewData.pics.get(0));
			}
			startActivityForResult(intent, REQUEST_PLAY);
			if (activityNewData.state != ActivityData.STATE_OVER) {
				if (activityNewData.joinStatus != ActivityData.JOIN_STATUS_FINISHED) {
					VolleyActivityRequest.newActivityGetbadge(this, null, this,
							activityNewData.activityId);
//					if (!isTemp){
//						VolleyActivityRequest.newActivityComplete(this, null, this,
//								activityNewData.activityId);
//					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PLAY) {
			String hasPlayKey = ACTIVITY_PREFERENCE_KEY_PLAY + "_"
					+ String.valueOf(activityNewData.activityId);
			if (activityNewData.state != ActivityData.STATE_OVER
					&& activityNewData.joinStatus != ActivityData.JOIN_STATUS_FINISHED
					&& !PreferencesUtils.getBoolean(this,
							ACTIVITY_PREFERENCE_NAME, hasPlayKey)) {
				if (isTemp){
					if (activityNewData.joinStatus == ActivityData.JOIN_STATUS_UNJOIN
					&& !PreferencesUtils.getBoolean(this,"dw","duanwu_play"+UserNow.current().userID,false)){
						PreferencesUtils.putBoolean(this,"dw","duanwu_play"+UserNow.current().userID,true);
						chanceCount++;
						DialogUtil.updateScoreToast("抽奖次数+1");
						playImgView.setImageResource(R.drawable.activity_pressed);
						playTxt.setText(R.string.play_again);
						lotteryImgView.setImageResource(R.drawable.activity_lottery_bg);
						lotteryTimes.setText(getString(R.string.activity_lottery,
								chanceCount));
					}else {
						playImgView.setImageResource(R.drawable.activity_pressed);
						playTxt.setText(R.string.play_again);
						return;
					}
					return;
				}
				chanceCount++;
				DialogUtil.updateScoreToast("抽奖次数+1");
				PreferencesUtils.putBoolean(this, ACTIVITY_PREFERENCE_NAME,
						hasPlayKey, true);
				PreferencesUtils.putInt(this, ACTIVITY_PREFERENCE_NAME,
						ACTIVITY_PREFERENCE_KEY + "_"
								+ activityNewData.activityId, chanceCount);
			}
			playImgView.setImageResource(R.drawable.activity_pressed);
			playTxt.setText(R.string.play_again);
		} else if (requestCode == REQUEST_SHARE) {
			if (resultCode == RESULT_OK) {
				DialogUtil.updateScoreToast("抽奖次数+1");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	ResultParser shareParser = new ResultParser();

	private void shareRequest() {
		VolleyHelper.post(new ShareRequest(ShareRequest.TYPE_ACTIVITY,
				(int)activityNewData.activityId).make(), new ParseListener(
				shareParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
//				if (shareParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//					if (parser.userInfo.point > 0) {
//						UserNow.current().setTotalPoint(
//								parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//					}
//				}
			}
		}, null);
	}

	@Override
	public void reloadData() {
		getBadgeDetail();
	}

	private FocusGallery picView;
	private LinearLayout picStarsLayout;
	private TextView picTitle;
	private static final int MSG_REFRESH_GALLERY = 1;
	private static final int DELAY_DURATION = 6000;
	private RecommendImageAdapter imageAdapter;
	private int currentPagePosition = 0;
	private View starCacheView;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_GALLERY: {
				if (picView != null && imageAdapter != null
						&& imageAdapter.getCount() > 0) {
					picView.setSelection((currentPagePosition + 1)
							% imageAdapter.getCount());
				}
				sendMsg(MSG_REFRESH_GALLERY, DELAY_DURATION);
				break;
			}
			default:
				break;
			}
		}
	};

	protected void initStarsLayout() {
		picTitle = (TextView) findViewById(R.id.pic_title);
		picView = (FocusGallery) findViewById(R.id.pic_view);
		picView.requestDisallowInterceptTouchEvent(true);
		// picView.setAnimationDuration(0);
		picView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				position = (int) arg0.getAdapter().getItemId(position);
				currentPagePosition = position;
				refreshStarsLayout(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		picView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.removeMessages(MSG_REFRESH_GALLERY);
					break;
				case MotionEvent.ACTION_UP:
					handler.sendEmptyMessageDelayed(MSG_REFRESH_GALLERY, 3000);
					break;

				default:
					break;
				}
				return false;
			}
		});
		picStarsLayout = (LinearLayout) findViewById(R.id.pic_star);
	}

	protected void updateStarsLayout(ArrayList<String> picList) {
		if (picList != null && picList.size() > 0) {
			imageAdapter = new RecommendImageAdapter(picList, this);
			picView.setAdapter(imageAdapter);
			int bigPicSize = picList.size();
			if (bigPicSize > 1) {
				picStarsLayout.removeAllViews();
				for (int i = 0; i < bigPicSize; i++) {
					FrameLayout frame = (FrameLayout) getLayoutInflater()
							.inflate(R.layout.rcmd_pic_star, null);
					frame.setTag(i);
					picStarsLayout.addView(frame);
				}
				refreshStarsLayout(0);
				handler.removeMessages(MSG_REFRESH_GALLERY);
				sendMsg(MSG_REFRESH_GALLERY, DELAY_DURATION);
			}
		} else {
			picView.setVisibility(View.INVISIBLE);
		}
	}

	private void sendMsg(int what, int delay) {
		Message msg = handler.obtainMessage(what);
		handler.sendMessageDelayed(msg, delay);
	}

	private ArrayList<String> picTitleList = new ArrayList<String>();

	private void refreshStarsLayout(int position) {
		if (picStarsLayout.getChildCount() <= position) {
			return;
		}
		if (starCacheView != null) {
			starCacheView.setSelected(false);
		}
		starCacheView = picStarsLayout.getChildAt(position);
		starCacheView.setSelected(true);
		picTitle.setText(picTitleList.get(position));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(MSG_REFRESH_GALLERY);
		activitySp.unregisterOnSharedPreferenceChangeListener(this);
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.contains(ACTIVITY_PREFERENCE_KEY)) {
			chanceCount = activitySp.getInt(ACTIVITY_PREFERENCE_KEY + "_"
					+ activityNewData.activityId, 0);
			if (chanceCount < 0) {
				chanceCount = 0;
				PreferencesUtils.putInt(this, ACTIVITY_PREFERENCE_NAME,
						ACTIVITY_PREFERENCE_KEY + activityNewData.activityId,
						chanceCount);
				lotteryTimes.setText(getString(R.string.activity_lottery,
						chanceCount));
			} else {
				if (canJoinCount < chanceCount) {
					chanceCount = canJoinCount;
					Toast.makeText(this, "您已获得最多抽奖次数", Toast.LENGTH_SHORT)
							.show();
				}
				lotteryTimes.setText(getString(R.string.activity_lottery,
						chanceCount));
			}
		}

	}

	@Override
	public void activityGetBadgeResult(int errCode,
			ArrayList<BadgeData> badgeList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (badgeList.size() > 0) {
				DialogUtil.showBadgeAddToast(this, badgeList.get(0).name);
			}
		}

	}

	@Override
	public void newActivityCompleteResult(int errCode) {

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.newActivityGetBadge);
		VolleyHelper.cancelRequest(Constants.newActivityComplete);
	}
	public void onEvent(EventMessage msg){
		if (msg.name.equals("ActivityActivity")){
			if (isTemp && PreferencesUtils.getBoolean(this,"dw","duanwu_award"+UserNow.current().userID,false)){
				lotteryImgView.setImageResource(R.drawable.activity_pressed);
				chanceCount = 0 ;
				lotteryTimes.setText(getString(R.string.activity_lottery,
						chanceCount));
				VolleyActivityRequest.newActivityComplete(this, null, this,
						activityNewData.activityId);
			}
		}
	}
	public void onEvent(UserInfoEvent event){
		if (UserNow.current().userID<=0 || (UserNow.current().isVip && !PreferencesUtils.getBoolean(this,ACTIVITY_PREFERENCE_NAME,
				ACTIVITY_PREFERENCE_KEY + "_vip_"
						+ activityNewData.activityId, false))){
			vipText.setEnabled(true);
			vipText.setText("VIP抽奖 +1");
		} else {
			vipText.setEnabled(false);
			vipText.setText("VIP抽奖 +0");
		}
	}
}
