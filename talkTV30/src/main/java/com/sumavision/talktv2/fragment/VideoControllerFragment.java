package com.sumavision.talktv2.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sumavision.talktv.videoplayer.ui.MediaControllerFragment;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LiveDetailActivity;
import com.sumavision.talktv2.activity.SlidingMainActivity;
import com.sumavision.talktv2.activity.help.AlimamaHelper.OnvideoInterstitialListener;
import com.sumavision.talktv2.adapter.NetPlayDataListAdapter;
import com.sumavision.talktv2.adapter.PlayerChannelAdapter;
import com.sumavision.talktv2.adapter.PlayerChannelTypeAdapter;
import com.sumavision.talktv2.adapter.ProgramListAdapter;
import com.sumavision.talktv2.bean.AvoidUrlBean;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.NetworkLiveBean;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.dao.Remind;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.eventbus.AddRemindEvent;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.AddExpParser;
import com.sumavision.talktv2.http.json.AddExpRequest;
import com.sumavision.talktv2.http.json.AddRemindParser;
import com.sumavision.talktv2.http.json.AddRemindRequest;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ChaseDeleteParser;
import com.sumavision.talktv2.http.json.DeleteRemindRequest;
import com.sumavision.talktv2.http.json.FindSubVideoUrlParser;
import com.sumavision.talktv2.http.json.FindSubVideoUrlRequest;
import com.sumavision.talktv2.http.json.LiveDetailParser;
import com.sumavision.talktv2.http.json.LiveDetailRequest;
import com.sumavision.talktv2.http.json.LiveParser;
import com.sumavision.talktv2.http.json.LiveRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShareRequest;
import com.sumavision.talktv2.http.listener.OnChaseProgramListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.service.LiveAlertService;
import com.sumavision.talktv2.share.OnUMShareListener;
import com.sumavision.talktv2.share.ShareManager;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.RedTipTextView2;
import com.sumavision.talktv2.wxapi.SharePlatformActivity;
import com.tencent.open.utils.SystemUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.UMImage;
import com.wechat.tools.st.SpotDialogListener;
import com.wechat.tools.st.SpotManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class VideoControllerFragment extends MediaControllerFragment implements
		OnHttpErrorListener, OnItemClickListener, OnChaseProgramListener,
		OnvideoInterstitialListener, OnScrollListener {

	private long activityId;
	int playType;
	private static final int ACTIVITY_PLAY = 4;
	private String programId, programName, programPic, activityPic;
	private String titleName;
	ChaseDeleteParser cparser = new ChaseDeleteParser();
	View advertView;
	RelativeLayout nat;
	private int channelId;
	private String channelName;
	
	RelativeLayout favSourcelayout;
	ListView sourceList;
	ArrayList<NetworkLiveBean> sourceData;
	TextView sourceHeader;
	NetPlayDataListAdapter sourceAdapter;
	
	private RelativeLayout mErrLayout;
	private TextView mErrTv;
	private ProgressBar mProgressBar;
	
	LinearLayout changetvLayout;
	LinearLayout leftPartLayout;
	RelativeLayout rightPartLayout;
	ListView typeListView;
	PlayerChannelAdapter channelAdapter;
	PlayerChannelTypeAdapter typeAdapter;
	ListView programListView;
	TextView titleTxt;
	private ArrayList<String> typeName = new ArrayList<String>();
	private ArrayList<ShortChannelData> channelList = new ArrayList<ShortChannelData>();
	boolean firstLoadingChannel = true;
	
	boolean firstLoadingProgramList = true;
	private ArrayList<String> weekList = new ArrayList<String>();
	private ArrayList<CpData> cpDataList = new ArrayList<CpData>();
	PlayerChannelTypeAdapter weekAdapter;
	ProgramListAdapter programListAdapter;
	ListView weekListView;
	ListView programInfoListView;
	TextView programListTitle;
	LinearLayout programListLayout;
	LinearLayout programLeftLayout;
	RelativeLayout programRightLayout;
	ChannelData channelData;
	
	LinearLayout loadingPartLayout;
	int mProgramId;
	private boolean adPause;
	public long startTime,endTime,curStartTime;
	Timer timer;
	TimerTask task;
	TextView channelFav;
	Set<String> favs;
	@Override
	protected int getLayoutResource() {
		return R.layout.fragment_video;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		favs = PreferencesUtils.getStringSet(getActivity(),null,Constants.SP_CHANNEL_RECORD,new HashSet<String>());
		getExtras();
		playCountStatistics();
		if (UserNow.current().userID>0){
			stoprecordTime();
		}
		EventBus.getDefault().register(this);
	}

	private void startRecordTime() {
		if (UserNow.current().userID>0){
			startTime = System.currentTimeMillis();
			curStartTime = startTime;
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					endTime = System.currentTimeMillis();
					if (getActivity() != null) {
						PreferencesUtils.putInt(getActivity(), null, "videoPlayTime", getPlayTime());
					}
				}
			};
			timer.schedule(task, 60 * 1000, 60 * 1000);
		}
	}
	AddExpParser parser ;
	private void stoprecordTime(){
		if (timer != null){
			timer.cancel();
		}
		int time = PreferencesUtils.getInt(getActivity(),null,"videoPlayTime",0);
		if (time > 0 && UserNow.current().userID>0){
			parser = new AddExpParser();
			VolleyHelper.post(new AddExpRequest(getActivity(), time).make(), new ParseListener(parser) {
				@Override
				public void onParse(BaseJsonParser parser) {
					if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
						PreferencesUtils.remove(getActivity(), null, "videoPlayTime");
						EventBus.getDefault().post(new UserInfoEvent());
					}
				}
			}, null);
		}
	}


	private void getExtras() {
		Intent intent = playerActivity.getIntent();
		activityId = intent.getLongExtra("activityId", 0);
		activityPic = intent.getStringExtra("activityPic");
		programPic = intent.getStringExtra("programPic");
		programName = intent.getStringExtra("programName");
		titleName = intent.getStringExtra("title");
		liveName = intent.getStringExtra("share_program");
		playType = intent.getIntExtra(PlayerActivity.TAG_INTENT_PLAYTYPE,
				VOD_PLAY);
		channelName = intent.getStringExtra("channelName");
		if (TextUtils.isEmpty(channelName)) {
			channelName = titleName;
		}
		programId = String.valueOf(intent.getIntExtra("id", 0));
		if (activityId > 0) {
			playerActivity.activityId = activityId;
		}
		channelId = intent.getIntExtra("channelId", 0);
		mProgramId = intent.getIntExtra("id", 0);
		if(mProgramId == 0){
			mProgramId = (int) intent.getLongExtra("id", 0);
		}
	}
	
	private void playCountStatistics() {
		if (activityId == 0) {
			if (playType == LIVE_PLAY) {
				VolleyProgramRequest.playCount(getActivity(),null, 0,0,
						channelId,"", null);
			} else {
				//半屏在节目页中计数
				if (playType == VOD_PLAY && !playerActivity.isHalf){
					VolleyProgramRequest.playCount(getActivity(),null, mProgramId,playerActivity.subid,
							0,"", null);
				}else if(playType  == PlayerActivity.SHAKE_PLAY){
					//摇一摇播放量统计
					VolleyProgramRequest.shakePlayCount(null, getActivity(),mProgramId, null);
				}
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		initView(inflater);
		
		return rootView;
	}
	
	private void initView(LayoutInflater inflater) {
		initLoading();
		initChangeTvLayout();
		initChangeProgramLayout();
		initRootView();
		if (playType != PlayerActivity.SHAKE_PLAY){
			showShareBtn();
		}else{
			hideShareBtn();
		}
		addShareView(inflater);
		addAdvert(inflater);
		adVideoLayout = (RelativeLayout) rootView.findViewById(R.id.ad_video_layout);
		adVideoLayout.setVisibility(View.GONE);
//		VideoAdManager.getInstance(getActivity()).showAdVideo("adp_db062ad76c38355a508cfd7f85e41f5ea1b3a22b", mVideoAdListener, adVideoLayout, true);
	}

	String TAG = "videoController";
	void playAMGVideo(){
		adVideoLayout.setVisibility(View.GONE);
	}
	RelativeLayout adVideoLayout;
	private void initLoading() {
		initLoadingLayout();
		hideLoadingLayout();
		loadingPartLayout = ((LinearLayout) rootView.findViewById(R.id.loading_part));
		loadingPartLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideLoadingLayout();
				hideLiveRight();
				return true;
			}
		});
	}
	
	private void initChangeTvLayout() {
		typeListView = (ListView) rootView.findViewById(R.id.type);
		programListView = (ListView) rootView.findViewById(R.id.channel);
		programListView.setOnScrollListener(this);
		titleTxt = (TextView) rootView.findViewById(R.id.changetv_title);
		changetvLayout = (LinearLayout) rootView.findViewById(R.id.player_changetv_layout);
		leftPartLayout = (LinearLayout) rootView.findViewById(R.id.leftpart_layout);
		leftPartLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				changetvLayout.setVisibility(View.GONE);
				showButtonRight(true);
				return true;
			}
		});
		rightPartLayout = (RelativeLayout) rootView.findViewById(R.id.rightpart_layout);
		rightPartLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					setHideDelay();
				} else {
					cancelHideDelay();
				}
				return true;
			}
		});
		typeAdapter = new PlayerChannelTypeAdapter(getActivity(), typeName);
		channelAdapter = new PlayerChannelAdapter(getActivity(), channelList);
		typeListView.setAdapter(typeAdapter);
		programListView.setAdapter(channelAdapter);
		typeListView.setOnItemClickListener(this);
		typeListView.setOnTouchListener(this);
		programListView.setOnItemClickListener(this);
		programListView.setOnTouchListener(this);
	}
	
	private void initChangeProgramLayout() {
		weekListView = (ListView) rootView.findViewById(R.id.week);
		programInfoListView = (ListView) rootView.findViewById(R.id.program);
		programListTitle = (TextView) rootView.findViewById(R.id.programlist_title);
		programListLayout = (LinearLayout) rootView.findViewById(R.id.player_programlist_layout);
		programLeftLayout = (LinearLayout) rootView.findViewById(R.id.leftpart_layout_programlist);
		programRightLayout = (RelativeLayout) rootView.findViewById(R.id.rightpart_layout_programlist);
		programLeftLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				programListLayout.setVisibility(View.GONE);
				showButtonRight(true);
				return true;
			}
		});
		programRightLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					setHideDelay();
				} else {
					cancelHideDelay();
				}
				return true;
			}
		});
		weekAdapter = new PlayerChannelTypeAdapter(getActivity(), weekList);
		programListAdapter = new ProgramListAdapter(getActivity(), cpDataList);
		programListAdapter.setFragment(this);
		weekListView.setAdapter(weekAdapter);
		weekListView.setOnItemClickListener(this);
		weekListView.setOnTouchListener(this);
		programInfoListView.setAdapter(programListAdapter);
		programInfoListView.setOnScrollListener(this);
		programInfoListView.setOnTouchListener(this);
		programInfoListView.setOnItemClickListener(this);
	}
	
	private void initRootView() {
		rootView.findViewById(R.id.crack).setVisibility(View.GONE);
		crack = (ImageButton) rootView.findViewById(R.id.vod_crack);
		crack.setOnClickListener(this);
		favSourcelayout = (RelativeLayout) rootView
				.findViewById(R.id.rlayout_fav_source);
		changeSourceBtn = (Button) rootView
				.findViewById(R.id.imgbtn_source);
		changeTvBtn = (Button) rootView.findViewById(R.id.imgbtn_changetv);
		changeProgramBtn = (Button) rootView.findViewById(R.id.imgbtn_programlist);
		changeSourceBtn.setOnClickListener(this);
		changeTvBtn.setOnClickListener(this);
		changeProgramBtn.setOnClickListener(this);
		sourceList = (ListView) rootView.findViewById(R.id.list_source);
		sourceHeader = (TextView) rootView.findViewById(R.id.list_source_header);
		nat = (RelativeLayout) rootView.findViewById(R.id.nat);
		channelFav = (TextView) rootView.findViewById(R.id.fav_channel);
		channelFav.setVisibility(View.GONE);
		Set<String> favs = PreferencesUtils.getStringSet(getActivity(),null,Constants.SP_CHANNEL_RECORD,new HashSet<String>());
		if (favs.contains(""+channelId)){
			channelFav.setText("已收藏");
			channelFav.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.channel_collected), null, null, null);
		} else {
			channelFav.setText("收藏频道");
			channelFav.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.channel_uncollected), null, null, null);
		}
		channelFav.setOnClickListener(this);
		if (playType == CACHE_PLAY) {
			favSourcelayout.setVisibility(View.GONE);
		} else {
			favSourcelayout.setVisibility(View.GONE);
			if (PreferencesUtils.getBoolean(getActivity(), null,
					UserNow.current().userID + "_" + programId + "_fav")) {
			}
			if (UserNow.current().userID == 0
					|| playerActivity.getIntent().getBooleanExtra("hideFav",
							false)) {
			}
			if (playType == LIVE_PLAY) {
				changeProgramBtn.setVisibility(View.VISIBLE);
				changeTvBtn.setVisibility(View.VISIBLE);
				crack.setVisibility(View.GONE);
				channelFav.setVisibility(View.VISIBLE);
				playNextBtn.setVisibility(View.INVISIBLE);
				playPreBtn.setVisibility(View.INVISIBLE);
				if(playerActivity.isBackLook){
					changeSourceBtn.setEnabled(false);
				}
				setNetResource(playerActivity.playUrlList);
				showButtonRight(true);
				openController();
			} else {
				int crackType = 0;
				if (!TextUtils.isEmpty(playerActivity.getStandpath())) {
					crackType = PlayerActivity.PATH_STAND;
				} else if (!TextUtils.isEmpty(playerActivity.getHighpath())) {
					crackType = PlayerActivity.PATH_HIGH;
				} else if (!TextUtils.isEmpty(playerActivity.getSuperpath())) {
					crackType = PlayerActivity.PATH_SUPER;
				}
				updateCrack(crackType);
				showQualityBtn();
				changeSourceBtn.setVisibility(View.GONE);
			}
		}
		player.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playType != PlayerActivity.CACHE_PLAY) {
					hideYoumiAd();
				}
			}
		});
	}
	
	private void addAdvert(LayoutInflater inflater) {
		if(playType == PlayerActivity.LIVE_PLAY || playType == PlayerActivity.VOD_PLAY){
//			showYoumiAd();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stoprecordTime();
		adPause = true;
		if (player != null && player.isPlaying() && isSharing) {
			player.pause();
			setPaused(true);
			playBtn.setSelected(true);
		}
	}

	protected void addShareView(LayoutInflater inflater) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		float top = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getActivity().getResources().getDisplayMetrics());
		if (playType == LIVE_PLAY || playType == VOD_PLAY) {
			float right = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getActivity().getResources().getDisplayMetrics());
			params.setMargins(0, (int)top, (int)right, 0);
		} else {
			params.setMargins(0, (int)top, 0, 0);
		}
		View shareView = inflater.inflate(R.layout.player_share_layout, null);
		shareLayout = (LinearLayout) shareView.findViewById(R.id.share_layout);
		shareLayout.findViewById(R.id.weixin).setOnClickListener(this);
		shareLayout.findViewById(R.id.weixin_circle).setOnClickListener(this);
		shareLayout.findViewById(R.id.weibo).setOnClickListener(this);
		shareLayout.findViewById(R.id.qzone).setOnClickListener(this);
		((ViewGroup) rootView).addView(shareView, params);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (shareLayout != null) {
			shareLayout.setVisibility(View.GONE);
		}
		startRecordTime();
		if (player != null && isSharing) {
			player.resume();
			setPaused(false);
			playBtn.setSelected(false);
			isSharing = false;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.weixin) {
			MobclickAgent.onEvent(getActivity(), "bfqwx");
			cancelDelayHide();
			shareLayout.setVisibility(View.GONE);
			showButtonRight(true);
			shareToWeixin(true);
		} else if (v.getId() == R.id.weixin_circle) {
			MobclickAgent.onEvent(getActivity(), "bfqwx");
			cancelDelayHide();
			shareLayout.setVisibility(View.GONE);
			showButtonRight(true);
			shareToWeixin(false);
		}
		else if (v.getId() == R.id.weibo) {
			MobclickAgent.onEvent(getActivity(), "bfqwb");
			cancelDelayHide();
			shareLayout.setVisibility(View.GONE);
			showButtonRight(true);
			shareToWeibo();
		} else if (v.getId() == R.id.qzone) {
			MobclickAgent.onEvent(getActivity(), "bfqqq");
			cancelDelayHide();
			shareLayout.setVisibility(View.GONE);
			showButtonRight(true);
			shareToQzone();
		} else if (v.getId() == R.id.share) {
			closeAd();
		} else if (v.getId() == R.id.play_btn) {
			MobclickAgent.onEvent(getActivity(), "bofangzanting");
			if (isPlaying() && playType != PlayerActivity.CACHE_PLAY) {
//				if(!changeAdPosition && playType == LIVE_PLAY){
//					addPauseAd();
//				}
				showYoumiAd();
				adPause = true;
				stoprecordTime();
			} else if (playType != PlayerActivity.CACHE_PLAY) {
				hideYoumiAd();
				adPause = false;
				startRecordTime();
			}
		} else if (v.getId() == R.id.imgbtn_fav) {
			MobclickAgent.onEvent(getActivity(), "bfqsc");
		} else if (v.getId() == R.id.imgbtn_source) {
			MobclickAgent.onEvent(getActivity(), "bfqqxd");
			closeAd();
			hideController();
			showSourceListView();
			liveHandler.sendEmptyMessageDelayed(HIDE_RIGHT, DELAYTIME);
			showButtonRight(false);
			removeQuitMessage();
		} else if (v.getId() == R.id.vod_crack) {
			clickCrack();
		} else if (v.getId() == R.id.imgbtn_changetv) {
			closeAd();
			cancelDelayHide();
			cancelHideDelay();
			hideController();
			changetvLayout.setVisibility(View.VISIBLE);
			titleTxt.setText("正在直播：" + channelName);
			if (firstLoadingChannel) {
				showLoadingLayout();
				getChannelData();
			} else {
				int index = channelAdapter.getSelectedPos();
				programListView.setSelection(index);
				String name = channelList.size() - 1 >= index ? channelList.get(index).typeName : "";
				for (int i = 0; i < typeName.size(); i++) {
					if (name.equals(typeName.get(i))) {
						typeAdapter.setSelectedPos(i);
						typeAdapter.notifyDataSetInvalidated();
						typeListView.setSelection(i);
						break;
					}
				}
				if (channelList.size() == 0) {
					showEmptyLayout("暂无数据");
				}
				liveHandler.sendEmptyMessageDelayed(HIDE_RIGHT, DELAYTIME);
			}
			showButtonRight(false);
			removeQuitMessage();
		} else if (v.getId() == R.id.imgbtn_programlist) {
			closeAd();
			cancelDelayHide();
			cancelHideDelay();
			hideController();
			programListLayout.setVisibility(View.VISIBLE);
			programListTitle.setText(channelName + "节目单");
			firstLoadingProgramList = true;
			if (firstLoadingProgramList) {
				weekList.clear();
				cpDataList.clear();
				programListAdapter.notifyDataSetChanged();
				weekAdapter.notifyDataSetChanged();
				showLoadingLayout();
				getProgramListData();
			}
			showButtonRight(false);
			removeQuitMessage();
		} else if (v.getId() == R.id.live_btn) {
			onLiveBtnClick((Integer) v.getTag(R.id.item_pos),
					(Integer) v.getTag(R.id.item_bool), (String) v.getTag(R.id.item_back));
		}else if(v.getId() == R.id.live_tv_err){
			closeAd();
		} else if (v.getId() == R.id.fav_channel){

			String tempId = channelId +"";
			if (favs.contains(tempId)){
				favs.remove(tempId);
				ToastHelper.showToast(getActivity(), "删除频道成功");
				channelFav.setText("收藏频道");
				channelFav.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.channel_uncollected), null, null, null);
			}else {
				favs.add(tempId);
				ToastHelper.showToast(getActivity(), "添加频道成功");
				channelFav.setText("已  收  藏");
				channelFav.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.channel_collected), null, null, null);
			}
			PreferencesUtils.putStringSet(getActivity(),null,Constants.SP_CHANNEL_RECORD,favs);
		}
		super.onClick(v);
	}
	
	public void closeAd(){
		if(playType == LIVE_PLAY && nat.isShown()){
			hideYoumiAd();
		}
	}
	
	private void getProgramListData() {
		final LiveDetailParser liveParser = new LiveDetailParser(channelName);
		VolleyHelper.post(new LiveDetailRequest(Integer.parseInt(playerActivity.channelId)).make(), new ParseListener(liveParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (liveParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					firstLoadingProgramList = false;
					weekList.clear();
					cpDataList.clear();
					weekList.addAll(liveParser.weekDate);
					cpDataList.addAll(liveParser.channelData.cpList);
					channelData = liveParser.channelData;
					if (cpDataList.size() == 0) {
						showEmptyLayout("该频道暂无节目单");
					} else {
						updateProgramListView();
					}
				} else {
					hideLoadingLayout();
					showErrorLayout();
				}
			}
		}, null);
	}

	String liveName = "";
	private void updateProgramListView() {
		if (getActivity() != null) {
			for (int i = 0; i < cpDataList.size(); i++) {
				if (cpDataList.get(i).isPlaying == 0) {
					if (LiveDetailActivity.entryPosition != -1) {
						programListAdapter.setSelectedPosition(LiveDetailActivity.entryPosition);
						programInfoListView.setSelection(LiveDetailActivity.entryPosition - 1);
						LiveDetailActivity.entryPosition = -1;
					} else {
						programListAdapter.setSelectedPosition(i);
						programInfoListView.setSelection(i - 1);
					}
					liveName = programListAdapter.getItem(programListAdapter.getSelectedPosition()).name;
					for (int j = 0; j < weekList.size(); j++) {
						if (cpDataList.get(i).week.equals(weekList.get(j))) {
							weekAdapter.setSelectedPos(j);
							break;
						}
					}
					break;
				}
			}
			weekAdapter.notifyDataSetChanged();
			programListAdapter.notifyDataSetChanged();
		}
		hideLoadingLayout();
		setHideDelay();
	}
	
	private void getChannelData() {
		String umengValue = "";
		try {
			umengValue = AppUtil
					.getMetaData(getActivity(), "UMENG_CHANNEL");
		} catch (Exception e) {
			umengValue = "";
		}
		final LiveParser liveParser = new LiveParser(umengValue);
		VolleyHelper.post(new LiveRequest(getActivity()).make(), new ParseListener(liveParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (liveParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					firstLoadingChannel = false;
					channelList.addAll(liveParser.channelInfo);
					typeName.addAll(liveParser.channelList);
					if (channelList.size() == 0) {
						showEmptyLayout("暂无数据");
					} else {
						updateTypeView();
					}
				} else {
					hideLoadingLayout();
					showErrorLayout();
				}
			}
		}, null);
	}
	
	private void updateTypeView() {
		if (getActivity() != null) {
			for (int i = 0; i < channelList.size(); i++) {
				String s = channelList.get(i).channelName;
				int id = channelList.get(i).channelId;
				String type = channelList.get(i).typeName;
				if (s.equals(channelName) || id == channelId) {
					channelAdapter.setSelectedPos(i);
					programListView.setSelection(i);
					for (int j = 0; j < typeName.size(); j++) {
						if (type.equals(typeName.get(j))) {
							typeAdapter.setSelectedPos(j);
							break;
						}
					}
					break;
				}
			}
			typeAdapter.notifyDataSetChanged();
			channelAdapter.notifyDataSetChanged();
		}
		hideLoadingLayout();
		setHideDelay();
	}

	private void showSourceListView() {	
		sourceList.setVisibility(View.VISIBLE);
		sourceHeader.setVisibility(View.VISIBLE);
	}

	private void hideSourceListView() {
		sourceList.setVisibility(View.GONE);
		sourceHeader.setVisibility(View.GONE);
	}
	
	private String shareUrl;
	private String shareTitle;
	private String shareContent;
	private UMImage shareImage;
	
	private void setShareData() {
		Intent intent = playerActivity.getIntent();
		programPic = intent.getStringExtra("programPic");
		programName = intent.getStringExtra("programName");
		titleName = intent.getStringExtra("title");
		setShareTitle();
		setShareUrl();
		setShareContent();
		setShareImage();
		isSharing = true;
	}
	boolean isSharing;
	
	private void setShareTitle() {
		if (playType != ACTIVITY_PLAY) {
			if (programName != null && hasDigital(titleName)) {
				shareTitle = programName + ":" + titleName;
			} else if (playType == LIVE_PLAY) {
				if (TextUtils.isEmpty(liveName) || liveName.equals("暂无数据")){
					liveName = "";
				} else {
					liveName = "-"+liveName;
				}
				shareTitle = "我在用电视粉手机APP观看"+ titleName + liveName +"在线直播";
			} else {
				shareTitle = titleName;
			}
		} else {
			shareTitle = "看片花赢大奖  "+titleName;
		}
	}
	public String getShareTitle(){
		String result = shareTitle;
		if (playType == LIVE_PLAY) {
			if (!TextUtils.isEmpty(Constants.liveShareTitle)){
				result = Constants.liveShareTitle;
			}
		} else if (playType != ACTIVITY_PLAY){
			if (!TextUtils.isEmpty(Constants.vodShareTitle)){
				result = Constants.vodShareTitle;
			}
		}
		return result;
	}
	
	private void setShareUrl() {
		StringBuilder targetUrl = new StringBuilder(Constants.url);
		if (playType != LIVE_PLAY && playType != CACHE_PLAY && activityId == 0) {
			targetUrl.append("web/mobile/shareProgram.action?subId=");
			targetUrl.append(playerActivity.subid);
		} else {
			targetUrl = new StringBuilder("http://tvfan.cn");
		}
		shareUrl = targetUrl.toString();
	}
	
	private void setShareContent() {
		StringBuilder hintBuf = new StringBuilder();
		if (playType == LIVE_PLAY){
			hintBuf.append("推荐你用电视粉看直播！电视粉聚合国内最全央视卫视地方台直播，可观看高清流畅体育、综艺、电视剧直播。观看地址：");
		} else  if(playType == ACTIVITY_PLAY){
			hintBuf.append("看片花、赢大奖，我参加了电视粉的《")
					.append(titleName).append("》看预告片 送精美礼品活动，@电视粉 活动，天天有奖品！你也来试试手气吧！地址：");
		} else {
			hintBuf.append("我用电视粉看了");
			if (programName != null && hasDigital(titleName)){
				hintBuf.append(shareTitle);
			}else {
				hintBuf.append(titleName);
			}
			hintBuf.append("，播放效果很棒！推荐观看，@电视粉 观看地址：");
		}
		hintBuf.append(shareUrl);
		shareContent = hintBuf.toString();
	}
	
	private void setShareImage() {
		if (!TextUtils.isEmpty(programPic)) {
			shareImage = new UMImage(playerActivity, programPic);
		} else {
			if (!TextUtils.isEmpty(activityPic)) {
				shareImage = new UMImage(playerActivity, activityPic);
			} else {
				shareImage = new UMImage(playerActivity, R.drawable.icon);
			}
		}
	}

	ResultParser shareParser = new ResultParser();

	private void shareRequest() {
		ShareRequest request;
		if (playType == LIVE_PLAY){
			request = new ShareRequest(ShareRequest.TYPE_LIVE,
					channelId);
		} else {
			request = new ShareRequest(ShareRequest.TYPE_PROGRAM,
					Integer.parseInt(programId));
		}
		VolleyHelper.post(request.make(), new ParseListener(
				shareParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				share.setRedTip(RedTipTextView2.RED_TIP_GONE);
				ToastHelper.showToast(getActivity(), "有奖分享成功，请在首页查看中奖规则");
//				if (shareParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//					if (parser.userInfo.point > 0) {
//						UserNow.current().setTotalPoint(
//								parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//					}
//				}
			}
		}, null);
	}


	private void shareToWeixin(boolean friend) {
		setShareData();
		ShareManager.getInstance(getActivity()).weixinShare(friend, shareUrl,
				getShareTitle(), shareContent, shareImage, new OnUMShareListener() {
					@Override
					public void umShareResult(String platform,
											  boolean shareSucc, boolean cancel) {
						if (!shareSucc) {
							Toast.makeText(playerActivity,
									R.string.share_failed, Toast.LENGTH_SHORT)
									.show();
						} else {
							shareRequest();
						}
					}
				});
	}
	
	private void shareToQzone() {
		setShareData();
		if (!SystemUtils.checkMobileQQ(getActivity())) {
			Toast.makeText(getActivity(), "未安装QQ客户端，请先安装QQ", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (getActivity() != null && !getActivity().isFinishing()) {
			ShareManager.getInstance(getActivity()).qzoneShare(getShareTitle(), shareContent,
					shareImage, shareUrl, new OnUMShareListener() {
						@Override
						public void umShareResult(String platform, boolean shareSucc, boolean cancel) {
							if (shareSucc){
								shareRequest();
							}
						}
					});
		}
	}
	
	private void shareToWeibo() {
		setShareData();
		Intent intent = new Intent();
		intent.setClass(getActivity(), SharePlatformActivity.class);
		intent.putExtra("type", SharePlatformActivity.TYPE_SINA);
		intent.putExtra("targetUrl", shareUrl);
		intent.putExtra("activityPic", TextUtils.isEmpty(activityPic)?programPic:activityPic);
		if (playType == LIVE_PLAY){
			intent.putExtra("programName",getShareTitle() +"    "+ shareContent);
		} else {
			intent.putExtra("programName", shareContent);
		}
		getActivity().startActivity(intent);
	}

	@Override
	protected void hideController() {
		favSourcelayout.setVisibility(View.GONE);
		sourceList.setVisibility(View.GONE);
		sourceHeader.setVisibility(View.GONE);
		super.hideController();
	}

	@Override
	protected void openController() {
		if (playType != CACHE_PLAY && activityId == 0) {
			crackLayout.setVisibility(View.GONE);
			favSourcelayout.setVisibility(View.VISIBLE);
		}
		if (playType != PlayerActivity.CACHE_PLAY && playerActivity.isPrepared) {
			if (adPause){
				
			}else{
				hideYoumiAd();
			}
		}
		sourceList.setVisibility(View.GONE);
		sourceHeader.setVisibility(View.GONE);
		super.openController();
		if (playType == CACHE_PLAY) {
			sourcelayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void updateUI() {
	}

	@Override
	public void onError(int code) {

	}

	@Override
	protected void setCrackBackground(int crackType) {
		updateCrack(crackType);
		playerActivity.changePath(crackType);
	}
	
	private void updateCrack(int crackType) {
		if (!TextUtils.isEmpty(playerActivity.getStandpath())) {
			crackTop.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(playerActivity.getHighpath())) {
			crackMiddle.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(playerActivity.getSuperpath())) {
			crackBottom.setVisibility(View.VISIBLE);
		}
		crackLayout.setVisibility(View.GONE);
		if (crackType == PlayerActivity.PATH_STAND) {
			crack.setImageResource(R.drawable.standard_selected);
			crackTop.setVisibility(View.GONE);
		}
		if (crackType == PlayerActivity.PATH_HIGH) {
			crack.setImageResource(R.drawable.higher_selected);
			crackMiddle.setVisibility(View.GONE);
		}
		if (crackType == PlayerActivity.PATH_SUPER) {
			crack.setImageResource(R.drawable.super_selected);
			crackBottom.setVisibility(View.GONE);
		}
		crack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/**
		 * change live source
		 */
		liveHandler.removeMessages(HIDE_RIGHT);
		if (parent.getId() == sourceList.getId()) {
			//切换网络来源
			if (position == sourceAdapter.getSelectedPosition()) {
				return;
			}
			sourceList.setSelection(position);
			sourceAdapter.setSelectedPosition(position);
			sourceAdapter.notifyDataSetInvalidated();
			changeSourceBtn.setText(sourceData.get(position).netLiveText);
			hideSourceListView();
			playerActivity.changeVideo = true;
			playerActivity.getIntent().putExtra("livepos", position);
			playerActivity.showUrl = playerActivity.playUrlList.get((int)id).showUrl;
			setResourceText(playerActivity.playUrlList.get((int) id).url,
					playerActivity.playUrlList.get((int) id).videoPath);
//			playerActivity.getIntent().putExtra("url",
//					playerActivity.playUrlList.get((int) id).url);
//			playerActivity.getIntent().putExtra("videoPath",
//					playerActivity.playUrlList.get((int) id).videoPath);
			playerActivity.getIntent().putExtra("isBackLook", false);
			playerActivity.lineNumber = position;
			playerActivity.setStartTextLine(position);
//			if(playerActivity.isPrepared){
//				player.stopPlayback();
//			}else{
//			}
			if (player.isPlaying()) {
				player.stopPlayback();
			} else {
				playerActivity.restartPlay(playerActivity.getIntent());
			}
		} else if (parent.getId() == typeListView.getId()) {
			String name = typeName.get(position);
			for (int i = 0; i < channelList.size(); i++) {
				ShortChannelData data = channelList.get(i);
				if (name.equals(data.typeName)) {
					programListView.setSelection(i);
					break;
				}
			}
			typeAdapter.setSelectedPos(position);
			typeAdapter.notifyDataSetInvalidated();
		} else if (parent.getId() == programListView.getId()) {
			if (position == channelAdapter.getSelectedPos()) {
				return;
			}
			if (channelList.get(position).netPlayDatas == null
					|| channelList.get(position).netPlayDatas.size() == 0) {
				Toast.makeText(getActivity(), "暂无可用视频源", Toast.LENGTH_SHORT).show();
				return;
			}
			weekList.clear();
			cpDataList.clear();
			weekAdapter.notifyDataSetChanged();
			programListAdapter.notifyDataSetChanged();
			
			changeSourceBtn.setEnabled(true);
			channelAdapter.setSelectedPos(position);
			channelAdapter.notifyDataSetInvalidated();
			channelName = channelList.get(position).channelName;
			liveName = channelList.get(position).programName;
			titleTxt.setText("正在直播：" + channelName);
			playerActivity.changeVideo = true;
			playerActivity.getIntent().putExtra("isBackLook", false);
			playerActivity.getIntent().putExtra("livepos", 0);
			playerActivity.getIntent().putExtra("title", channelName);
//			playerActivity.getIntent().putExtra("videoPath",
//					channelData.netPlayDatas.get(0).videoPath);
//			playerActivity.getIntent().putExtra("url",
//					channelData.netPlayDatas.get(0).url);
			playerActivity.getIntent().putExtra("channelId", channelList.get(position).channelId);
			setNetResource(channelList.get(position).netPlayDatas);
			Bundle bundle = new Bundle();
			bundle.putSerializable("NetPlayData", channelList.get(position).netPlayDatas);
			playerActivity.getIntent().putExtras(bundle);
//			if(playerActivity.isPrepared){
//				player.stopPlayback();
//			}else{
//			}
				player.stopPlayback();
				playerActivity.restartPlay(playerActivity.getIntent());
			playerActivity.lineNumber = 0;
			playerActivity.setStartTextLine(0);
			changetvLayout.setVisibility(View.GONE);
			firstLoadingProgramList = true;
			channelId = channelList.get(position).channelId;
			if (favs.contains(channelId+"")){
				channelFav.setText("已收藏");
				channelFav.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.channel_collected), null, null, null);
			} else {
				channelFav.setText("收藏频道");
				channelFav.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.channel_uncollected), null, null, null);
			}
		} else if (parent.getId() == weekListView.getId()) {
			String weekname = weekList.get(position);
			for (int i = 0; i < cpDataList.size(); i++) {
				CpData data = cpDataList.get(i);
				if (weekname.equals(data.week)) {
					programInfoListView.setSelection(i);
					break;
				}
			}
			weekAdapter.setSelectedPos(position);
			weekAdapter.notifyDataSetInvalidated();
		} else if (parent.getId() == programInfoListView.getId()) {
			CpData data = programListAdapter.getItem(position);
			onLiveBtnClick(position, data.isPlaying, data.backUrl);
		}
	}

	@Override
	public void chaseResult(int errCode) {
		if (getActivity() != null) {
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				PreferencesUtils.putBoolean(getActivity(), null,
						UserNow.current().userID + "_" + programId + "_fav",
						true);
				DialogUtil.alertToast(getActivity(),
						getString(R.string.zhuijuu));
			} else {
				DialogUtil.alertToast(getActivity(),
						getString(R.string.chase_failed));
			}
		}

	}

	boolean adClosed;
	@Override
	public void onClickClose() {
		if (advertView != null) {
			advertView.setVisibility(View.GONE);
			nat.setVisibility(View.GONE);
			adClosed = true;
		}
	}

	@Override
	public void onReady() {
		if (advertView != null) {
			advertView.setVisibility(View.VISIBLE);
			nat.setVisibility(View.VISIBLE);
		}
	}
	
	protected void initLoadingLayout() {
		mErrLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		mErrLayout.setBackgroundResource(R.drawable.live_right_bg);
		mErrTv = (TextView) rootView.findViewById(R.id.err_text);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		mErrLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	protected void showLoadingLayout() {
		if (mErrLayout != null) {
			mErrLayout.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mErrTv.setVisibility(View.GONE);
		}
		if (loadingPartLayout != null) {
			loadingPartLayout.setVisibility(View.VISIBLE);
		}
	}

	protected void hideLoadingLayout() {
		if (mErrLayout != null) {
			mErrLayout.setVisibility(View.GONE);
		}
		if (loadingPartLayout != null) {
			loadingPartLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 加载失败
	 */
	protected void showErrorLayout() {
		if (mErrLayout != null) {
			mErrLayout.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mErrTv.setVisibility(View.VISIBLE);
		}
		if (loadingPartLayout != null) {
			loadingPartLayout.setVisibility(View.VISIBLE);
		}
	}
	
	protected void showEmptyLayout(String emptyTip) {
		mErrLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mErrTv.setVisibility(View.VISIBLE);
		mErrTv.setClickable(false);
		mErrTv.setCompoundDrawables(null, null, null, null);
		mErrTv.setText(emptyTip);
		if (loadingPartLayout != null) {
			loadingPartLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			int firstVisibleItem = view.getFirstVisiblePosition();
			int lastVisibleItem = view.getLastVisiblePosition();
			int size = 0;
			if (channelList != null && view.getId() == programListView.getId()) {
				size = channelList.size();
			} else {
				size = cpDataList.size();
			}
			if (size > 0) {
				if (lastVisibleItem == view.getCount() - 1) {
					if (view.getId() == programListView.getId()) {
						typeAdapter.setSelectedPos(typeName.size() - 1);
						typeAdapter.notifyDataSetInvalidated();
						typeListView.setSelection(typeName.size() - 1);
					} else {
						weekAdapter.setSelectedPos(weekList.size() - 1);
						weekAdapter.notifyDataSetInvalidated();
						weekListView.setSelection(weekList.size() - 1);
					}
					return;
				}
				if (firstVisibleItem < size && firstVisibleItem != 0) {
					if (view.getId() == programListView.getId()) {
						ShortChannelData data = channelList.get(firstVisibleItem);
						String name = data.typeName;
						for (int i = 0; i < typeName.size(); i++) {
							if (name.equals(typeName.get(i))) {
								typeAdapter.setSelectedPos(i);
								typeListView.setSelection(i);
								typeAdapter.notifyDataSetInvalidated();
							}
						}
					} else {
						CpData cpdata = cpDataList.get(firstVisibleItem);
						String week = cpdata.week;
						for (int i = 0; i < weekList.size(); i++) {
							if (week.equals(weekList.get(i))) {
								weekAdapter.setSelectedPos(i);
								weekListView.setSelection(i);
								weekAdapter.notifyDataSetInvalidated();
							}
						}
					}
				} else if (firstVisibleItem < size && firstVisibleItem == 0) {
					if (view.getId() == programListView.getId()) {
						typeAdapter.setSelectedPos(0);
						typeListView.setSelection(0);
						typeAdapter.notifyDataSetInvalidated();
					} else {
						weekAdapter.setSelectedPos(0);
						weekListView.setSelection(0);
						weekAdapter.notifyDataSetInvalidated();
					}
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	
	//下面是预约控制相关代码
	int remindPosition;
	String lastBackUrl = "";
	public void onLiveBtnClick(int position, int flag, String backUrl) {
		CpData cpData = programListAdapter.getItem(position);
		lastBackUrl = playerActivity.getIntent().getStringExtra("url");
		if (!TextUtils.isEmpty(backUrl) && flag == CpData.TYPE_REVIEW
				&& !lastBackUrl.equals(backUrl)) {
			programListAdapter.setSelectedPosition(position);
			programListAdapter.notifyDataSetInvalidated();
			playerActivity.lineNumber = 0;
			playerActivity.setStartTextLine(playerActivity.lineNumber);
			changeSourceBtn.setEnabled(false);
			playerActivity.changeVideo = true;
			playerActivity.getIntent().putExtra("isBackLook", true);
			playerActivity.getIntent().putExtra("path", backUrl);
			playerActivity.getIntent().putExtra("url", backUrl);
			playerActivity.getIntent().putExtra("title", cpData.name);
			liveName = cpData.name;
//			if(playerActivity.isPrepared){
//				player.stopPlayback();
//			}else{
//			}
				player.stopPlayback();
				playerActivity.restartPlay(playerActivity.getIntent());
			return;
		}
		if (flag == CpData.TYPE_LIVE) {
			if (programListAdapter.getSelectedPosition() == position) {
				return;
			}
			programListAdapter.setSelectedPosition(position);
			programListAdapter.notifyDataSetInvalidated();
			channelName = cpData.channelName;
			playerActivity.changeVideo = true;
			playerActivity.getIntent().putExtra("isBackLook", false);
			playerActivity.getIntent().putExtra("livepos", 0);
			playerActivity.getIntent().putExtra("title", channelName);
			playerActivity.getIntent().putExtra("channelName", channelName);
//			playerActivity.getIntent().putExtra("videoPath",
//					channelData.netPlayDatas.get(0).videoPath);
//			playerActivity.getIntent().putExtra("url",
//					channelData.netPlayDatas.get(0).url);
			Bundle bundle = new Bundle();
			bundle.putSerializable("NetPlayData", channelData.netPlayDatas);
			setNetResource(channelData.netPlayDatas);
			playerActivity.getIntent().putExtras(bundle);
//			if(playerActivity.isPrepared){
//				player.stopPlayback();
//			} else {
//			}
				player.stopPlayback();
				playerActivity.restartPlay(playerActivity.getIntent());
			playerActivity.lineNumber = 0;
			playerActivity.setStartTextLine(0);
			changeSourceBtn.setEnabled(true);
		}
		if (flag == CpData.TYPE_UNPLAY) {
			// 预约或者取消预约
			if (UserNow.current().userID == 0) {
				Toast.makeText(getActivity(), "请先登录，再预约", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (cpData.order == 1) {
					deleteRemind(UserNow.current().userID,
							(int) cpData.remindId);
					remindPosition = position;
				} else {
					addRemind(UserNow.current().userID, cpData.id);
					remindPosition = position;
				}
			}
		}
	}
	
	private void addRemind(int userId, int cpId) {
		showLoadingLayout();
		final AddRemindParser addparser = new AddRemindParser();
		VolleyHelper.post(new AddRemindRequest(userId, cpId).make(),
				new ParseListener(addparser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						hideLoadingLayout();
						if (addparser.errCode == JSONMessageType.SERVER_CODE_OK) {
							DialogUtil.alertToast(getActivity(), "预约成功!");
							CpData cpData = programListAdapter
									.getItem(remindPosition);
							cpData.order = 1;
							cpData.remindId = addparser.remindId;
							programListAdapter.notifyDataSetChanged();
							VodProgramData program = cpToProgram();
							Remind.get(program).save();
							Intent alertServcice = new Intent(getActivity(),
									LiveAlertService.class);
							getActivity().startService(alertServcice);
							remindPosition = -1;
							EventBus.getDefault().post(new AddRemindEvent());
						} else {
							DialogUtil.alertToast(getActivity(),
									addparser.errMsg + "");
						}
					}
				}, null);
	}
	
	private void deleteRemind(int userId, int remindId) {
		showLoadingLayout();
		final ResultParser rparser = new ResultParser();
		VolleyHelper.post(new DeleteRemindRequest(userId, String.valueOf(remindId)).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					DialogUtil.alertToast(getActivity(), "取消预约成功!");
					CpData cpData = programListAdapter.getItem(remindPosition);
					cpData.order = 0;
					programListAdapter.notifyDataSetChanged();
					DataSupport.deleteAll(Remind.class,"cpid = ?",
							programListAdapter.getItem(remindPosition).id+"");
					remindPosition = -1;
					Intent alertServcice = new Intent(getActivity(), LiveAlertService.class);
					getActivity().startService(alertServcice);
					EventBus.getDefault().post(new AddRemindEvent());
				} else {
					DialogUtil.alertToast(getActivity(), "取消预约失败!");
				}
			}
		}, null);
	}
	
	private VodProgramData cpToProgram() {
		CpData cp = programListAdapter.getItem(remindPosition);
		VodProgramData program = new VodProgramData();
		program.cpId = cp.id;
		program.channelId = String.valueOf(channelId);
		program.cpName = cp.name;
		program.channelName = titleName;
		program.channelLogo = "";
		program.cpDate = cp.date;
		program.startTime = cp.startTime;
		return program;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == programListView.getId() 
				|| v.getId() == typeListView.getId()
				|| v.getId() == weekListView.getId()
				|| v.getId() == programInfoListView.getId()) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				liveHandler.sendEmptyMessageDelayed(HIDE_RIGHT, DELAYTIME);
			} else {
				liveHandler.removeMessages(HIDE_RIGHT);
			}
			return false;
		}
		if(playType == LIVE_PLAY && nat.isShown() && !adClosed){
			onSingleTapUp(event);
			return false;
		}
		return super.onTouch(v, event);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		if(playType == LIVE_PLAY && !playerActivity.isPrepared){
			favSourcelayout.setVisibility(barShow?View.GONE:View.VISIBLE);
		}
		return super.onSingleTapUp(arg0);
	}
	
	public void hideLiveRight() {
		changetvLayout.setVisibility(View.GONE);
		programListLayout.setVisibility(View.GONE);
		hideSourceListView();
		hideLoadingLayout();
		liveHandler.removeMessages(HIDE_RIGHT);
		showButtonRight(true);
	}
	
	protected final int HIDE_RIGHT = 1;
	private final int DELAYTIME = 6000;
	Handler liveHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HIDE_RIGHT:
				hideLiveRight();
				break;

			default:
				break;
			}
		};
	};
	
	public void setHideDelay() {
		if (liveHandler != null) {
			liveHandler.sendEmptyMessageDelayed(HIDE_RIGHT, DELAYTIME);
		}
	}
	
	public void cancelHideDelay() {
		if (liveHandler != null) {
			liveHandler.removeMessages(HIDE_RIGHT);
		}
	}
	
	@Override
	public void setNetLine(int position) {
		position = position % playerActivity.maxLine;
		if (sourceList != null && sourceAdapter != null && sourceData != null
				&& sourceData.size() - 1 >= position) {
			sourceList.setSelection(position);
			sourceAdapter.setSelectedPosition(position);
			sourceAdapter.notifyDataSetInvalidated();
			changeSourceBtn.setText(sourceData.get(position).netLiveText);
		}
	}
	
	private boolean hasDigital(String s) {
		if (TextUtils.isEmpty(s)) {
			return false;
		} else {
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
					return true;
				}
			}
		}
		return false;
	}
	
	private void setNetResource(List<NetPlayData> netlist) {
		int liveSize = 0;
		if (netlist != null) {
			liveSize = netlist.size();
		}
		if (liveSize > 0) {
			changeSourceBtn.setVisibility(View.VISIBLE);
		} else {
			changeSourceBtn.setVisibility(View.GONE);
		}
		sourceData = AvoidUrlBean.getInstance().getLiveList(
				liveSize);
		sourceAdapter = new NetPlayDataListAdapter(
				getActivity(), sourceData);
		sourceList.setAdapter(sourceAdapter);
		sourceList.setOnItemClickListener(this);
        int tempPos = playerActivity.getIntent().getIntExtra("livePos",0);
        if (tempPos<liveSize && tempPos<5){
            sourceList.setSelection(tempPos);
            changeSourceBtn.setText("网络"+(tempPos+1));
        }else {
            sourceList.setSelection(0);
            changeSourceBtn.setText("网络1");
        }
	}
	public int getPlayTime(){
		int temp =(int) (endTime - startTime);
		temp = temp/1000/60;
		return temp >0 ? temp:0;
	}
	/*
	获取当前视频播放时间，用于播放信息统计
	 */
	@Override
	public void setCurPlayTime(){
		long temp = System.currentTimeMillis();
		curPlayTime =  (temp - curStartTime)/1000;
		curStartTime = temp;
	}

	
	private void showYoumiAd() {
		if (playerActivity != null && PreferencesUtils.getBoolean(getActivity(), null, "hasPauseAd", true)
				&& !playerActivity.isHalf) {
			SpotManager.getInstance(playerActivity).showSpotAds(playerActivity, new SpotDialogListener() {
				@Override
				public void onShowSuccess() {
					AdStatisticsUtil.adCount(getActivity(), Constants.zanting);
				}

				@Override
				public void onShowFailed() {
				}

				@Override
				public void onSpotClosed() {
				}

				@Override
				public void onSpotClick() {

				}
			});

		}
	}
	
	private void hideYoumiAd() {
		if (playerActivity != null && PreferencesUtils.getBoolean(getActivity(), null, "hasPauseAd", true)) {
			SpotManager.getInstance(playerActivity).onStop();
		}
	}
	
	@Override
	public void onDestroy() {
		VolleyHelper.cancelRequest("playVideo");
		VolleyHelper.cancelRequest(Constants.chaseAdd);
		VolleyHelper.cancelRequest(Constants.chaseDelete);
		VolleyHelper.cancelRequest(Constants.channelContent);
		VolleyHelper.cancelRequest(Constants.channelTypeDetailList);
		VolleyHelper.cancelRequest(Constants.remindAdd);
		VolleyHelper.cancelRequest(Constants.remindDelete);
		VolleyHelper.cancelRequest(Constants.findSubVideoUrl);
		VolleyHelper.cancelRequest(Constants.addExp);
		if (playType != PlayerActivity.CACHE_PLAY) {
			hideYoumiAd();
		}
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	@Override
	public void goHome() {
		super.goHome();
		startActivity(new Intent(getActivity(), SlidingMainActivity.class));
	}
	@Override
	public void dismissAd(){
		hideYoumiAd();
	}
	public FindSubVideoUrlParser subVideoParser = new FindSubVideoUrlParser();
	@Override
	public void getSubVideoInfo(int programId,int subId,int position, final int flag, final String version){
		VolleyHelper.post(new FindSubVideoUrlRequest(programId, subId, position).make(), new ParseListener(subVideoParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (subVideoParser.subVideoData != null) {
					playerActivity.getDataError = false;
					subVideoParser.subVideoData.positionFlag = flag;
					subVideoParser.subVideoData.recVersion = version;
					EventBus.getDefault().post(subVideoParser.subVideoData);
				} else {
					playerActivity.getDataError = true;
					playerActivity.resetLoadingLogo(3);
				}
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				playerActivity.getDataError = true;
				playerActivity.resetLoadingLogo(3);
			}
		});
	}
	@Override
	public void getSubVideoInfo(int programId,int subId,int position, final int flag){
		getSubVideoInfo(programId, subId, position, flag, "");
	}

	public void onEvent(EventMessage msg){
		if (msg.name.equals("video_share_sina")){
			shareRequest();
		} else if (msg.name.equals("video_letv_crack")){
			final EventMessage resultMsg = new EventMessage("player_letv_crack_result");
			VolleyQueueManage.getRequestQueue().add(new StringRequest(msg.bundle.getString("url"),
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							try {
								String result = new JSONObject(response).optString("location");
								resultMsg.bundle.putString("result",result);
								EventBus.getDefault().post(resultMsg);
							} catch (JSONException e) {
								e.printStackTrace();
								resultMsg.bundle.putString("result","");
								EventBus.getDefault().post(resultMsg);
							}
						}
					},new Response.ErrorListener(){

				@Override
				public void onErrorResponse(VolleyError error) {
					resultMsg.bundle.putString("result","");
					EventBus.getDefault().post(resultMsg);
				}
			}));
		}
	}
}
