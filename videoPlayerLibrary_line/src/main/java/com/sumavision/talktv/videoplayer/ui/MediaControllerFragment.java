package com.sumavision.talktv.videoplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.activity.MainWebPlayActivity;
import com.sumavision.talktv.videoplayer.activity.StbSearchActivity;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.RedTipTextView;
import com.sumavision.talktv2.widget.RedTipTextView2;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放器基本控制操作 <br>
 * 设置自动播放上下集：sp名称：PlayerActivity.SP_AUTO_PLAY,key=auto;
 * 
 * @author suma-hpb
 * 
 */
public class MediaControllerFragment extends Fragment implements
		OnClickListener, OnTouchListener, OnSeekBarChangeListener,
		OnGestureListener {
	private static final String TAG = MediaControllerFragment.class
			.getSimpleName();
	protected PlayerActivity playerActivity;
	protected View rootView;
	public BVideoView player;
	public ImageButton playBtn, playPreBtn, playNextBtn;
	private TextView currentText, totalText;
	public SeekBar mediaSeekBar;
	private TimerTask task;
	private boolean isSeeking = false;
	private static final int SECOND_DURATION = 1000;
	private int playType;
	private int silentCount = 5;
	public boolean isLocked = false;
	private int windowWidth = 0;
	private int windowHeight = 0;
	public static final int VOD_PLAY = 2;
	public static final int LIVE_PLAY = 1;
	public static final int CACHE_PLAY = 3;
	static final int START_OPTION = 13;
	static final int UPDATE_TIME = 20;
	static final int QUIT = 21;
	static final int HIDE_VOL = 22;
	private RelativeLayout gesturelayout;// 进度条
	private ImageView gesturelayoutBg;
	private TextView gestureDegree;
	private RelativeLayout controllerTop = null;
	// private int mMaxVolume;
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	private AudioManager mAudioManager;
	private VerticalSeekBarMusic volSeekBar;
	private GestureDetector mGestureDetector;// 手势
	private static final int GESTURE_CANCEL = 0xede19;// 控制手势
	private static final int GESTURE_UPDATE_TXT = 0xede20;// 更新显示
	private long videoDuration;
	private RelativeLayout gestureVis;// 非全屏
	private RelativeLayout gestureGo;// 全屏
	private ImageButton shuai, scale;
	private ImageButton soundBtn;
	private int currentVol;
	private int streamMaxVolume;
	private int streamNowVolume;
	private LinearLayout vol_layout;
	public RelativeLayout sourcelayout;
	private TextView sourceurlbtn, sourceurl;
	public LinearLayout mController = null;// 控制键
	protected LinearLayout crackLayout = null;// 高清，超清布局
	public RedTipTextView2 share = null;// 分享
	protected LinearLayout shareLayout;
	private static final int HIDE_CONTROLER = 33;
	private static final int HIDE_DELAY_TIME = 5000;
	private static final int UI_EVENT_UPDATE_CURRPOSITION = 1;
	protected ImageButton crack = null;// 上面的
	protected ImageButton crackTop = null;// 标清，高清，超清。
	protected ImageButton crackMiddle = null;
	protected ImageButton crackBottom = null;
	private String standPath = null;// 只有破解有
	private String highPath = null;// 高清
	private String superPath = null;// 超清
	private int CRACK_STANDARD = 0;
	private int CRACK_HIGH = 1;
	private int CRACK_SUPER = 2;
	public ImageButton lock;
	public ImageButton lockBig;
	private static final int HIDE_CONTROLER_LOCKIG = 34;
	public int currPosition;
	public static boolean IS_GUIDE_VISIBLE = false;
	public Button changeSourceBtn, changeTvBtn, changeProgramBtn;

	Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HIDE_VOL:
				if (vol_layout != null) {
					vol_layout.setVisibility(View.GONE);
				}
				if (volSeekBar != null) {
					volSeekBar.setVisibility(View.GONE);
				}
				break;
			case QUIT:
				if (playerActivity != null) {
					playerActivity.finish();
				}
				break;
			case HIDE_CONTROLER:
				if (silentCount > 0) {
					hideControllerDelay();
				} else {
					hideController();
				}

				break;
			case UI_EVENT_UPDATE_CURRPOSITION:

				break;
			case GESTURE_CANCEL:
				gesturelayout.setVisibility(View.GONE);
				break;
			case GESTURE_UPDATE_TXT:
				if (seekpos > 0) {
					updateTextViewWithTimeFormat(currentText, seekpos);
					updateTextViewWithTimeFormat(gestureDegree, seekpos);
				} else {
					updateTextViewWithTimeFormat(currentText, 0);
					updateTextViewWithTimeFormat(gestureDegree, 0);
				}
				// mUIHandler.removeMessages(GESTURE_UPDATE_TXT);
				break;
			case HIDE_CONTROLER_LOCKIG:
				dismisLockBig();
				break;
			case START_OPTION:
				vol_layout.setVisibility(View.INVISIBLE);
				volSeekBar.setVisibility(View.INVISIBLE);
				// removeQuitMessage();
				playerActivity.hideHalfControl();
				playerActivity.sourceText.setVisibility(View.GONE);
				playerActivity.vipText.setVisibility(View.GONE);
				showBackLookUi();
				if (!isLocked) {
					openController();
					hideControllerDelay();
				} else {
					cancelDelayHide();
					hideController();
				}
				if (shareLayout != null) {
					shareLayout.setVisibility(View.GONE);
				}
				mediaSeekBar.setEnabled(true);
				boolean canNext = false;
				boolean canPre = false;
				mPosition = playerActivity.getIntent().getIntExtra("mPosition", 0);
				if (playType != VOD_PLAY){
					if (playerActivity.jiShuDatas != null
							&& playerActivity.jiShuDatas.size() > 1) {
						if (mPosition == 0) {
							canNext = true;
						} else if (mPosition == playerActivity.jiShuDatas.size() - 1) {
							canPre = true;
						} else {
							canNext = true;
							canPre = true;
						}
					}
				}else{
					if (playerActivity.getIntent().getBooleanExtra("isLastStage",false)
							&& mPosition == playerActivity.jiShuDatas.size() - 1){
						canNext = false;
					}else if(playerActivity.programFragment == null){
						canNext = false;
					}else{
						canNext = true;
					}
					canPre = true;
				}
//				if(mPosition == 0 && !playerActivity.getIntent().getBooleanExtra("normalOrder",true)){
//					canNext = false;
//				}
				playNextBtn.setEnabled(canNext);
				playPreBtn.setEnabled(canPre);
				if (canNext) {
					playNextBtn
							.setImageResource(R.drawable.cp_play_tool_advance_bg);
				} else {
					playNextBtn
							.setImageResource(R.drawable.cp_play_next_false_bg);
				}
				if (canPre) {
					playPreBtn
							.setImageResource(R.drawable.cp_play_tool_back_bg);
				} else {
					playPreBtn
							.setImageResource(R.drawable.cp_play_back_false_bg);
				}
				updateResourceText();
				break;
			case UPDATE_TIME:
				setTimeValue();
				break;
			default:
				break;
			}
		}
	};

	public void setSecondaryProgress(int progress) {
		if (mediaSeekBar != null) {
			mediaSeekBar.setSecondaryProgress(progress);
		}
	}

	protected boolean isPlaying() {
		return player.isPlaying();
	}

	protected int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	protected int getPlayType() {
		return playType;
	}

	/**
	 * 进度实时更新操作
	 */
	public void updateUI() {

	}

	@Override
	public void onAttach(Activity activity) {
		Log.e(TAG, "-----onAttach");
		player = ((PlayerActivity) activity).getMediaPlayer();
		task = new VideoUiTask();
		playerActivity = (PlayerActivity) activity;

		standPath = playerActivity.getStandpath();
		superPath = playerActivity.getSuperpath();
		highPath = playerActivity.getHighpath();
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		windowWidth = metrics.widthPixels;
		windowHeight = metrics.heightPixels;
		mGestureDetector = new GestureDetector(getActivity(), this);
		mGestureDetector.setIsLongpressEnabled(false);
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle bundle) {
		Log.e(TAG, "-----onCreate:  bundle=" + bundle);
		super.onCreate(bundle);
		bundle = getArguments();
		playType = bundle.getInt("playType");
		// path = bundle.getString("path");
	}

	/**
	 * 覆写替换布局文件
	 * 
	 * @return rootview布局
	 */
	protected int getLayoutResource() {
		return R.layout.fragment_controller;
	}

	private RelativeLayout titleLayout;
	public TextView titlename;
	private ImageView btnBack;
	private ImageView batteryImageView;
	private TextView timeTextView;
	private BatteryReceiver batteryReceiver;
	public ImageButton enlarge;
	private LinearLayout powerLayout;
	private TimerTask timetask = new TimerTask() {
		@Override
		public void run() {
			mUIHandler.sendEmptyMessage(UPDATE_TIME);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "-----onCreateView");
		timer = new Timer();
		mPosition = playerActivity.getIntent().getIntExtra("mPosition", -1);
		// subOrderType = getActivity().getIntent().getIntExtra("subOrderType",
		// 1);
		if (rootView == null) {
			int resId = getLayoutResource();
			if (resId == 0) {
				resId = R.layout.fragment_controller;
			}
			rootView = inflater.inflate(resId, null);
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}

		titleLayout = (RelativeLayout) rootView
				.findViewById(R.id.np_l_top_layout);
		titlename = (TextView) rootView.findViewById(R.id.np_live_title);
		btnBack = (ImageView) rootView.findViewById(R.id.btn_back);
		btnBack.setOnClickListener(this);
		titlename.setText(getProgramName(getActivity().getIntent()));
		timeTextView = (TextView) rootView.findViewById(R.id.time);
		batteryImageView = (ImageView) rootView.findViewById(R.id.battery);
		powerLayout = (LinearLayout) rootView
				.findViewById(R.id.power_time_layout);
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		batteryReceiver = new BatteryReceiver(batteryImageView);
		getActivity().registerReceiver(batteryReceiver, intentFilter);

		timer = new Timer();
		try {
			timer.schedule(timetask, 0, 60 * 1000);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		if (playType == VOD_PLAY || playType == CACHE_PLAY || playType == 4 || playType == 5) {
			ViewStub stub = (ViewStub) rootView
					.findViewById(R.id.controller_vod_stub);
			mController = (LinearLayout) stub.inflate();
		} else if (playType == LIVE_PLAY) {
			ViewStub stub = (ViewStub) rootView
					.findViewById(R.id.controller_live_stub);
			mController = (LinearLayout) stub.inflate();
			if (!playerActivity.isBackLook) {
				rootView.findViewById(R.id.time_layout)
						.setVisibility(View.GONE);
				mController.setBackgroundColor(R.drawable.player_bg);
			} else {
				rootView.findViewById(R.id.time_layout).setVisibility(
						View.VISIBLE);
				mController.setBackgroundResource(R.drawable.player_bg);
			}
		}
		rootView.findViewById(R.id.controller_layout).setOnTouchListener(this);
		enlarge = (ImageButton) rootView.findViewById(R.id.enlarge);
		if (playerActivity.isHalf) {
			enlarge.setOnClickListener(this);
			enlarge.setVisibility(View.VISIBLE);
		} else {
			if (enlarge != null) {
				enlarge.setVisibility(View.GONE);
				enlarge.setImageResource(R.drawable.to_half_gray);
			}
		}
		playBtn = (ImageButton) rootView.findViewById(R.id.play_btn);
		if (playerActivity.getIntent().getIntExtra("playType", 0) == CACHE_PLAY
				|| playerActivity.getIntent().getIntExtra("playType", 0) == LIVE_PLAY) {
			RelativeLayout.LayoutParams playBtnParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			if(playerActivity.getIntent().getIntExtra("playType", 0) == LIVE_PLAY){
				playBtnParam.addRule(RelativeLayout.CENTER_IN_PARENT);
			}
			playBtn.setLayoutParams(playBtnParam);
		}
		playPreBtn = (ImageButton) rootView.findViewById(R.id.player_pre_btn);
		playPreBtn.setEnabled(false);
		playNextBtn = (ImageButton) rootView.findViewById(R.id.player_next_btn);
		playNextBtn.setEnabled(false);
		currentText = (TextView) rootView.findViewById(R.id.time_current);
		totalText = (TextView) rootView.findViewById(R.id.time_total);
		mediaSeekBar = (SeekBar) rootView.findViewById(R.id.media_progress);

		gesturelayout = (RelativeLayout) rootView
				.findViewById(R.id.gesturelayout);

		gesturelayoutBg = (ImageView) rootView
				.findViewById(R.id.gesturelayout_iv_pic);
		gestureDegree = (TextView) rootView.findViewById(R.id.degree);
		controllerTop = (RelativeLayout) rootView
				.findViewById(R.id.np_l_top_layout_rel);
		gestureVis = (RelativeLayout) rootView.findViewById(R.id.gesture_vis);
		gestureVis.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});

		gestureGo = (RelativeLayout) rootView.findViewById(R.id.gesture_gone);
		gestureGo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});

		soundBtn = (ImageButton) rootView.findViewById(R.id.tool_vol);
		volSeekBar = (VerticalSeekBarMusic) rootView.findViewById(R.id.vol);
		vol_layout = (LinearLayout) rootView.findViewById(R.id.vol_layout);
		volSeekBar
				.setOnSeekBarChangeListener(new VerticalSeekBarMusic.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(
							VerticalSeekBarMusic VerticalSeekBar) {
						hideControllerDelay();
					}

					@Override
					public void onStartTrackingTouch(
							VerticalSeekBarMusic VerticalSeekBar) {
						cancelDelayHide();
					}

					@Override
					public void onProgressChanged(
							VerticalSeekBarMusic VerticalSeekBar, int progress,
							boolean fromUser) {
						currentVol = progress * streamMaxVolume / 100;
						setVol(currentVol);
						if (progress <= 30 && progress > 0) {
							soundBtn.setImageResource(R.drawable.player_sound_min);
						} else if (progress > 70) {
							soundBtn.setImageResource(R.drawable.player_sound_max);
						} else if (progress <= 70 && progress > 30) {
							soundBtn.setImageResource(R.drawable.player_sound_middle);
						} else {
							soundBtn.setImageResource(R.drawable.player_sound_disable);
						}
					}
				});
		initStartVol();
		registerCallbackForControl();
		shuai = (ImageButton) rootView.findViewById(R.id.shuai);
		// if (playType != VOD_PLAY) {
		shuai.setVisibility(View.GONE);
		// }
		shuai.setOnClickListener(this);
		scale = (ImageButton) rootView.findViewById(R.id.scale);
		scale.setOnClickListener(this);
		sourcelayout = (RelativeLayout) rootView
				.findViewById(R.id.sourcelayout);
		if (playerActivity.needAvoid ) {
			sourcelayout.setOnClickListener(this);
			sourcelayout.setClickable(true);
		} else if(playType == LIVE_PLAY && playerActivity.getIntent().getBooleanExtra("toWeb",false)){
			sourcelayout.setOnClickListener(this);
			sourcelayout.setClickable(true);
		}else {
			sourcelayout.setClickable(false);
		}
		mController.setVisibility(View.GONE);
		crack = (ImageButton) rootView.findViewById(R.id.crack);
		crack.setOnClickListener(this);
		crack.setVisibility(View.GONE);
		crackTop = (ImageButton) rootView.findViewById(R.id.crack_top);
		crackTop.setOnClickListener(this);
		crackMiddle = (ImageButton) rootView.findViewById(R.id.crack_middle);
		crackMiddle.setOnClickListener(this);
		crackBottom = (ImageButton) rootView.findViewById(R.id.crack_bottom);
		crackBottom.setOnClickListener(this);
		crackLayout = (LinearLayout) rootView.findViewById(R.id.crack_layout);
		crackLayout.setVisibility(View.GONE);
		share = (RedTipTextView2) rootView.findViewById(R.id.share);
		share.setOnClickListener(this);
		if(playType == PlayerActivity.SHAKE_PLAY){
			share.setVisibility(View.GONE);
		}
		sourceurlbtn = (TextView) rootView.findViewById(R.id.sourceurlbtn);
		sourceurlbtn.setVisibility(View.GONE);
		sourceurl = (TextView) rootView.findViewById(R.id.sourceurl);
		if (playType == LIVE_PLAY) {
			if (TextUtils.isEmpty(getUrl())) {
				sourcelayout.setVisibility(View.GONE);
			} else {
				sourceurl.setText(getString(R.string.video_source) + getUrl());
			}
		}
		updateResourceText();
		shakeX = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_x);
		setCrackResource(standPath, highPath, superPath);
		lock = (ImageButton) rootView.findViewById(R.id.lock);
		if (playType == LIVE_PLAY) {
			lock.setVisibility(View.INVISIBLE);
		}
		lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(getActivity(), "suoping");
				cancelDelayHide();
				hideControllerDelay();
				if (!isLocked) {
					isLocked = true;
					lockBig.setImageResource(R.drawable.liveplaye_bigr_locked);
					hideController();
					lock.setImageResource(R.drawable.liveplayer_locked);
					lockBig.setVisibility(View.VISIBLE);
					cancelHideLockBig();
					hideLockBig();
				} else {
					isLocked = false;
					lockBig.setImageResource(R.drawable.liveplaye_bigr_unlock);
				}
			}
		});

		// 加大屏幕锁定按钮
		lockBig = (ImageButton) rootView.findViewById(R.id.lock_big);
		lockBig.setVisibility(View.GONE);
		{
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) lockBig
					.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(20, 0, 0, 0);
		lockBig.setLayoutParams(params);
		lockBig.setImageResource(R.drawable.live_big_unlock);
		}
		lockBig.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isLocked) {
					// 解锁
					lockBig.setImageResource(R.drawable.live_big_unlock);
					lock.setImageResource(R.drawable.liveplayer_unlock);
					isLocked = false;
					cancelDelayHide();
				} else {
					// 锁定
					MobclickAgent.onEvent(getActivity(), "suoping");
					hideController();
					cancelHideLockBig();
					hideLockBig();
					isLocked = true;
					lockBig.setImageResource(R.drawable.live_big_locked);
				}

			}
		});

		playBtn.setOnClickListener(this);
		playNextBtn.setOnClickListener(this);
		playPreBtn.setOnClickListener(this);
		mediaSeekBar.setOnSeekBarChangeListener(this);
		initErr();
		if (playerActivity.isHalf) {
			setHalfView();
		}
		return rootView;
	}

	public void setHalfView() {
		if (playerActivity.isHalf) {
			playNextBtn.setVisibility(View.GONE);
			if (enlarge != null){
				enlarge.setVisibility(View.VISIBLE);
			}
			sourcelayout.setVisibility(View.GONE);
			crack.setVisibility(View.GONE);
			share.setVisibility(View.GONE);
			errLiveReport.setVisibility(View.GONE);
			batteryImageView.setVisibility(View.GONE);
			timeTextView.setVisibility(View.GONE);
			lockBig.setVisibility(View.GONE);
		} else {
			if (playNextBtn != null) {
				playNextBtn.setVisibility(View.VISIBLE);
			}
			if (enlarge != null ){
				if ( getActivity().getIntent().getBooleanExtra("isHalf", false)) {
					enlarge.setVisibility(View.VISIBLE);
				}else{
					enlarge.setVisibility(View.VISIBLE);
				}
			}
			showQualityBtn();
			sourcelayout.setVisibility(View.VISIBLE);
			errLiveReport.setVisibility(View.VISIBLE);
			if (playType == PlayerActivity.SHAKE_PLAY){
				share.setVisibility(View.GONE);
			}else {
				share.setVisibility(View.VISIBLE);
			}
			batteryImageView.setVisibility(View.VISIBLE);
			timeTextView.setVisibility(View.VISIBLE);
			lockBig.setVisibility(View.VISIBLE);
		}
	}

	public void updateResourceText() {
		if (isVisible()) {
			if (TextUtils.isEmpty(getUrl())) {
				sourcelayout.setVisibility(View.GONE);
			} else {
				sourceurl.setText(getString(R.string.video_source) + getUrl());
			}

			if (playType == PlayerActivity.CACHE_PLAY) {
				sourcelayout.setVisibility(View.GONE);
			}
		}
	}

	public void setResourceText(String url, String path) {
		if (isVisible()) {
			if (!TextUtils.isEmpty(url)) {
				sourceurl.setText(getString(R.string.video_source) + url);
			} else if (!TextUtils.isEmpty(path)) {
				sourceurl.setText(getString(R.string.video_source) + path);
			} else {
				sourcelayout.setVisibility(View.GONE);
			}
			if (playType == LIVE_PLAY && !TextUtils.isEmpty(playerActivity.showUrl)){
				sourceurl.setText(getString(R.string.video_source) + playerActivity.showUrl);
			}
		}
	}

	private void setTimeValue() {
		Time time = new Time();
		time.setToNow();
		int hour = time.hour;
		int minute = time.minute;
		StringBuilder timeValue = new StringBuilder();
		if (hour < 10) {
			timeValue.append(0);
		}
		timeValue.append(hour).append(":");
		if (minute < 10) {
			timeValue.append(0);
		}
		timeValue.append(minute);
		timeTextView.setText(timeValue.toString());
	}

	// private int subOrderType = 1;
	// private static final int SUB_ORDER_INVERSE = 2;
	int mPosition;

	/**
	 * 显示标题栏视频质量标签按钮(标清)
	 */
	public void showQualityBtn() {
		int standardFlag = TextUtils.isEmpty(standPath) ? 0 : 1;
		int highFlag = TextUtils.isEmpty(highPath) ? 0 : 1;
		int superFlag = TextUtils.isEmpty(superPath) ? 0 : 1;
		if ((standardFlag + highFlag + superFlag) >= 2) {
			crack.setVisibility(View.VISIBLE);
		} else {
			crack.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏标题栏视频质量标签按钮(标清)
	 */
	public void hideQualityBtn() {
		crack.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示标题栏分享按钮
	 */
	public void showShareBtn() {
		if (!playerActivity.isHalf) {
			share.setVisibility(View.VISIBLE);
		} else {
			share.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏标题栏分享按钮
	 */
	public void hideShareBtn() {
		share.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示上下集按钮
	 */
	public void showPreNextBtn() {
		playPreBtn.setVisibility(View.VISIBLE);
		playNextBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏上下集按钮
	 */
	public void hidePreNextBtn() {
		playPreBtn.setVisibility(View.INVISIBLE);
		playNextBtn.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示底部控制栏甩屏按钮
	 */
	public void showShuaiBtn() {
		shuai.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏底部控制栏甩屏按钮
	 */
	public void hideShuaiBtn() {
		shuai.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示底部控制栏缩放按钮
	 */
	public void showScaleBtn() {
		scale.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏底部控制栏缩放按钮
	 */
	public void hideScaleBtn() {
		scale.setVisibility(View.INVISIBLE);
	}

	public void showTitleLayout() {
		titleLayout.setVisibility(View.VISIBLE);
		try {
			if (getActivity() != null) {
				titleLayout.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.open2up));
			} else if (playerActivity != null) {
				titleLayout.startAnimation(AnimationUtils.loadAnimation(
						playerActivity, R.anim.open2up));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideTitleLayout() {
		titleLayout.setVisibility(View.GONE);
		try {
			if (getActivity() != null) {
				titleLayout.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.close2up));
			} else if (playerActivity != null) {
				titleLayout.startAnimation(AnimationUtils.loadAnimation(
						playerActivity, R.anim.close2up));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		hideErrList();
		if (shareLayout != null) {
			shareLayout.setVisibility(View.GONE);
		}
	}

	public String getUrl() {
		if (playType == VOD_PLAY && TextUtils.isEmpty(playerActivity.url) && TextUtils.isEmpty(playerActivity.path)) {
			return "";
		}
		if (playType == LIVE_PLAY && !playerActivity.isBackLook) {
			if (!TextUtils.isEmpty(playerActivity.showUrl)){
				return playerActivity.showUrl;
			}
			if (!TextUtils.isEmpty(playerActivity.url)) {
				return playerActivity.url;
			} else {
				return playerActivity.path;
			}
		}
		if (playType == LIVE_PLAY
				|| playerActivity.activityId != 0
				|| (playerActivity.url != null && playerActivity.url
						.endsWith(".png"))
				|| (playerActivity.url != null && playerActivity.url
						.endsWith(".jpg"))) {
			return playerActivity.path;
		} else {
			return TextUtils.isEmpty(playerActivity.url) ? playerActivity.path : playerActivity.url;
		}
	}

	private void hideLockBig() {
		mUIHandler.sendEmptyMessageDelayed(HIDE_CONTROLER_LOCKIG, 3000);
	}

	public void cancelDelayHide() {

		mUIHandler.removeMessages(HIDE_CONTROLER);
	}

	private void cancelHideLockBig() {
		mUIHandler.removeMessages(HIDE_CONTROLER_LOCKIG);

	}

	private void dismisLockBig() {
		lockBig.setVisibility(View.GONE);
	}

	private Timer timer;

	@Override
	public void onViewCreated(View view, Bundle bundle) {
		Log.e(TAG, "-----onViewCreated: bundle=" + bundle);
		timer.schedule(task, 2000, SECOND_DURATION);
		super.onViewCreated(view, bundle);
	}

	private void setVol(int value) {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
	}

	private void initStartVol() {

		mAudioManager = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		streamMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		streamNowVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		int progress = streamNowVolume * 100 / streamMaxVolume;
//		progress = progress < 10 ? 10 : progress;
		volSeekBar.setProgress(progress);
		setVol(progress * streamMaxVolume / 100);
		if (progress <= 30 && progress > 0) {
			soundBtn.setImageResource(R.drawable.player_sound_min);
		} else if (progress > 70) {
			soundBtn.setImageResource(R.drawable.player_sound_max);
		} else if (progress <= 70 && progress > 30) {
			soundBtn.setImageResource(R.drawable.player_sound_middle);
		} else {
			soundBtn.setImageResource(R.drawable.player_sound_disable);
		}
	}

	@Override
	public void onDestroyView() {
		Log.e(TAG, "-----onDestroyView");
		if (timer != null) {
			if (task != null) {
				task.cancel();
			}
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (batteryReceiver != null) {
			getActivity().unregisterReceiver(batteryReceiver);
		}
		if (mUIHandler != null) {
			mUIHandler.removeMessages(HIDE_CONTROLER);
			mUIHandler.removeMessages(HIDE_CONTROLER_LOCKIG);
			mUIHandler.removeMessages(GESTURE_CANCEL);
			mUIHandler.removeMessages(GESTURE_UPDATE_TXT);
			mUIHandler.removeMessages(START_OPTION);
			mUIHandler.removeMessages(UPDATE_TIME);
		}
		super.onDestroyView();
	}

	private boolean isCaching = false;
	private boolean isPaused = false;

	public void setPaused(boolean b) {
		isPaused = b;
	}

	/**
	 * 上下集播放需实现
	 */
	public void preNextPlay(int position) {
		if (playerActivity.jiShuDatas != null && position >= 0
				&& position < playerActivity.jiShuDatas.size()) {
			JiShuData tempData = playerActivity.jiShuDatas.get(mPosition);
			String url = tempData.url;
			String videoPath = tempData.videoPath;
			String title = tempData.name;
			int playType = 2;
			Intent intent = playerActivity.getIntent();
			if (playerActivity.isTvType()){
				title = tempData.shortName;
			}else {
				title = (TextUtils.isEmpty(tempData.shortName)?"":(tempData.shortName +" : ")) + tempData.name;
			}
			playerActivity.getFragmentHelper().playCount(getActivity(),Integer.parseInt(playerActivity.programId),tempData.id, 0);
			// 直接播放
			if (!TextUtils.isEmpty(videoPath)) {
				// PlayVideoCount();
				intent.putExtra("path", videoPath);

				if (TextUtils.isDigitsOnly(title)) {
					title = "第" + title + "集";
				}
				if (tempData.cacheInfo != null && tempData.cacheInfo.state == 2) {
					intent.putExtra("playType", PlayerActivity.CACHE_PLAY);
				}
				intent.putExtra("title", title);
				intent.putExtra("mPosition", mPosition);
				intent.putExtra("needVParse", false);
				intent.putExtra("subid", tempData.id);
				titlename.setText(getProgramName(intent));
				playerActivity.restartPlay(intent);
			} else if (!TextUtils.isEmpty(tempData.url)) {// 破解播放
//				boolean baiduParser = tempData.url.contains("pan.baidu.com")
//						|| tempData.url.contains("d.pcs.baidu.com");
//				boolean userVparser = tempData.url.contains("sohu")
//						|| tempData.url.contains("youku")
//						|| url.contains("iqiyi") || url.contains("tudou")
//						|| url.contains("letv") || url.contains("qq")
//						|| url.contains("pptv") || url.contains("hunantv");
//				if (baiduParser || userVparser) {
					//url破解
					intent.putExtra("path", "");
					intent.putExtra("playType", playType);
					intent.putExtra("fromString", tempData.name);
					intent.putExtra("url", url);
					intent.putExtra("subid", tempData.id);
					if (TextUtils.isDigitsOnly(title)) {
						title = "第" + title + "集";
					}
					intent.putExtra("title", title);
					intent.putExtra("needVParse", true);
					Bundle mBundle = new Bundle();
					mBundle.putInt("mPosition", mPosition);
					intent.putExtras(mBundle);
					titlename.setText(getProgramName(intent));
					playerActivity.restartPlay(intent);

//				} else {
//					openNetLiveActivity(url, videoPath, playType, title);
//				}
			}
		} else {
			playerActivity.finish();
		}

	}

	public void openNetLiveActivity(String url, String videoPath, int isLive,
			String title) {
		Intent intent = new Intent();
		intent.putExtra("url", url);
		intent.putExtra("videoPath", videoPath);
		intent.putExtra("title", title);
		intent.setClass(playerActivity, MainWebPlayActivity.class);
		startActivity(intent);
	}

	public boolean playNext = true;

	public boolean switchPlayPreNext;

	public boolean isPaused() {
		return isPaused;
	}

	public void playPreOrNext() {
		if (playerActivity == null) {
			return;
		}
		if (switchPlayPreNext) {
			switchPlayPreNext = false;
			mPosition = playerActivity.getIntent().getIntExtra("mPosition", -1);
			if(playerActivity.getIntent().getBooleanExtra("jump", false)){
				playerActivity.changeVideo = true;
				preNextPlay(mPosition);
				playerActivity.getIntent().putExtra("jump", false);
				return;
			}
			if (mPosition != -1) {
				if (!playNext) {
					MobclickAgent.onEvent(getActivity(), "shangyiji");
					mPosition--;
				} else {
					mPosition++;
				}
				playerActivity.changeVideo = true;
				preNextPlay(mPosition);
			} else {
				if (getActivity() != null) {
					getActivity().finish();
				}
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	boolean isScaled = false;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.play_btn) {
			playBtn.setSelected(player.isPlaying());
			if (player.isPlaying()) {
				player.pause();
				isPaused = true;
			} else {
				player.resume();
				isPaused = false;
			}
		} else if (id == R.id.player_pre_btn || id == R.id.player_next_btn) {
			playPreBtn.setEnabled(false);
			playNextBtn.setEnabled(false);
			setReportEnable(true);
			cancelDelayHide();
			hideControllerDelay();
			switchPlayPreNext = true;
			playerActivity.changePath = false;
			playerActivity.switchEpisode = false;
			if (id == R.id.player_pre_btn) {
				MobclickAgent.onEvent(getActivity(), "shangyiji");
				playNext = false;
			} else {
				MobclickAgent.onEvent(getActivity(), "xiayiji");
				playNext = true;
			}
			player.stopPlayback();
		} else if (id == R.id.crack_middle) {
			setCrackBackground(CRACK_HIGH);
		} else if (id == R.id.crack_bottom) {
			setCrackBackground(CRACK_SUPER);
		} else if (id == R.id.crack) {
			clickCrack();
		} else if (id == R.id.crack_top) {
			setCrackBackground(CRACK_STANDARD);
		} else if (id == R.id.sourcelayout) {
			playerActivity.opensourceurl();
		} else if (id == R.id.first_touch_id) {
			IS_GUIDE_VISIBLE = false;

		} else if (v.getId() == R.id.share) {
			MobclickAgent.onEvent(getActivity(), "bfqfx");
			if (shareLayout != null) {
				cancelDelayHide();
				hideControllerDelay();
				if (!shareLayout.isShown()) {
					shareLayout.setVisibility(View.VISIBLE);
					setVisible = false;
					if (crackLayout.isShown()) {
						crackLayout.setVisibility(View.GONE);
					}
				} else {
					shareLayout.setVisibility(View.GONE);
					setVisible = true;
				}
			}
			errList.setVisibility(View.GONE);
			liveList.setVisibility(View.GONE);
			showButtonRight(setVisible);
		} else if (v.getId() == R.id.scale) {
			MobclickAgent.onEvent(getActivity(), "pingmubl");
			if (!isScaled) {
				scale.setImageResource(R.drawable.video_scale_normal);
			} else {
				scale.setImageResource(R.drawable.video_scale_zoom);
			}
			isScaled = !isScaled;
			playerActivity.screenScale();
		} else if (v.getId() == R.id.btn_back) {
			onBackClick();
		} else if (id == R.id.tv_error) {
			if (errList.getVisibility() == View.GONE) {
				errList.setVisibility(View.VISIBLE);
			} else {
				errList.setVisibility(View.GONE);
			}
			if (shareLayout != null) {
				shareLayout.setVisibility(View.GONE);
			}
		} else if (id == R.id.live_tv_err) {
			if (liveList.getVisibility() == View.GONE) {
				if (errList.getVisibility() == View.GONE) {
					liveList.setVisibility(View.VISIBLE);
					setVisible = false;
				} else {
					errList.setVisibility(View.GONE);
				}
			} else {
				liveList.setVisibility(View.GONE);
				setVisible = true;
			}
			if (shareLayout != null) {
				shareLayout.setVisibility(View.GONE);
			}
			showButtonRight(setVisible);
		} else if (id == R.id.shuai) {
			startDlna();
		} else if (id == R.id.enlarge) {
			playerActivity.changeOrientation(true);
		} else if (id == R.id.media_start_play) {
			if (playerActivity.isPrepared) {
				player.stopPlayback();
			} else {
				player.stopPlayback();
				playerActivity.restartPlay(playerActivity.getIntent());
			}
		}
	}

	private void onBackClick() {
		if (playerActivity.enterType == 1) {
			playerActivity.changeOrientation(true);
		} else {
			playerActivity.clickquit = true;
			playerActivity.finish();
		}
	}

	public void clickCrack() {
		if (crackLayout.isShown()) {
			crackLayout.setVisibility(View.GONE);
		} else {
			crackLayout.setVisibility(View.VISIBLE);
			if (shareLayout != null && shareLayout.isShown()) {
				shareLayout.setVisibility(View.GONE);
			}
		}
		crack.setVisibility(View.GONE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}

		// 处理手势结束
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// mUIHandler.removeMessages(HIDE_CONTROLER);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			GESTURE_FLAG = 0;
			if (seeking && seekpos <= videoDuration && playType != LIVE_PLAY) {
				seeking = false;
				player.seekTo(seekpos);
				playerActivity.cachingSeek();
			} else if (seekpos > videoDuration && playType != LIVE_PLAY) {
				if (playerActivity.subid == 0 || Integer.parseInt(playerActivity.programId) == 0
						|| playerActivity.subid == Integer.parseInt(playerActivity.programId)
						|| playType == CACHE_PLAY) {
					getActivity().finish();
				}else {
//					playerActivity.mUIHandler.sendEmptyMessage(15); //15即PLAY_OVER
					player.stopPlayback();
					seekpos = 0;
				}
			}
			if (playType == LIVE_PLAY && playerActivity.isBackLook) {
				if (seeking && seekpos <= videoDuration) {
					seeking = false;
					player.seekTo(seekpos);
					playerActivity.cachingSeek();
				} else if (seekpos > videoDuration) {
					if (playerActivity.subid == 0 || Integer.parseInt(playerActivity.programId) == 0
							|| playerActivity.subid == Integer.parseInt(playerActivity.programId)
							|| playType == CACHE_PLAY) {
						getActivity().finish();
					}else {
//						playerActivity.mUIHandler.sendEmptyMessage(15); //15即PLAY_OVER
						player.stopPlayback();
						seekpos = 0;
					}
				}
			}
			if (barShow) {
				hideControllerDelay();
			}
			endGesture();
			gesturelayout.setVisibility(View.GONE);
			break;
		case MotionEvent.ACTION_SCROLL:

			break;
		}

		return true;

	}

	public static String getTime(int second) {

		int length = second;
		int hour = length / 3600;
		int min = (length - hour * 3600) / 60;
		int sec = length - hour * 3600 - min * 60;
		String time = String.format("%02d:%02d:%02d", hour, min, sec);
		return time;
	}

	private boolean isend = false;

	class VideoUiTask extends TimerTask {

		@Override
		public void run() {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					silentCount--;
					updateUI();
					currPosition = player.getCurrentPosition();
					videoDuration = player.getDuration();
					if (videoDuration != 0) {
						playerActivity.videoTotalTime = videoDuration;
					}
					if (currPosition >= videoDuration && videoDuration > 0) {
						isend = true;
					}
					if (player.isPlaying()) {
						playerActivity.isPrepared = true;
						if (!isSeeking) {
							updateTextViewWithTimeFormat(currentText,
									player.getCurrentPosition());
							mediaSeekBar.setProgress(player
									.getCurrentPosition());

						}
						updateTextViewWithTimeFormat(totalText,
								player.getDuration());

						mediaSeekBar.setMax(player.getDuration());
						if (isPaused) {
							player.pause();
						}
						// if (isend) {
						// playerActivity.playCompleted = true;
						// playerActivity.closeOrPlayNext();
						// }
					} else if (!isPaused && !isCaching && playType != LIVE_PLAY
							&& videoDuration != 0) {
						// playerActivity.playCompleted = true;
						// playerActivity.closeOrPlayNext();
					} else if (isPaused && playType == LIVE_PLAY){
						player.pause();
					}
				}
			});
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			currentText.setText(getTime(progress));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		isSeeking = true;
		mUIHandler.removeMessages(HIDE_CONTROLER);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		MobclickAgent.onEvent(getActivity(), "huadongjindu");
		player.seekTo(seekBar.getProgress());
		playerActivity.cachingSeek();
		isSeeking = false;
		if (!player.isPlaying()) {
			playBtn.setSelected(player.isPlaying());
			player.resume();
		}
		mUIHandler.sendEmptyMessage(HIDE_CONTROLER);
	}

	private void updateTextViewWithTimeFormat(TextView view, int second) {
		if (second < 0) {
			second = 0;
		}
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		if (0 != hh) {
			strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			strTemp = String.format("%02d:%02d", mm, ss);
		}
		view.setText(strTemp);
	}

	public int seekpos;
	boolean seeking = false;
	public boolean barShow = true;

	private void registerCallbackForControl() {
		soundBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(getActivity(), "yinliang");
				if (volSeekBar.isShown()) {
					vol_layout.setVisibility(View.INVISIBLE);
					volSeekBar.setVisibility(View.INVISIBLE);
				} else {
					// initStartVol();
					// setVolumeSeekBarAndVolumeBtn(true);
					vol_layout.setVisibility(View.VISIBLE);
					volSeekBar.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	private void setVolumeSeekBarAndVolumeBtn(boolean raise) {
		streamNowVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		int progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
				* 100 / streamMaxVolume;

		int streamMaxVolumeT = streamMaxVolume * 100;
		if (raise) {
			progress += 10;
			if (progress > streamMaxVolumeT)
				progress = streamMaxVolumeT;

		} else {
			progress -= 10;
			if (progress < 0)
				progress = 0;
		}
		volSeekBar.setProgress(progress);
		setVol(progress * streamMaxVolume / 100);
		volSeekBar.setThumbOffset(-progress * streamMaxVolume / 100);
		if (progress <= 30 && progress > 0) {
			soundBtn.setImageResource(R.drawable.player_sound_min);
		} else if (progress > 70) {
			soundBtn.setImageResource(R.drawable.player_sound_max);
		} else if (progress <= 70 && progress > 30) {
			soundBtn.setImageResource(R.drawable.player_sound_middle);
		} else {
			soundBtn.setImageResource(R.drawable.player_sound_disable);
		}

	}

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

		}
		// 显示
		gesturelayoutBg.setBackgroundResource(R.drawable.gesture_middle_sound);
		gesturelayout.setVisibility(View.VISIBLE);

		int index = (int) (percent * streamMaxVolume) + mVolume;
		if (index > streamMaxVolume)
			index = streamMaxVolume;
		else if (index < 0)
			index = 0;
		Log.i("new play er", "index==" + index);
		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
		float index1 = index;
		gestureDegree.setText((int) (index1 / streamMaxVolume * 100) + "%");
		volSeekBar.setProgress((int) (index1 / streamMaxVolume * 100));
		volSeekBar.setThumbOffset((int) (-index1 * streamMaxVolume / 100));
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getActivity().getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.00f;

		}
		// 显示
		// mOperationBg.setImageResource(R.drawable.video_brightness_bg);
		gesturelayout.setVisibility(View.VISIBLE);
		gesturelayoutBg.setBackgroundResource(R.drawable.gesture_middle_light);
		WindowManager.LayoutParams lpa = getActivity().getWindow()
				.getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getActivity().getWindow().setAttributes(lpa);
		gestureDegree.setText((int) (lpa.screenBrightness * 100) + "%");
	}

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		if (player.isPlaying()) {
			mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
			mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
		}

		mUIHandler.removeMessages(GESTURE_CANCEL);
		mUIHandler.sendEmptyMessageDelayed(GESTURE_CANCEL, 1000);
	}

	float mOldX = 0, mOldY = 0;
	private Animation shakeX;

	private void processLockBtn() {
		lockBig.setImageResource(R.drawable.live_big_locked);
		lockBig.setVisibility(View.VISIBLE);
		Toast.makeText(getActivity(), "屏幕已锁定，请先解锁", Toast.LENGTH_SHORT).show();
		lockBig.startAnimation(shakeX);
		cancelHideLockBig();
		hideLockBig();
	}

	// show=true时显示控制条，false时隐藏控制条
	public void updateControlBar(boolean show) {
		if (isLocked) {
			gestureGo.setVisibility(View.GONE);
			gestureVis.setVisibility(View.GONE);
			lockBig.setImageResource(R.drawable.live_big_locked);
			if (lockBig.isShown())
				lockBig.setVisibility(View.GONE);
			else {
				processLockBtn();
			}
		} else {
			lockBig.setImageResource(R.drawable.live_big_unlock);
			cancelDelayHide();
			if (show) {
				gestureGo.setVisibility(View.GONE);
				gestureVis.setVisibility(View.VISIBLE);
				openController();
				hideControllerDelay();// 自动隐藏 定时计数器
			} else {
				hideController();
				gestureVis.setVisibility(View.GONE);
				gestureGo.setVisibility(View.VISIBLE);
			}
			barShow = show;
		}

	}

	int curr;
	int cp;
	private int current = 0;
	private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
	private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量
	private static final int GESTURE_MODIFY_PROGRESS = 1;
	private static final int GESTURE_MODIFY_VOLUME_BRIGHRT = 2;

	@Override
	public boolean onDown(MotionEvent arg0) {
		curr = player.getDuration();
		cp = player.getCurrentPosition();
		firstScroll = true;
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (!playerActivity.isPrepared) {
			return false;
		}
		float mOldX = e1.getX(), mOldY = e1.getY();
		int y = (int) e2.getRawY();
		int x = (int) e2.getRawX();

		if (firstScroll) {
			if (Math.abs(distanceX) >= Math.abs(distanceY)) {
				GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
			} else {
				GESTURE_FLAG = GESTURE_MODIFY_VOLUME_BRIGHRT;
			}
		}
		if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS && !isLocked
				&& (playType != LIVE_PLAY || playerActivity.isBackLook)
				&& seekpos <= curr) {// 控制进度条
			if (Math.abs(distanceX) > Math.abs(distanceY)) {
				gesturelayout.setVisibility(View.VISIBLE);
				seeking = true;
				if ((current - x) < 0) {
					current = x;
					gesturelayoutBg
							.setBackgroundResource(R.drawable.gesture_middle_next);
					if (mOldX > x) {
						seekpos = (int) (cp - Math.abs((mOldX - x) / 5
								/ controllerTop.getWidth() * curr));
					} else {
						seekpos = (int) (cp + Math.abs((mOldX - x) / 5
								/ controllerTop.getWidth() * curr));
					}
					mediaSeekBar.setProgress(seekpos);
				} else {
					current = x;
					gesturelayoutBg
							.setBackgroundResource(R.drawable.gesture_middle_back);
					if (mOldX > x) {
						seekpos = (int) (cp - Math.abs((mOldX - x) / 5
								/ controllerTop.getWidth() * curr));
					} else {
						seekpos = (int) (cp + Math.abs((mOldX - x) / 5
								/ controllerTop.getWidth() * curr));
					}
					mediaSeekBar.setProgress(seekpos);
				}
				mUIHandler.sendEmptyMessage(GESTURE_UPDATE_TXT);
			}
		} else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME_BRIGHRT && !isLocked) {
			if (Math.abs(distanceY) > Math.abs(distanceX)) {
				if (mOldX > windowWidth * 1 / 2)// 右边滑动
					onVolumeSlide((mOldY - y) / windowHeight);
				else if (mOldX < windowWidth / 2)// 左边滑动
					onBrightnessSlide((mOldY - y) / windowHeight);
			}
		}
		firstScroll = false;
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		if (playerActivity.isPrepared) {
			silentCount = 5;
			updateControlBar(!barShow);
		} else {
			if (playType == LIVE_PLAY) {
				silentCount = 5;
				updateControlBar(!barShow);
			} else {
				if (barShow) {
					hideTitleLayout();
					barShow = false;
				} else {
					showTitleLayout();
					barShow = true;
				}
			}

		}
		return false;
	}

	protected void hideControllerDelay() {
		mUIHandler.removeMessages(HIDE_CONTROLER);
		mUIHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, HIDE_DELAY_TIME);
	}

	// 隐藏界面上的控制条
	protected void hideController() {
		// / CRACK_CLICK = false;
		if (!barShow) {
			return;
		}
		share.setClickable(true);
		crackLayout.setVisibility(View.GONE);
		barShow = false;
		if (getActivity() != null) {
			mController.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.close2bottom));
			controllerTop.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.close2up));
		}
		hideTitleLayout();
		controllerTop.setVisibility(View.GONE);
		sourcelayout.setVisibility(View.GONE);
		mController.setVisibility(View.GONE);
		if (shareLayout != null) {
			shareLayout.setVisibility(View.GONE);
		}
		vol_layout.setVisibility(View.INVISIBLE);
		volSeekBar.setVisibility(View.INVISIBLE);
		errList.setVisibility(View.GONE);
		lockBig.setVisibility(View.GONE);
		mUIHandler.removeMessages(HIDE_CONTROLER);
	}

	// 打开界面上的全部控制条
	protected void openController() {
		setVisible = true;
		showButtonRight(setVisible);
		barShow = true;
		if (getActivity() != null) {
			if (playerActivity.isPrepared) {
				mController.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.open2bottom));
				mController.setVisibility(View.VISIBLE);
			}
			controllerTop.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.open2up));
			showTitleLayout();
			setErrView();
			controllerTop.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(getUrl()))
				sourcelayout.setVisibility(View.VISIBLE);
			lockBig.setVisibility(View.VISIBLE);
			if (playType == VOD_PLAY && showcountnum > 1) {
				crack.setVisibility(View.VISIBLE);
			}
			cancelHideLockBig();

			setHalfView();
		}
	}

	private int showcountnum = 0;
	
	public void setCrackUrl(String standurl, String highurl,
			String superurl) {
		standPath = standurl;
		highPath = highurl;
		superPath = superurl;
	}

	public void setCrackResource(String standurl, String highurl,
			String superurl) {
		standPath = standurl;
		highPath = highurl;
		superPath = superurl;
		int standFlag = TextUtils.isEmpty(standurl) ? 0 : 1;
		int highFlag = TextUtils.isEmpty(highurl) ? 0 : 1;
		int superFlag = TextUtils.isEmpty(superurl) ? 0 : 1;
		int showCount = standFlag + highFlag + superFlag;
		showcountnum = showCount;
		if (crack != null) {
			if (showCount == 1) {
				crack.setVisibility(View.GONE);
			} else if (showCount >= 2) {
				crack.setVisibility(View.VISIBLE);
				crack.setClickable(true);
				setCrackItem(standFlag, crackTop, R.drawable.standard_false,
						R.drawable.standard_press);
				setCrackItem(highFlag, crackMiddle, R.drawable.high_false,
						R.drawable.high_press);
				setCrackItem(superFlag, crackBottom, R.drawable.super_false,
						R.drawable.super_press);
				if (standFlag == 1) {
					crack.setImageResource(R.drawable.standard_selected);
					crackTop.setVisibility(View.GONE);
				} else {
					crack.setImageResource(R.drawable.higher_selected);
					crackMiddle.setVisibility(View.GONE);
				}
			} else if (showCount == 0) {
				crack.setVisibility(View.GONE);
			}
		} else if (showCount == 0 && crack != null) {
			crack.setVisibility(View.GONE);
		}
	}

	private void setCrackItem(int flag, ImageView imgeBtn, int emptyResId,
			int noEmptyResId) {
		if (flag == 0) {
			imgeBtn.setImageResource(emptyResId);
			imgeBtn.setClickable(false);
			imgeBtn.setVisibility(View.GONE);
		} else {
			imgeBtn.setImageResource(noEmptyResId);
			imgeBtn.setClickable(true);
			imgeBtn.setVisibility(View.VISIBLE);
		}
	}

	protected void setCrackBackground(int crackType) {
//		if (!TextUtils.isEmpty(playerActivity.getStandpath())) {
//			crackTop.setVisibility(View.VISIBLE);
//		}
//		if (!TextUtils.isEmpty(playerActivity.getHighpath())) {
//			crackMiddle.setVisibility(View.VISIBLE);
//		}
//		if (!TextUtils.isEmpty(playerActivity.getSuperpath())) {
//			crackBottom.setVisibility(View.VISIBLE);
//		}
//		crackLayout.setVisibility(View.GONE);
//		if (crackType == PlayerActivity.PATH_STAND) {
//			crack.setBackgroundResource(R.drawable.standard_selected);
//			crackTop.setVisibility(View.GONE);
//		}
//		if (crackType == PlayerActivity.PATH_HIGH) {
//			crack.setBackgroundResource(R.drawable.higher_selected);
//			crackMiddle.setVisibility(View.GONE);
//		}
//		if (crackType == PlayerActivity.PATH_SUPER) {
//			crack.setBackgroundResource(R.drawable.super_selected);
//			crackBottom.setVisibility(View.GONE);
//
//		}
//		playerActivity.changePath(crackType);
//		crack.setVisibility(View.VISIBLE);
	}

	public boolean keydown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (playerActivity != null) {
				playerActivity.clickquit = true;
			}
			if (PlayerActivity.IS_DOWNLOADING_STATUS) {
				if (playerActivity.enterType == 1) {
					playerActivity.changeOrientation(true);
					playerActivity.clickquit = false;
					return true;
				}
				return false;
			} else if (IS_GUIDE_VISIBLE) {
				IS_GUIDE_VISIBLE = false;
			} else {
				if (!isLocked) {
					if (playerActivity.enterType == 1) {
						playerActivity.changeOrientation(true);
						playerActivity.clickquit = false;
						return true;
					}
					return false;
				} else {
					Toast.makeText(getActivity(), "屏幕已锁定，请先解锁",
							Toast.LENGTH_SHORT).show();
					cancelDelayHide();
					if (!isLocked)
						openController();
					cancelHideLockBig();
					hideLockBig();
					lockBig.setImageResource(R.drawable.live_big_locked);
					lockBig.setVisibility(View.VISIBLE);
					lockBig.startAnimation(shakeX);
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mController.isShown()) {
				hideController();
			} else {
				cancelDelayHide();
				if (!isLocked && playerActivity.isPrepared)
					openController();
				hideControllerDelay();
			}
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			// 音量减小
			mUIHandler.removeMessages(HIDE_VOL);
			if (playerActivity.isPrepared && !isLocked) {
				if (!barShow) {
					openController();
				}
				hideControllerDelay();
			}
			if (playerActivity.isPrepared) {
				volSeekBar.setVisibility(View.VISIBLE);
				vol_layout.setVisibility(View.VISIBLE);
				mUIHandler.sendEmptyMessageDelayed(HIDE_VOL, 3000);
			}
			setVolumeSeekBarAndVolumeBtn(false);
			hideControllerDelay();
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			// 音量增大
			mUIHandler.removeMessages(HIDE_VOL);
			if (playerActivity.isPrepared && !isLocked) {
				if (!barShow) {
					openController();
				}
				hideControllerDelay();
			}
			if (playerActivity.isPrepared) {
				volSeekBar.setVisibility(View.VISIBLE);
				vol_layout.setVisibility(View.VISIBLE);
				mUIHandler.sendEmptyMessageDelayed(HIDE_VOL, 3000);
			}
			setVolumeSeekBarAndVolumeBtn(true);
			hideControllerDelay();
			return true;

		}
		return true;
	}

	String programName;

	public String getProgramName(Intent intent) {
		programName = intent
				.getStringExtra(PlayerActivity.TAG_INTENT_PROGRAMNAME);
		if (TextUtils.isEmpty(programName)) {
			programName = intent
					.getStringExtra(PlayerActivity.TAG_INTENT_TITLE);
		} else {
			StringBuffer sb = new StringBuffer(programName);
			sb.append(" ").append(
					intent.getStringExtra(PlayerActivity.TAG_INTENT_TITLE));
			programName = sb.toString();
		}
		if (TextUtils.isEmpty(programName)) {
//			programName = getString(R.string.player_default_name);
			programName = "";
		}
		return programName;
	}

	ListView errList;
	ListView liveList;
	TextView errReportTxt;
	ImageButton errLiveReport;
	ReportFrontAdapter reportAdapter;

	private void initErr() {
		errReportTxt = (TextView) rootView.findViewById(R.id.tv_error);
		errLiveReport = (ImageButton) rootView.findViewById(R.id.live_tv_err);
		errLiveReport.setVisibility(View.VISIBLE);
		errReportTxt.setVisibility(View.GONE);
		errReportTxt.setOnClickListener(this);
		errLiveReport.setOnClickListener(this);
		errList = (ListView) rootView.findViewById(R.id.list_err);
		liveList = (ListView) rootView.findViewById(R.id.list_live);
		liveList.setDivider(null);
		errList.setDivider(null);
		String[] liveItem = getResources().getStringArray(
				R.array.vod_play_list);
		if (playType == LIVE_PLAY) {
			liveItem = getResources().getStringArray(
					R.array.live_play_list);
		}
		reportAdapter = new ReportFrontAdapter(getActivity(), liveItem);
		liveList.setAdapter(reportAdapter);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) errList
				.getLayoutParams();
		params.setMargins(0, 0, 0, 0);
		errList.setLayoutParams(params);
			liveList.setAdapter(reportAdapter);
			liveList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					switch (position) {
					case 0:
						if (reportAdapter != null
								&& reportAdapter.isCanReport()) {
							liveList.setVisibility(View.GONE);
							errList.setVisibility(View.VISIBLE);
						}
						break;
					case 1:
						MobclickAgent.onEvent(getActivity(), "pingmubl");
						isScaled = !isScaled;
						if (!isScaled) {
							player.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
						} else {
							player.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
						}
						liveList.setVisibility(View.GONE);
						break;
					case 2:
						startDlna();
						break;
					case 3:
						ImageView checkedVIew = (ImageView) view
								.findViewById(R.id.imgv_checkd);
						boolean isVisible = checkedVIew.getVisibility() == View.VISIBLE;
						checkedVIew.setVisibility(isVisible ? View.GONE
								: View.VISIBLE);
						PreferencesUtils.putBoolean(getActivity(), null,
								"half_auto", !isVisible);
						break;
					case 4:
						checkedVIew = (ImageView) view
								.findViewById(R.id.imgv_checkd);
						isVisible = checkedVIew.getVisibility() == View.VISIBLE;
						checkedVIew.setVisibility(isVisible ? View.GONE
								: View.VISIBLE);
						PreferencesUtils
								.putBoolean(getActivity(),
										PlayerActivity.SP_AUTO_PLAY, "auto",
										!isVisible);
						break;
					}
				}

			});
		final String[] problems = getResources().getStringArray(
				R.array.play_err);
		errList.setAdapter(new ReportAdapter(getActivity(), problems));
		errList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				playerActivity.getFragmentHelper().feedbackReport(
						playerActivity.programId, playerActivity.channelId,
						String.valueOf(playerActivity.subid), programName,
						problems[position]);
				Toast.makeText(getActivity().getApplicationContext(),
						"感谢您的意见，我们会及时解决", Toast.LENGTH_SHORT).show();
				setReportEnable(false);
				errList.setVisibility(View.GONE);
			}
		});
	}

	private void startDlna() {
		MobclickAgent.onEvent(getActivity(), "shuaip");
		Intent dlnaSearchIntent = new Intent(getActivity(),
				StbSearchActivity.class);
		String remoteUrl = "";
		if (playType == LIVE_PLAY) {
			remoteUrl = playerActivity.getVideoSource();
		} else {
			remoteUrl = playerActivity.crackPath;
		}
		dlnaSearchIntent.putExtra(StbSearchActivity.TAG_PATH, remoteUrl);
		dlnaSearchIntent.putExtra(StbSearchActivity.TAG_ICON, getActivity()
				.getIntent().getIntExtra(PlayerActivity.TAG_INTENT_ICON, 0));
		if (playType == VOD_PLAY) {
			dlnaSearchIntent.putExtra("videoDuration",
					(long) playerActivity.getDuration());
			dlnaSearchIntent.putExtra("breakPoint", getCurrentPosition());
		}
		dlnaSearchIntent.putExtra("isLivePlay", playType == LIVE_PLAY);
		startActivity(dlnaSearchIntent);
		liveList.setVisibility(View.GONE);
	}

	public void setErrView() {
		errReportTxt.setVisibility(View.GONE);
		errLiveReport.setVisibility(View.VISIBLE);
	}

	public void setReportEnable(boolean canTouch) {
		if (reportAdapter != null) {
			reportAdapter.setCanReport(canTouch);
			reportAdapter.notifyDataSetInvalidated();
		}
		errReportTxt.setEnabled(canTouch);
		if (canTouch && errReportTxt != null) {
			errReportTxt.setTextColor(Color.WHITE);
		} else if (errReportTxt != null) {
			errReportTxt.setTextColor(Color.GRAY);
		}
	}

	public void hideErrList() {
		if (errList != null) {
			errList.setVisibility(View.GONE);
		}
		if (liveList != null) {
			liveList.setVisibility(View.GONE);
		}
	}

	public void setTitleName() {
		if (titlename != null && getActivity() != null
				&& getActivity().getIntent() != null) {
			String name = getProgramName(getActivity().getIntent());
			if (name == null) {
				name = "";
			}
			titlename.setText(name);
		}
	}
	public void setTitleName(String title){
		titlename.setText(title);
	}

	public void showBackLookUi() {
		if (playType == LIVE_PLAY) {
			if (!playerActivity.isBackLook) {
				rootView.findViewById(R.id.time_layout)
						.setVisibility(View.GONE);
				mController.setBackgroundColor(Color.TRANSPARENT);
			} else {
				rootView.findViewById(R.id.time_layout).setVisibility(
						View.VISIBLE);
				mController.setBackgroundResource(R.drawable.player_bg);
			}
			setSourceBtn(playerActivity.isBackLook);
		}
	}

	private boolean setVisible = true;

	public void showButtonRight(boolean visible) {
		if (visible) {
			setButtonRightVisible();
		} else {
			setButtonRightHide();
		}
	}

	public void setButtonRightVisible() {
		if (playType == LIVE_PLAY) {
			changeSourceBtn.setVisibility(View.VISIBLE);
			changeTvBtn.setVisibility(View.VISIBLE);
			changeProgramBtn.setVisibility(View.VISIBLE);
		}
	}

	public void setButtonRightHide() {
		changeSourceBtn.setVisibility(View.GONE);
		changeTvBtn.setVisibility(View.GONE);
		changeProgramBtn.setVisibility(View.GONE);
	}

	public void setSourceBtn(boolean isBackLook) {
		if (isBackLook) {
			changeSourceBtn.setVisibility(View.GONE);
		} else {
			changeSourceBtn.setVisibility(View.VISIBLE);
		}
	}

	public void setNetLine(int position) {
		// 由子类覆写
	}

	public void sendQuitMessage(int delaytime) {
		if (mUIHandler != null) {
			mUIHandler.sendEmptyMessageDelayed(QUIT, delaytime);
		}
	}

	public void removeQuitMessage() {
		if (mUIHandler != null) {
			mUIHandler.removeMessages(QUIT);
		}
	}

	public void goHome() {
		//在子类里面覆盖该方法实现跳转
	}
	public void getSubVideoInfo(int programId,int subId,int position,int flag,String version){
		//在子类里面覆盖该方法实现获取视频
	}
	public void getSubVideoInfo(int programId,int subId,int position,int flag){
		//在子类里面覆盖该方法实现获取视频
	}
	public long curPlayTime;
	public void setCurPlayTime(){
		//在子类里面覆盖该方法计算当前视频播放时长
	}

	public void dismissAd(){

	}
}
