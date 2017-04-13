package com.sumavision.talktv.videoplayer.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionWithParamListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.baidu.cyberplayer.utils.VersionManager;
import com.baidu.cyberplayer.utils.VersionManager.CPU_TYPE;
import com.sumavision.cachingwhileplaying.CachingWhilePlayingService;
import com.sumavision.cachingwhileplaying.entity.BufferedPositionInfo;
import com.sumavision.cachingwhileplaying.entity.PreLoadingResultInfo;
import com.sumavision.cachingwhileplaying.util.CrackResultListener;
import com.sumavision.cachingwhileplaying.util.ParserUtil;
import com.sumavision.crack.UpdateCrackDialog;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.activity.WebPlayerActivity;
import com.sumavision.talktv.videoplayer.dao.AccessProgram;
import com.sumavision.talktv.videoplayer.data.BVideoError;
import com.sumavision.talktv.videoplayer.utils.CommonUtils;
import com.sumavision.talktv.videoplayer.utils.ConnectivityReceiver;
import com.sumavision.talktv.videoplayer.utils.CpuData;
import com.sumavision.talktv.videoplayer.utils.GetCpuVersionTask;
import com.sumavision.talktv.videoplayer.utils.GetDownloadUrlForCurrentVersionTask;
import com.sumavision.talktv.videoplayer.utils.NetConnectionListener;
import com.sumavision.talktv.videoplayer.utils.SoDownLoadTask;
import com.sumavision.talktv2.bean.EpisodeEvent;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.tvata.p2p.P2PManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

//import com.example.letvt1.MyLiveService;
//import com.example.letvt1.TempService;
//import com.vbyte.p2p.P2PModule;

/**
 * 播放器主页<br>
 * 设置自动播放上下集：sp名称：PlayerActivity.SP_AUTO_PLAY,key=auto;
 *
 * @author suma-hpb
 */
public final class PlayerActivity extends FragmentActivity implements
		OnPreparedListener, OnCompletionListener, OnErrorListener,
		OnInfoListener, OnPlayingBufferCacheListener, OnClickListener,
		CrackResultListener {
	private MediaFragmentHelper mFragmentHelper;
	private String p2pChannel;
//	private LetvServerAIDL liveService;
	private String userNat,userMAC;

	/**
	 * “p2p://”开头的直播源,经过服务器破解后的结果
	 */
	private String p2pServerCrackResult = "";

	protected MediaFragmentHelper getFragmentHelper() {
		if (mFragmentHelper == null) {
			mFragmentHelper = MediaFragmentHelper.getInstacne();
		}
		return mFragmentHelper;
	}

	public static final String SP_AUTO_PLAY = "autoPlay";
	private String mVideoSource = null;
	private int mLastPos = 0;

	protected enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED, PLAYER_UNINIT;
	}

	private RelativeLayout controlLayout;
	private RelativeLayout playerLayout;
	private RelativeLayout errLayout;
	private ImageButton retry;
	private TextView errorText;
	public MediaControllerFragment mediaControllerFragment;
	protected PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	private BVideoView mVV = null;
	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;
	private final Object SYNC_Playing = new Object();
	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	private TextView loadingTextView;
	public RelativeLayout loadingLayout;
	private final String percentSuf = "%";
	private CpuData cpuData = null;
	private int mBufferingPercent;
	public boolean isPrepared = false;
	public long activityId;
	public String channelId;
	private static final String TAG = "PlayerActivity";

	private static final int COMPLETE = 1;
	private static final int DOWNLOAD = 2;
	private static final int EXECUTE_DOWNLOAD = 3;

	private boolean mIsHwDecode = false;// 机顶盒用硬解 手机用软解
	private boolean needCaching = false; // 是否使用边看边缓功能
	public static final String TAG_INTENT_CACHE = "needCaching";// 是否使用边看边缓功能-intent_key
	public static final String TAG_INTENT_URL = "url";// 网页源-intent_key
	public static final String TAG_INTENT_PATH = "path";// 播放源-intent_key
	public static final String TAG_INTENT_PLAYTYPE = "playType";// 播放类型-intent_key
	public static final String TAG_INTENT_PROGRAMID = "id";// 节目ID-intent_key
	public static final String TAG_INTENT_STANDURL = "standUrl";// 标清源-intent_key
	public static final String TAG_INTENT_HIGHURL = "highUrl"; // 高清源-intent_key
	public static final String TAG_INTENT_SUPERURL = "superUrl"; // 超清源-intent_key
	public static final String TAG_INTENT_NEEDPARSE = "needVParse"; // 是否需要破解-intent_key
	public static final String TAG_INTENT_PROGRAMNAME = "programName";// 节目名称-intent_key
	public static final String TAG_INTENT_SUBID = "subid";// 子节目ID-intent_key
	public static final String TAG_INTENT_TITLE = "title";// 标题-intent_key
	public static final String TAG_INTENT_ICON = "icon";// 应用logo

	public static final String INTENT_NEEDAVOID = "needAvoid";// 是否开启版权规避，用于版权规避页

	private final int EVENT_PLAY = 1;
	private final int EVENT_PAUSE = 16;
	private final int UI_EVENT_BUFFER_START = 2;
	private final int UI_EVENT_BUFFER_END = 3;
	private final int UI_EVENT_PREPARED = 4;
	private final int UI_BUFFER_CACHE_PERCENT = 5;
	private final int UI_ERROR = 6;
	private final int UI_RESTART = 8;
	private final int UI_SWITCH_PLAY = 9;
	private final int UI_EVENT_UPDATE_BUFFER = 10;
	private final int SOURCE_INVALID = 11;
	private final int TOUCH_GUIDE = 12;
	private final int SWITCH_LIVE_NUMBER = 13;
	private final int RETRY = 14;
	private final int PLAY_OVER = 15;
	private final int CHANNEL_FEEDBACK = 17;
	private final int HANDLE_CRACK_RESULT = 18;
	public static final String PLAY_URL = "play_url";
	public static final String PLAY_POINT = "break_point";
	public static final String PLAY_PARSE = "can_parse";

	public static final int VOD_PLAY = 2;
	public static final int LIVE_PLAY = 1;
	public static final int CACHE_PLAY = 3;
	public static final int ACTIVITY_PLAY = 4;
	public static final int SHAKE_PLAY = 5;

	public String url;// 网页地址
	public String showUrl;//显示地址(假地址)
	public int playType;
	public String path;
	private String standpath;
	private String highpath;
	private String superpath;
	String programId;
	public int subid;
	public String programName;
	public ArrayList<JiShuData> jiShuDatas = new ArrayList<JiShuData>();// 點播列表
	public List<NetPlayData> playUrlList;// 直播地址列表
	public boolean playCompleted = false;
	private boolean changeSourceUrl;
	public static boolean IS_DOWNLOADING_STATUS = false;
	public boolean isOnpause = false;
	public boolean needAvoid = false;
	public boolean isCrackComplete = false;

	private String type; // 破解后的视频类型，m3u8使用边看边缓，mp4不使用
	private boolean getUrl = false;
	private int MAX_WAIT = 30000;
	private SharedPreferences helpSp;
	private ViewStub helpStub;
	private boolean hasShowedHelp;
	private boolean hasShowedBack;
	public int lineNumber;// 当前网络索引
	public int maxLine;// 最大网络数
	public boolean isBackLook;
	public boolean switchStop = false;
	public boolean isHalf;
	public int enterType = 0;

	public TextView sourceText,vipText;
	private boolean getParseUrlAfterOnPause;
	public boolean switchEpisode;
//	private PhoneReceiver phoneReceiver;
//	public static boolean isPhoneCalled;

	boolean isLandscape;//是否是横屏
	boolean isEnlargeClicked;//是否点击切换横竖屏
	boolean canFull = true;
	OrientationEventListener mScreenOrientationEventListener;
	public boolean getDataError;
	public String webPage="";

	public JSONArray channelStateArray = new JSONArray();
	int bufferNum;
	int channelNetId;
	JSONObject channelStatObj =  new JSONObject();



	public ImageView loadingIcon;
	long lastBytes = 0;
	long currBytes;
	private Handler speedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			lastBytes = currBytes;
			currBytes = CommonUtils.getNetSpeed(PlayerActivity.this);
			if (msg.what == 7) {
				long curr = currBytes - lastBytes;
				if (lastBytes == 0) {
					curr = 0;
				}
				String text = loadingTextView.getText().toString();
				int index = text.indexOf("(");
				if (text.contains(getString(R.string.loading_text))) {
					if (index == -1) {
						loadingTextView.append("(" + (curr / 1024) + "k/s)");
					} else {
						loadingTextView.setText(text.substring(0, index) + "("
								+ (curr / 1024) + "k/s)");
					}
				}
				if (playType == LIVE_PLAY) {
					String text2 = startText.getText().toString();
					int index2 = text2.indexOf("(");
					if (index2 == -1) {
						startText.append("(" + (curr / 1024) + "k/s)");
					} else {
						startText.setText(text2.substring(0, index2) + "("
								+ (curr / 1024) + "k/s)");
					}
				}
			}
			speedHandler.removeMessages(7);
			speedHandler.sendEmptyMessageDelayed(7, 1000);
			super.handleMessage(msg);
		}
	};

	// private TextView loadtimes;

	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case EVENT_PLAY:
					if (needCaching && playType != LIVE_PLAY && playType != 4) {
						setCachingPlayUrl();
					} else {
						Log.i(TAG, "mVideoSource=" + mVideoSource);
						mVV.setVideoPath(mVideoSource);
					}
					mLastPos = getHistoryPlayPosition();
					if (mLastPos > 0) {
						mVV.seekTo(mLastPos);
						mLastPos = 0;
					}
					mVV.showCacheInfo(false);
					if (!TextUtils.isEmpty(mVideoSource) && mVideoSource.contains("baidu")) {
						mVV.setUserAgent("netdisk;7.5.0;MI+2;android-android;4.1.1");
					}
					Log.i(TAG, "mvv.start()");
					Log.e("msg_path", mVideoSource + "****" + cachingWhilePlayingUrl);
//					if (playType == LIVE_PLAY && !isBackLook ){
//                        if (p2p != null){
//                            p2p.closeModule();
//							p2p = null;
//                        }
//                        if (!TextUtils.isEmpty(p2pChannel)){
//                            initP2P();
//                            mVideoSource = p2p.getPlayPath(PlayerActivity.this, p2pChannel);
//                            mVV.setVideoPath(mVideoSource);
//                        }
//					}
//					Log.e("tvfan_subId","eventplay_"+subid);
					mVV.start();
					mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
					if (playType == LIVE_PLAY && !isBackLook) {
						mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
						mUIHandler
								.sendEmptyMessageDelayed(SWITCH_LIVE_NUMBER, 15 * 1000);
					}
					break;
				case EVENT_PAUSE:
					break;
				default:
					break;
			}
		}
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UI_EVENT_BUFFER_START:
					if (isPlaying()){
						loadingLayout.setVisibility(View.VISIBLE);
						loadingTextView.setVisibility(View.VISIBLE);
					}
					errLayout.setVisibility(View.GONE);
					break;
				case UI_EVENT_BUFFER_END:
					loadingLayout.setVisibility(View.GONE);
					loadingTextView.setVisibility(View.GONE);
					mBufferingPercent = 0;
					break;
				case UI_BUFFER_CACHE_PERCENT:
					long curr = currBytes - lastBytes;
					if (lastBytes == 0) {
						curr = 0;
					}
					loadingTextView.setText(mBufferingPercent + percentSuf + "("
							+ (curr / 1024) + "k/s)");
					break;
				case UI_EVENT_PREPARED:
					resetLoadingBackground();
					loadingLayout.setVisibility(View.GONE);
					loadingLayout.setBackgroundResource(R.color.c_null);
					if (startLoadingLayout != null) {
						startLoadingLayout.setVisibility(View.GONE);
					}
					break;
				case UI_ERROR:
//				PlayerActivity.this.finish();
					resetLoadingLogo(3);
					loadingLayout.setVisibility(View.GONE);
					isError = true;
					break;
				case UI_RESTART:
					restartPlay(getIntent());
					break;
				case UI_SWITCH_PLAY:
					mediaControllerFragment.setReportEnable(true);
					mediaControllerFragment.switchPlayPreNext = true;
					mediaControllerFragment.playPreOrNext();
					isPrepared = false;
					break;
				case UI_EVENT_UPDATE_BUFFER:
					int pos = mVV.getCurrentPosition();
					if (pos != 0 && needCaching) {
						float bufferedposition = (Float) msg.obj;
						if (bufferedposition == -1) {
							if (changeSourceUrl) {
								bufferedposition = 0;
								changeSourceUrl = false;
							} else {
								bufferedposition = mVV.getDuration();
							}
						}
						mediaControllerFragment
								.setSecondaryProgress((int) bufferedposition);
					}
					mUIHandler.removeMessages(UI_EVENT_UPDATE_BUFFER);
					break;
				case SOURCE_INVALID:
					if (!isPrepared || !getUrl || mVV == null
							|| (!mVV.isPlaying() && !isOnpause)) {
						if (playType == VOD_PLAY) {
							getFragmentHelper().disablePlayVod(subid, 0);
						} else if (playType == SHAKE_PLAY) {
							getFragmentHelper().disablePlayVod(Integer.parseInt(programId), 1);
						}
					}
					mUIHandler.removeMessages(SOURCE_INVALID);
					break;
				case TOUCH_GUIDE:
					firstTouchId.setVisibility(View.VISIBLE);
					break;
				case SWITCH_LIVE_NUMBER:
					// TODO
					if (parserUtil != null) {
						parserUtil.stop();
					}
//				switchStop = true;
//				mVV.stopPlayback();
					Log.e("msg_handler", "switch line number");
					tryLivePlay(false);
					break;
				case RETRY:
					changeVideo = true;
					mVV.stopPlayback();
					break;
				case PLAY_OVER:
					if (playType == VOD_PLAY || playType == SHAKE_PLAY) {
						if (enterType == 1) {
							changeOrientation(true);
						}
						if (isError) {
							break;
						}
						showStartplay();
					}
					break;
				case CHANNEL_FEEDBACK:
					try {
						channelStatObj.put("type", 1);
						channelStatObj.put("caches", bufferNum);
						channelStatObj.put("createTime", System.currentTimeMillis() / 1000);
						channelStateArray.put(channelStatObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					sendChannelFeedbackMsg();
					bufferNum = 0;
					break;
				case HANDLE_CRACK_RESULT:
					handleCrackResult((PreLoadingResultInfo) msg.obj);
					break;
				default:
					break;
			}
		}
	};

	private RelativeLayout firstTouchId;
	public LinearLayout startLoadingLayout;
	private TextView startText;
	public RelativeLayout loadingAdLayout;
	ReportAdapter reportAdapter;
	public RelativeLayout root1;
	public boolean isError;//播放器是否出错
	public Display display;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (Build.VERSION.SDK_INT < 13) {
			setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		}
		super.onCreate(savedInstanceState);
		// 可能会造成一些界面的问题 因此先注释掉
		// if (Build.VERSION.SDK_INT >= 19) {// >=4.4
		// getWindow().getDecorView().setSystemUiVisibility(
		// View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		// | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
		// | View.SYSTEM_UI_FLAG_IMMERSIVE);
		// }
		Log.e(TAG, "oncreate");
		setContentView(R.layout.activity_mediaplayer);
		display = getWindowManager().getDefaultDisplay();;
		getVideoHeight();
		if(getIntent().getBooleanExtra("notice", false)){
			stopPreLoading();
		}
		root1 = (RelativeLayout) findViewById(R.id.root1);
		helpSp = getSharedPreferences("player_help", 0);
		currBytes = CommonUtils.getNetSpeed(this);

		playType = getIntent().getIntExtra("playType", CACHE_PLAY);
		programId = String.valueOf(getIntent().getIntExtra(
				TAG_INTENT_PROGRAMID, 0));
		if("0".equals(programId)){
			programId = String.valueOf(getIntent().getLongExtra(
					TAG_INTENT_PROGRAMID, 0));
		}
		channelId = String.valueOf(getIntent().getIntExtra("channelId", 0));
		subid = getIntent().getIntExtra(TAG_INTENT_SUBID, 0);
		getProgramName(getIntent());

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, POWER_LOCK);


		if (savedInstanceState == null) {
			savedInstanceState = getIntent().getExtras();
		} else {
			mLastPos = savedInstanceState.getInt(PLAY_POINT);
		}

		receiver = new ConnectivityReceiver();
		IntentFilter connFilter = new IntentFilter();
		connFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(receiver, connFilter);
		loadingLayout = (RelativeLayout) findViewById(R.id.progress_layout);
		playerLayout = (RelativeLayout) findViewById(R.id.videoviewholder);
		controlLayout = (RelativeLayout) findViewById(R.id.control_layout);
		loadingTextView = (TextView) findViewById(R.id.loading_text);
		sourceText = (TextView) findViewById(R.id.media_loading_source);
		sourceText.setOnClickListener(this);
		sourceText.setVisibility(View.GONE);
		vipText = (TextView) findViewById(R.id.media_loading_vip);
		vipText.setVisibility(View.GONE);
		errLayout = (RelativeLayout) findViewById(R.id.media_error_layout);
		retry = (ImageButton) findViewById(R.id.media_error_reload);
		retry.setOnClickListener(this);
		errorText = (TextView) findViewById(R.id.media_error_text);
		loadingTextView.setText(R.string.loading_text);
		loadingIcon = (ImageView) findViewById(R.id.media_error_logo);
		loadingIcon.setOnClickListener(this);
		if (playType == LIVE_PLAY) {
			loadingLayout.setVisibility(View.GONE);
			ViewStub vs = (ViewStub) findViewById(R.id.loading_stub);
			startLoadingLayout = (LinearLayout) vs.inflate();
			startLoadingLayout.setOnClickListener(this);
			startText = (TextView) findViewById(R.id.loading_logo_text);
			setStartTextLine(lineNumber);
			loadingAdLayout = (RelativeLayout) findViewById(R.id.loading_ad_frame);
		}

		if (needLoadHelpStub()) {
			initHelpStub();
		}

		doInit();
		if (isHalf) {
			loadingLayout.setVisibility(View.GONE);
			errLayout.setVisibility(View.VISIBLE);
			setHalfScreenView();
		}
		speedHandler.sendEmptyMessageDelayed(7, 1000);

		if (mUIHandler != null) {
			mUIHandler.sendEmptyMessageDelayed(SOURCE_INVALID, MAX_WAIT);
		}
		EventBus.getDefault().registerSticky(this);

		if (playType == LIVE_PLAY){
			startP2PService();
			//获取用户的NAT值
			if(getNATValue()!=null){
				userNat =getNATValue().trim();
				Log.d(TAG, "用户的NAT值为...." + userNat);
			}
			//获取用户的MAC地址值
			userMAC =getLocalMacAddress().trim();
			Log.d(TAG,"用户的MAC值为...."+userMAC);
			needCaching = false;
		}

	}
	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	private void initHelpStub() {
		helpStub = (ViewStub) findViewById(R.id.first_help_stub);
		helpStub.inflate();
		firstTouchId = (RelativeLayout) findViewById(R.id.first_touch_id);
		firstTouchId.setOnClickListener(this);
		firstTouchId.setVisibility(View.GONE);
		if (playType == LIVE_PLAY && !isBackLook) {
			findViewById(R.id.first_touch_progress).setVisibility(View.GONE);
		}
	}

	private boolean needLoadHelpStub() {
		return ((helpSp.getInt("helpCount_live", 0) < 3
				&& playType == LIVE_PLAY && !isBackLook) || (playType != LIVE_PLAY && helpSp
				.getInt("helpCount_play", 0) < 1))
				&& !hasShowedHelp
				|| ((isBackLook && helpSp.getInt("helpCount_back", 0) < 3) && !hasShowedBack);
	}

	ConnectivityReceiver receiver = null;

	public void doInit() {
		Log.e("msg_method", "doInit");
		getExtras();
		EventBus.getDefault().removeAllStickyEvents();
		mVideoSource = path;
		if (playType != VOD_PLAY) {
			if (playType == LIVE_PLAY && !isBackLook) {
				if (TextUtils.isEmpty(url)) {
					getUrl = true;
				}
			} else {
				getUrl = true;
			}
		}
		initPlayer();
		if (mediaControllerFragment == null) {
			addMediaControlFragment();
		}
		if (playType == VOD_PLAY && isHalf) {
			addProgramHalfFragment(programHalfFragmentName);
			changeStatuBar(false);
			int tempSubid = getPlaySubId(this,Integer.parseInt(programId),getIntent().getIntExtra("ptype",0),getIntent().getIntExtra("where",0));
			mediaControllerFragment.getSubVideoInfo(Integer.parseInt(programId),
					tempSubid, 0, 1);
		}else if(playType == SHAKE_PLAY && isHalf){
			addProgramHalfFragment(shakeProgramDetailFragmentName);
			changeStatuBar(false);
		} else {
			changeOrientationOnly();
		}

		if (mHandlerThread == null) {
			mHandlerThread = new HandlerThread("event handler thread",
					Process.THREAD_PRIORITY_BACKGROUND);
			mHandlerThread.start();
			mEventHandler = new EventHandler(mHandlerThread.getLooper());
		}
		if (!isUseDefault()) {
			if (isInititaled()) {
				setLibsDirectory();
			} else {
				mPlayerStatus = PLAYER_STATUS.PLAYER_UNINIT;
				loadingTextView.setText(R.string.loading_init_text);
				checkCpuVersion();
			}
		}
//			Intent intent = new Intent();
//			intent.setClass(getApplicationContext(), LeService.class);
//			bindService(intent, conn, Context.BIND_AUTO_CREATE);
		if (playType == LIVE_PLAY){

//			Log.e("msg_doInit_bind",System.currentTimeMillis()+"");

			channelStatObj = new JSONObject();
			try {
				channelStatObj.put("channelId", channelId);
				channelStatObj.put("playId", channelNetId);
				channelStatObj.put("type", 0);
				channelStatObj.put("stat", 0);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		curSubId = subid;
//		Log.i("tvfan_subId","doinit_"+curSubId);
		if (playType != VOD_PLAY
				&& mPlayerStatus != PLAYER_STATUS.PLAYER_UNINIT
				&& playType != SHAKE_PLAY && !(playType == LIVE_PLAY && !getUrl && !isBackLook)){
			needCaching = false;
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}else{
			if (!isHalf || (playType == LIVE_PLAY && !getUrl && !isBackLook)){
				prepareToPlay();
			}
		}
	}
	private void prepareToPlay(){
		if (playType == LIVE_PLAY && timerTask != null) { // 清除p2p的log发送任务
			timerTask.cancel();
		}
		boolean direct = path.endsWith(".m3u8") || path.endsWith(".mp4");
		if (path.contains("pcs.baidu")||path.contains("112.91.94.240")){
			direct = false;
		}
		if ((playType == VOD_PLAY || playType == SHAKE_PLAY)&&!TextUtils.isEmpty(path)){
			direct = true;
		}
		if ((playType == VOD_PLAY || playType == SHAKE_PLAY) && !direct) {
			startPreLoading(url, Integer.parseInt(programId), subid);
			Log.i("PlayerActivity", "restart startPreLoading");
		} else {
			if (playType == LIVE_PLAY && !isBackLook && !TextUtils.isEmpty(url) && !direct) {
//				if (TextUtils.isEmpty(p2pChannel)){
				doLiveCrack(url);
//				}else {
//					startP2pLive();
//				}
			} else {
				isCrackComplete = false;
				url = path;
				standpath = "";
				highpath = "";
				superpath = "";
				mediaControllerFragment.setCrackResource(null, null, null);
				needCaching = false;
				mEventHandler.sendEmptyMessageDelayed(EVENT_PLAY, 200);
			}
		}
	}
	//	ServiceConnection conn = new ServiceConnection() {
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			liveService = null;
//		}
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			liveService = LetvServerAIDL_Stub.asInterface(service);
////			Log.e("msg_bind_complete",System.currentTimeMillis()+"");
////			if (!TextUtils.isEmpty(url) && url.startsWith("p2p://")) {
//				if (!TextUtils.isEmpty(p2pServerCrackResult)) {
//				// 服务器破解已经结束，再将破解结果传回crackResult()
//					PreLoadingResultInfo tmp = new PreLoadingResultInfo();
//					tmp.path = p2pServerCrackResult;
//					crackResult(tmp);
//				}
////				return;
////			}
//			EventBus.getDefault().post(new EventMessage("Player_Letv_Ready"));
//		}
//	};
	public boolean isTvType(){
		int type = getIntent().getIntExtra("ptype", 0);
		if (type == 1 || type == 11){
			return true;
		}
		return false;
	}
	private int getPlaySubId(Context context, int programId, int type, int where) {
		int result = PreferencesUtils.getInt(context,"playRecord",programId+"",subid);
		if (type == 1 || type == 11){
			if (where == 4){
				result = subid;
			}
		}else {
			result = subid;
		}
		return result;
	}

	private int liveIndex;

	/**
	 * 获取数据
	 */
	@SuppressWarnings("unchecked")
	private void getExtras() {
		Log.e("msg_method", "getExtras");
		Intent intent = getIntent();
		url = intent.getStringExtra(TAG_INTENT_URL);
		needCaching = intent.getBooleanExtra(TAG_INTENT_CACHE, true);
		needAvoid = intent.getBooleanExtra(INTENT_NEEDAVOID, false);
		playType = intent.getIntExtra(TAG_INTENT_PLAYTYPE, VOD_PLAY);
		path = intent.getStringExtra(TAG_INTENT_PATH);
		isBackLook = intent.getBooleanExtra("isBackLook", false);
		isHalf = intent.getBooleanExtra("isHalf", false);
		String source = TextUtils.isEmpty(url) ? path : url;
		sourceText.setText("视频来源：" + source);
		if (!TextUtils.isEmpty(source) && (playType == VOD_PLAY || playType == SHAKE_PLAY)) {
			sourceText.setVisibility(View.VISIBLE);
			if (PreferencesUtils.getBoolean(this,null,"vipActivity",false)){
				vipText.setVisibility(View.VISIBLE);
			} else {
				vipText.setVisibility(View.GONE);
			}
		}
		if (isBackLook) {
			url = path;
		}
		if (path == null) {
			path = "";
		} else {
			if (TextUtils.isEmpty(url)) {
				url = path;
			}
		}
		standpath = intent.getStringExtra(TAG_INTENT_STANDURL);
		highpath = intent.getStringExtra(TAG_INTENT_HIGHURL);
		superpath = intent.getStringExtra(TAG_INTENT_SUPERURL);
		programId = intent.getIntExtra(TAG_INTENT_PROGRAMID, 0) + "";
		if (programId.equals("0")) {
			programId = intent.getLongExtra(TAG_INTENT_PROGRAMID, 0) + "";
		}
		subid = intent.getIntExtra(TAG_INTENT_SUBID, 0);
		channelId = String.valueOf(getIntent().getIntExtra("channelId", 0));
		if (playType == VOD_PLAY || playType == CACHE_PLAY) {// 获取点播列表
			getJishuExtra();
		} else if (playType == LIVE_PLAY && !isBackLook) {// 直播地址列表
			liveIndex = intent.getIntExtra("livepos", 0);
			livePosition = liveIndex;
			if (intent.hasExtra("NetPlayData")) {
				playUrlList = (ArrayList<NetPlayData>) intent
						.getSerializableExtra("NetPlayData");
				path = playUrlList.get(liveIndex).videoPath;
				url = playUrlList.get(liveIndex).url;
				showUrl = playUrlList.get(liveIndex).showUrl;
				p2pChannel = playUrlList.get(liveIndex).channelIdStr;
				webPage = playUrlList.get(liveIndex).webPage;
				channelNetId = playUrlList.get(liveIndex).id;
				if (path == null){
					path = "";
				}
				if (playUrlList != null && playUrlList.size() > 5) {
					playUrlList = playUrlList.subList(0, 5);
				}
				maxLine = playUrlList.size();
			}
		}
		getProgramName(intent);

		if (playType == CACHE_PLAY) {
			Uri uri = this.getIntent().getData();
			if (uri != null) {
				Log.e(TAG, "uri" + uri.toString());
				if (uri.toString().startsWith("tvfanplayurl")) {
					String str = "http://"
							+ uri.toString().substring(15,
							uri.toString().length());

					if (!TextUtils.isEmpty(str)) {
						path = str;
						Log.e(TAG, path);
					}
				} else if (uri.toString().endsWith("tvfanplayurl")) {
					String str = uri.toString().substring(0,
							uri.toString().length() - 12);
					if (!TextUtils.isEmpty(str)) {
						path = str;
					}
				} else if (uri.toString().startsWith("tvfanvideoweixin")) {
					String str = uri.toString().substring(19,
							uri.toString().length());
					if (!str.startsWith("http:")) {
						StringBuffer sb = new StringBuffer(str);
						sb.insert(4, ":");
						str = sb.toString();
					}
					if (str != null && !str.equals("")) {
						path = str;
						Log.e(TAG, path);
					}
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.not_support_link, Toast.LENGTH_SHORT)
							.show();
					finish();
					return;
				}
			}
		}
	}

	public void getJishuExtra() {
		if (getIntent().hasExtra("subList")) {
			jiShuDatas.clear();
			jiShuDatas.addAll((ArrayList<JiShuData>) getIntent()
					.getSerializableExtra("subList"));
			Log.i(TAG, "size = " + jiShuDatas.size());
			subid = jiShuDatas
					.get(getIntent().getIntExtra("mPosition", 0)).id;
		}
	}

	public String getProgramName(Intent intent) {
		programName = intent.getStringExtra(TAG_INTENT_PROGRAMNAME);
		if (TextUtils.isEmpty(programName)) {
			programName = intent.getStringExtra(TAG_INTENT_TITLE);
		} else {
			StringBuffer sb = new StringBuffer(programName);
			sb.append(" ").append(intent.getStringExtra(TAG_INTENT_TITLE));
			programName = sb.toString();
		}
		if (TextUtils.isEmpty(programName)) {
//			programName = getString(R.string.player_default_name);
			programName = "";
		}
		return programName;
	}

	private Boolean onCompletionExecuted = false;

	private void initPlayer() {
		String AK = "qSN3scX2ct8hpaD2zd6VLxlT";
		String SK = "8Tn38lA9IgpmXqMo";
		BVideoView.setAKSK(AK, SK);
		mVV = (BVideoView) findViewById(R.id.video_view);
		mVV.setOnCompletionListener(this);
		mVV.setOnPreparedListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVV.showCacheInfo(false);
		mVV.setOnCompletionWithParamListener(new OnCompletionWithParamListener() {

			@Override
			public void OnCompletionWithParam(int arg0) {
				Log.e(TAG, "OnCompletionWithParam:" + arg0);
				if (arg0 == BVideoView.MEDIA_ERROR_INVALID_INPUTFILE) {
					return;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!onCompletionExecuted) {
					onCompleteHandler();
				}

			}
		});
		mVV.setOnPlayingBufferCacheListener(this);

		mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW
				: BVideoView.DECODE_SW);
	}
//	P2PModule p2p;
//    public void initP2P(){
//        p2p = P2PModule.getInstance("vbyte-v7a");
////        p2p.setP2PHandler(new StarP2PHandler(this));
//    };

	public static String controllerFragmentName = MediaControllerFragment.class
			.getName();
	public static String programHalfFragmentName,shakeProgramDetailFragmentName;

	private void addMediaControlFragment() {
		FragmentManager manager = getSupportFragmentManager();
		if(mediaControllerFragment != null){
			manager.beginTransaction().remove(mediaControllerFragment).commitAllowingStateLoss();
		}
		FragmentTransaction ft = manager.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putString("title", programName);
		bundle.putInt("playType", playType);
		bundle.putString("path", path);
		Fragment fragment = Fragment.instantiate(getApplicationContext(),
				controllerFragmentName, bundle);
		ft.replace(R.id.control_layout, fragment);
		ft.commitAllowingStateLoss();
		mediaControllerFragment = (MediaControllerFragment) fragment;
		mediaControllerFragment.setButtonRightVisible();
	}

	Fragment programFragment;
	private void addProgramHalfFragment(String halfName) {
		FragmentManager manager = getSupportFragmentManager();
		if(programFragment != null){
			manager.beginTransaction().remove(programFragment).commitAllowingStateLoss();
		}
		FragmentTransaction ft = manager.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putLong("programId", Long.parseLong(programId));
		bundle.putInt("point", getIntent().getIntExtra("point", 0));
		bundle.putInt("subId",getPlaySubId(this,Integer.parseInt(programId),getIntent().getIntExtra("ptype",0),getIntent().getIntExtra("where",0)));
		Fragment fragment = Fragment.instantiate(getApplicationContext(),
				halfName, bundle);
		ft.replace(R.id.program_layout, fragment);
		ft.commitAllowingStateLoss();
		programFragment = fragment;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initOrientationEvent();
	}

	private int lanType = 1,curLan = 1;
	private void initOrientationEvent() {
		mScreenOrientationEventListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int orientation) {
				Log.e("msg", "listener"+orientation);
				if (mediaControllerFragment.isLocked){
					return;
				}
				if((orientation>=60 && orientation<=120) || (orientation>=240 && orientation<=300)){
					if (orientation>=240){
						curLan = 1;
					}else {
						curLan = 2;
					}
					if(isEnlargeClicked){
						if(!isLandscape && canFull){
							return;
						}else{
							isLandscape = true;
							canFull = false;
							isEnlargeClicked = false;
							CommonUtils.showMethodLog("Heng -- isClick And else");
							if(orientation>=240){
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
								lanType = 1;
							}else{
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
								lanType = 2;
							}
						}
					}else{
						if (isLandscape && !canFull && (curLan == lanType)){
							return;
						}
						if(orientation>=240){
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
							lanType = 1;
						}else{
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
							lanType = 2;
						}
						requestFullLayout();
						isLandscape = true;
						canFull = false;
						CommonUtils.showMethodLog("Heng_nature");
					}
				}else if((orientation<=30 && orientation>=0) || (orientation<=360 && orientation>=330)){
					if(isEnlargeClicked){
						if(isLandscape && !canFull){
							return;
						}else{
							isLandscape = false;
							canFull = true;
							isEnlargeClicked = false;
							CommonUtils.showMethodLog("Shu -- isClick And else");
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						}
					}else{
						if (!isLandscape && canFull){
							return;
						}
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						lanType = 1;
						requestHalfLayout();
						isLandscape = false;
						canFull = true;
						CommonUtils.showMethodLog("Shu_nature");
					}
				}
			}
		};
	}
	public void enlargeClick(){
		isEnlargeClicked = true;
		if(isLandscape){
			//切至半屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			canFull = true;
			requestHalfLayout();
			CommonUtils.showMethodLog("click_toHalf");
		}else{
			//切至全屏
			if (lanType == 2){
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			}else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			canFull = false;
			requestFullLayout();
			CommonUtils.showMethodLog("orientation_tofull"+getRequestedOrientation());
			CommonUtils.showMethodLog("click_toFull");
		}
		isLandscape = !isLandscape;
	}
	int i = 0;
	@Override
	protected void onResume() {
		super.onResume();
		if (i>0){
					excuteTimerTask();
		}
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
		Log.e(TAG, "*** onResume");
		CommonUtils.showMethodLog("onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}
		if (mPlayerStatus != PLAYER_STATUS.PLAYER_UNINIT)
			isOnpause = false;
		if (onCompletionExecuted || getParseUrlAfterOnPause) {
//			if (playType == LIVE_PLAY && !TextUtils.isEmpty(url)
////					&& (url.startsWith("Letv://") || url.startsWith("p2p://"))
//					){
//				doLiveCrack(url);
//			} else
			if (!getIntent().getBooleanExtra("isNative",true)){

			} else {
				mEventHandler.sendEmptyMessage(EVENT_PLAY);
			}
			getParseUrlAfterOnPause = false;
		}
		if (mScreenOrientationEventListener != null){
			mScreenOrientationEventListener.enable();
		}
		i++;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
		Log.e(TAG, "*** onPause " + mPlayerStatus);
		CommonUtils.showMethodLog("onPause");
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
		isOnpause = true;
		if (mVV != null) {
			mLastPos = mVV.getCurrentPosition();
			if(playType != LIVE_PLAY && mLastPos >0){
				saveProgram(mLastPos);
			}
		}
		if (mScreenOrientationEventListener != null){
			mScreenOrientationEventListener.disable();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(timerTask!=null){
			timerTask.cancel();
		}
		CommonUtils.showMethodLog("onStop");
		if (playType != LIVE_PLAY) {
			if (isPrepared || playCompleted) {
				saveProgram(mLastPos);
			}
		}
		try {
			if (mVV != null && mVV.isPlaying()) {
				mVV.pause();
				mediaControllerFragment.setPaused(true);
				mediaControllerFragment.playBtn.setSelected(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mLastPos = savedInstanceState.getInt(PLAY_POINT);
		Log.e(TAG, "onRestoreInstanceState");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timerTask!=null){
			timerTask.cancel();
		}
		/**
		 * 退出后台事件处理线程
		 */
		CommonUtils.showMethodLog("onDestory");
		if (receiver != null) {
			unregisterReceiver(receiver);
		}

//		if (phoneReceiver != null){
//			unregisterReceiver(phoneReceiver);
//		}

		if (parserUtil != null) {
			parserUtil.stop();
		}

		if (mHandlerThread != null) {
			mHandlerThread.quit();
			mUIHandler.removeMessages(UI_RESTART);
			mUIHandler.removeMessages(UI_ERROR);
			mUIHandler.removeMessages(SOURCE_INVALID);
			mUIHandler.removeMessages(UI_BUFFER_CACHE_PERCENT);
			mUIHandler.removeMessages(UI_EVENT_BUFFER_END);
			mUIHandler.removeMessages(UI_EVENT_BUFFER_START);
			mUIHandler.removeMessages(UI_EVENT_PREPARED);
			mUIHandler.removeMessages(UI_EVENT_UPDATE_BUFFER);
			mUIHandler.removeMessages(UI_SWITCH_PLAY);
			mEventHandler.removeMessages(EVENT_PLAY);
			mUIHandler.removeMessages(CHANNEL_FEEDBACK);
		}
		if (speedHandler != null) {
			speedHandler.removeMessages(7);
		}
		if (getIntent().getIntExtra("resultCode",0) == 101){
			EventBus.getDefault().post(new EventMessage("addProgram"));
		}
		try {
			if (playType == LIVE_PLAY){
//				if (p2p != null){
//					p2p.closeModule();
//					p2p = null;
//				}
				sendChannelFeedbackMsg();
			}
		} catch (Exception e){
			e.printStackTrace();
		}

//		if (liveService != null){
//			if (playType != LIVE_PLAY){
//				stopLeTvService();
//			}
////			unbindService(conn);
//		}
//		if (playType == LIVE_PLAY && p2pManager != null) {
//			p2pManager.get().stop();
//		}
		EventBus.getDefault().unregister(this);
		i=0;
	}
	private void sendChannelFeedbackMsg(){
		if (channelStateArray.length()>0){
			msg_channelStat.bundle.putString("content", channelStateArray.toString());
			EventBus.getDefault().post(msg_channelStat);
			channelStateArray = new JSONArray();
		}
	}
//	public void stopLeTvService(){
//		if (liveService != null){
////			try {
////				liveService.stopServer();
////				liveService.stopPlay();
////			} catch (RemoteException e) {
////				e.printStackTrace();
////			}
//		}
//	}

	@Override
	public boolean onInfo(int what, int extra) {
		switch (what) {
			case BVideoView.MEDIA_INFO_BUFFERING_START:

//			Log.e(TAG, "oninfo buffer start");
				if (playType == LIVE_PLAY) {
					mUIHandler.removeMessages(RETRY);
					mUIHandler.sendEmptyMessageDelayed(RETRY, 10000);
					bufferNum++;
				}
				mUIHandler.sendEmptyMessage(UI_EVENT_BUFFER_START);
				break;
			case BVideoView.MEDIA_INFO_BUFFERING_END:
//			Log.e(TAG, "oninfo buffer end");
				mUIHandler.sendEmptyMessage(UI_EVENT_BUFFER_END);
				break;
			case BVideoView.MEDIA_INFO_PLAYING_AVDIFFERENCE:
			case BVideoView.MEDIA_INFO_PLAYING_QUALITY:
				if (mBufferingPercent == 100) {
					mUIHandler.sendEmptyMessage(UI_EVENT_BUFFER_END);
				}
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public void onPlayingBufferCache(int percent) {
		mBufferingPercent = percent;
		if (percent != 0 && playType == LIVE_PLAY) {
			mUIHandler.removeMessages(RETRY);
		}
		mUIHandler.sendEmptyMessage(UI_BUFFER_CACHE_PERCENT);
		if (mediaControllerFragment.isPaused() && percent == 100) {
			mVV.pause();
		}
	}

	public int livePosition;

	@Override
	public boolean onError(int what, int extra) {
		CommonUtils.showMethodLog("onError");
		if (what == BVideoView.MEDIA_ERROR_INVALID_INPUTFILE
				|| what == BVideoView.MEDIA_ERROR_NO_INPUTFILE) {
			if (playType == VOD_PLAY) {
				getFragmentHelper().disablePlayVod(subid, 0);
			} else if (playType == SHAKE_PLAY) {
				getFragmentHelper().disablePlayVod(Integer.parseInt(programId), 1);
			}
		}
		Log.e(TAG, "onError  " + " what: " + BVideoError.getStringError(what)
				+ "  extra: " + extra);
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}

		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		if (!mHandlerThread.isAlive()) {
			mHandlerThread = new HandlerThread("event handler thread",
					Process.THREAD_PRIORITY_BACKGROUND);
			mHandlerThread.start();
			mEventHandler = new EventHandler(mHandlerThread.getLooper());
		}
		if (playType == LIVE_PLAY && !clickquit && playUrlList != null
				&& !isBackLook) {
			// tryLivePlay(true);
		} else {
			mUIHandler.sendEmptyMessageDelayed(UI_ERROR, 200);
		}
		return true;
	}

	private int tryLiveTimes = 1;
	private boolean firsttip = true;

	public void tryLivePlay(boolean auto) {
		Log.e("msg_live", lineNumber + "+" + livePosition);
		livePosition++;
		if (auto) {
			tryLiveTimes++;
		}
		try {
			channelStatObj.put("stat", 0);
			channelStatObj.put("createTime", System.currentTimeMillis() / 1000);
			channelStateArray.put(channelStatObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (playUrlList == null || playUrlList.size() == 0) {
			if (!isOnpause) {
				Toast.makeText(this, R.string.live_failed, Toast.LENGTH_SHORT)
						.show();
			}
			mediaControllerFragment.sendQuitMessage(10000);
			return;
		}
		if (tryLiveTimes > playUrlList.size() && firsttip) {
			firsttip = false;
			Toast.makeText(this, R.string.no_resource, Toast.LENGTH_SHORT)
					.show();
		}
		if (livePosition >= playUrlList.size()) {
			livePosition = livePosition % playUrlList.size();
		}
		path = playUrlList.get(livePosition).videoPath;
		url = playUrlList.get(livePosition).url;
		showUrl = playUrlList.get(livePosition).showUrl;
		p2pChannel = playUrlList.get(livePosition).channelIdStr;
		webPage = playUrlList.get(livePosition).webPage;
		channelNetId = playUrlList.get(livePosition).id;
		if (!auto) {
//			changeVideo = true;
			if (lineNumber >= maxLine) {
				lineNumber = lineNumber % playUrlList.size();
				if (!isOnpause) {
					Toast.makeText(this, R.string.live_failed, Toast.LENGTH_SHORT)
							.show();
				}
				mediaControllerFragment.sendQuitMessage(10000);
				return;
			} else {
				lineNumber++;
				setStartTextLine(lineNumber % maxLine);
				mediaControllerFragment.setNetLine(lineNumber % maxLine);
				mediaControllerFragment.setResourceText(url, path);
			}
		}
		if (path == null) {
			path = "";
		}

		Log.d(TAG,"tryLiveUrl"+path);
		Log.d(TAG ,"tryLiveUrl"+url);
		getIntent().putExtra("livepos", livePosition);
		changeVideo = true;
		if (!TextUtils.isEmpty(url)) {
			Log.e("msg_live", lineNumber + "url--" + url);
//			if (isPrepared) {
//				mVV.stopPlayback();
//			} else {

			// zhangyisu added
			if (mVV.isPlaying()) {
				mVV.stopPlayback();
			} else {
				mUIHandler.sendEmptyMessageDelayed(UI_RESTART, 2000);
			}
//			}
		} else {
			Log.e("msg_live",lineNumber+"path--"+path);
			isCrackComplete = false;
			mVideoSource = path;
			getIntent().putExtra("livepos", livePosition);
			mVV.stopPlayback();
			mEventHandler.sendEmptyMessageDelayed(EVENT_PLAY, 1000);
		}
	}

	ParserUtil parserUtil;

	private void doLiveCrack(String url) {
		if (parserUtil != null) {
			parserUtil.stop();
		}
//		if (url.startsWith("Letv://")) {
//			if (liveService != null) {
//                isCrackComplete = false;
//                mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
//				url = url.replace("Letv://", "");
//				PreLoadingResultInfo info = new PreLoadingResultInfo();
//				info.path = liveService.getPlayUrl(url, "http://127.0.0.1:8024");
//                crackResult(info);
//			} else {
//				Log.e("msg_live","bind live service failed");
//			}
//		} else {
//			if (url.startsWith("p2p://")) {
//				url = url.replace("p2p://","");
//			}
		parserUtil = new ParserUtil(this, this, url, 2, subid);
		isCrackComplete = false;
		mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
		Log.i(TAG, "cracking live");
//		}

	}

	private void getTempLeurl(){

	}

	public long videoTotalTime;
	int curpos1;
	EventMessage msg = new EventMessage("SlidingMainActivity_playinfo");
	EventMessage msg_channelStat = new EventMessage("SlidingMainActivity_channelStat");

	@Override
	public void onCompletion() {
		Log.e("msg_method", "onCompletion");
		if (playType == LIVE_PLAY || isBackLook) {
			getExtras();
//			stopLeTvService();
		}
		Log.e(TAG, "onCompletion  " + "videoTotalTime:" + videoTotalTime);
		Log.e(TAG, "onCompletion  " + "curpos:" + mediaControllerFragment.currPosition);
		if (mediaControllerFragment.currPosition>0){
			//统计播放信息(由slidingmainactivity发送)
			msg.bundle.putLong("progressTime",mediaControllerFragment.currPosition);
			msg.bundle.putLong("videoTime",videoTotalTime);
			mediaControllerFragment.setCurPlayTime();
			msg.bundle.putLong("curPlayTime", mediaControllerFragment.curPlayTime);
			EventBus.getDefault().post(msg);
		}
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		if (!onCompletionExecuted) {
			onCompleteHandler();
		}

	}

	private void onCompleteHandler() {
		Log.e("msg_method", "onCompleteHandler");
		if(mediaControllerFragment != null){
			mediaControllerFragment.setSecondaryProgress(0);
		}
		if (clickquit || onCompletionExecuted) {
			return;
		}
		onCompletionExecuted = true;
		if (switchEpisode || isOnpause) {
			return;
		}
		if (switchStop) {
			Log.e("msg_complete", "switchStop");
			switchStop = false;
			return;
		}
		if (changePath) {
			if (!TextUtils.isEmpty(type) && type.equals("m3u8")) {
				startChangePath(mVideoSource);
			} else {
				cachingWhilePlayingUrl = mVideoSource;
				mEventHandler.sendEmptyMessage(EVENT_PLAY);
			}
			return;
		}
		if (curpos1 == 0) {
			curpos1 = mVV.getCurrentPosition();
		}
		Log.e(TAG, "onCompletion  " + "current pos:" + curpos1);
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

		if (playType != LIVE_PLAY) {
			if ((videoTotalTime != 0 && !isOnpause)
					|| curpos1 == videoTotalTime) {
				playCompleted = true;
			}
			if (playCompleted) {
				closeOrPlayNext();
			}
		} else if ((playUrlList != null || isBackLook) && !clickquit) {
			if (changeVideo) {

//				Log.e("msg_complete", "live_changeVideo");
				changeVideo = false;
				mUIHandler.sendEmptyMessageAtTime(UI_RESTART, 300);
			} else {
				Log.e("msg_complete", "live_tryLivePlay");
				tryLivePlay(true);
			}
		}
		isPrepared = false;
	}

	protected void closeOrPlayNext() {
		Log.e("msg_method", "closeOrPlayNext");
		SharedPreferences auoPlaySp = getSharedPreferences(
				PlayerActivity.SP_AUTO_PLAY, Context.MODE_PRIVATE);
		boolean change = mediaControllerFragment.switchPlayPreNext
				|| auoPlaySp.getBoolean("auto", true);
		int pos = getIntent().getIntExtra("mPosition", -1);
		if (jiShuDatas != null && jiShuDatas.size() > 0 && change) {
			boolean myOrder = getIntent().getBooleanExtra("normalOrder", true);
			int type = getIntent().getIntExtra("ptype", 0);
			if ((type == 1 || type == 11) && !myOrder) {
				if (pos == 0) {
					mediaControllerFragment.getSubVideoInfo(Integer.parseInt(programId), jiShuDatas.get(pos).id, 1, 2);
				} else if (pos < jiShuDatas.size() && pos > 0) {
					stopPreLoading();
					mediaControllerFragment.playNext = false;
					mUIHandler.sendEmptyMessage(UI_SWITCH_PLAY);
				}
			} else {
				if (pos == jiShuDatas.size() - 1) {
					if (jiShuDatas.get(pos).id == curSubId && getIntent().getBooleanExtra("isLastStage", false)) {
						mUIHandler.sendEmptyMessage(PLAY_OVER);
					} else {
						mediaControllerFragment.getSubVideoInfo(Integer.parseInt(programId),
								curSubId <= 0 ? jiShuDatas.get(pos).id : curSubId, myOrder ? 1 : -1, 2);
					}
				} else if (pos < jiShuDatas.size() - 1) {
					stopPreLoading();
					mUIHandler.sendEmptyMessage(UI_SWITCH_PLAY);
				}
			}
		} else {
			if (subid == 0 || Integer.parseInt(programId) == 0
					|| subid == Integer.parseInt(programId)
					|| playType == CACHE_PLAY) {
				if ((playType == CACHE_PLAY || playType == SHAKE_PLAY) && programFragment != null) {
					mUIHandler.sendEmptyMessage(PLAY_OVER);
				} else {
					finish();
				}
			} else {
				if (getIntent().getIntExtra("resultCode", 0) == 101 || programFragment == null) {
					finish();
				} else {
					mUIHandler.sendEmptyMessage(PLAY_OVER);
				}
			}
		}
	}

	public void playOver() {
		mUIHandler.sendEmptyMessage(PLAY_OVER);
	}

	public boolean changePath = false;
	public boolean changeVideo = true;

	@Override
	public void onPrepared() {
		CommonUtils.showMethodLog("onPrepared");
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
		mUIHandler.sendEmptyMessage(UI_EVENT_PREPARED);
		mediaControllerFragment.mUIHandler
				.sendEmptyMessage(MediaControllerFragment.START_OPTION);
		isPrepared = true;
		playCompleted = false;
		onCompletionExecuted = false;
		switchEpisode = false;
		tryLiveTimes = 0;
		videoTotalTime = mVV.getDuration();
		if (mediaControllerFragment != null) {
			mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
			mediaControllerFragment.removeQuitMessage();
		}
		if (playType == LIVE_PLAY) {
			try {
				channelStatObj.put("stat", 1);
				channelStateArray.put(channelStatObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mUIHandler.sendEmptyMessageDelayed(CHANNEL_FEEDBACK, 5 * 60 * 1000);
		}
		if (needLoadHelpStub()) {
			if (playType == LIVE_PLAY) {
				if (isBackLook) {
					if (firstTouchId == null) {
						initHelpStub();
					} else {
						findViewById(R.id.first_touch_progress).setVisibility(
								View.VISIBLE);
					}
					hasShowedBack = true;
					helpSp.edit()
							.putInt("helpCount_back",
									helpSp.getInt("helpCount_back", 0) + 1)
							.commit();
				} else {
					hasShowedHelp = true;
					helpSp.edit()
							.putInt("helpCount_live",
									helpSp.getInt("helpCount_live", 0) + 1)
							.commit();
				}
			} else {
				hasShowedHelp = true;
				helpSp.edit()
						.putInt("helpCount_play",
								helpSp.getInt("helpCount_play", 0) + 1)
						.commit();
			}
			if(!isHalf){
				mUIHandler.sendEmptyMessage(TOUCH_GUIDE);
			}
		}
		if (playType == LIVE_PLAY) {
			mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
		}
		msg.bundle.putLong("progId",Long.valueOf(programId));
		msg.bundle.putLong("progSubId",curSubId);

//		Toast.makeText(getApplicationContext(), getNATValue(), 1).show();
	}

	private void checkCpuVersion() {
		cpuData = new CpuData();
		IS_DOWNLOADING_STATUS = true;
		new GetCpuVersionTask(netListener, "getCpuVersion").execute(callback);
	}

	private NetConnectionListener netListener = new NetConnectionListener() {
		@Override
		public void onNetEnd(int code, String msg, String method,
							 boolean isLoadMore) {
			if ("download".equals(method)) {
				Log.e(TAG, "msg=" + code);
				if (code == 0) {
					saveCpuInfo(true);
					setLibsDirectory();
					loadingTextView.setText(R.string.loading_text);
					if (getUrl  && !getIntent().getBooleanExtra("isHalf",false)) {
						mEventHandler.sendEmptyMessage(EVENT_PLAY);
					}
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.load_failed, Toast.LENGTH_SHORT).show();
				}
			}
		}

	};

	private VersionManager.RequestCpuTypeAndFeatureCallback callback = new VersionManager.RequestCpuTypeAndFeatureCallback() {
		@Override
		public void onComplete(CPU_TYPE arg0, int arg1) {
			boolean needDownloadUrl = false;
			cpuData.mType = arg0;
			if (cpuData.mType != CPU_TYPE.ARMV7_NEON) {
				needDownloadUrl = true;
			}
			if (!needDownloadUrl) {
				cpuHandler.sendEmptyMessage(COMPLETE);
			} else {
				cpuHandler.sendEmptyMessage(DOWNLOAD);
			}
		}
	};
	VersionManager.RequestDownloadUrlForCurrentVersionCallback urlcallback = new VersionManager.RequestDownloadUrlForCurrentVersionCallback() {

		@Override
		public void onComplete(String arg0, int arg1) {
			Log.i(TAG + "-urlback:", "arg0" + arg0 + ",arg1" + arg1);
			cpuData.downloadUrl = arg0;
			cpuHandler.sendEmptyMessage(EXECUTE_DOWNLOAD);
		}
	};

	private Handler cpuHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case COMPLETE:
					useDefault(true);
					loadingTextView.setText(R.string.loading_text);
					if (getUrl  && !getIntent().getBooleanExtra("isHalf",false)) {
						mEventHandler.sendEmptyMessage(EVENT_PLAY);
					}
					// MediaControllerFragment.IS_GUIDE_VISIBLE = true;
					break;
				case DOWNLOAD:
					new GetDownloadUrlForCurrentVersionTask(netListener,
							"getCpuVersion").execute(cpuData, cpuData.mType,
							urlcallback);
					break;
				case EXECUTE_DOWNLOAD:
					String dir = getFilesDir().getAbsolutePath();
					String path = dir + getPackageName() + "/";
					String fileName = "so.zip";
					new SoDownLoadTask(netListener, "download").execute(
							cpuData.downloadUrl, path, fileName);
					break;
				default:
					break;
			}
		};
	};

	private void setLibsDirectory() {
		String dir = getFilesDir().getAbsolutePath();
		File file = new File(dir + getPackageName() + "/libcyberplayer.so");
		File file1 = new File(dir + getPackageName()
				+ "/libcyberplayer-core.so");
		if (file.exists() && file1.exists()) {
			BVideoView.setNativeLibsDirectory(dir + getPackageName() + "/");
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		Log.e(TAG, "onSaveInstance");
		outState.putString(PLAY_URL, mVideoSource);
		outState.putInt(PLAY_POINT, mLastPos);
	}

	private void saveCpuInfo(boolean complete) {
		SharedPreferences sp = getSharedPreferences("playerInfo",
				Activity.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("complete", complete);
		editor.commit();
	}

	private void useDefault(boolean useDefault) {
		SharedPreferences sp = getSharedPreferences("playerInfo",
				Activity.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("useDefault", useDefault);
		editor.commit();
	}

	private boolean isInititaled() {
		SharedPreferences sp = getSharedPreferences("playerInfo",
				Activity.MODE_PRIVATE);
		return sp.getBoolean("complete", false);
	}

	private boolean isUseDefault() {
		SharedPreferences sp = getSharedPreferences("playerInfo",
				Activity.MODE_PRIVATE);
		return sp.getBoolean("useDefault", false);
	}

	public static final int PATH_STAND = 0;
	public static final int PATH_HIGH = 1;
	public static final int PATH_SUPER = 2;

	public void changePath(int type) {
		mLastPos = mVV.getCurrentPosition();
		saveProgram(mLastPos);
		if (mediaControllerFragment != null) {
			mediaControllerFragment.mediaSeekBar.setEnabled(false);
		}
		switch (type) {
			case PATH_STAND:
				mVideoSource = standpath;
				break;
			case PATH_HIGH:
				mVideoSource = highpath;
				break;
			case PATH_SUPER:
				mVideoSource = superpath;
				break;
			default:
				break;
		}
		changePath = true;
		onCompletionExecuted = false;
		curpos1 = mVV.getCurrentPosition();
		mVV.stopPlayback();
		isPrepared = false;
		loadingLayout.setVisibility(View.VISIBLE);
		if (startLoadingLayout != null) {
			startLoadingLayout.setVisibility(View.VISIBLE);
			loadingLayout.setVisibility(View.GONE);
		}
		errLayout.setVisibility(View.GONE);
	}

	public void opensourceurl() {
		// 记录播放器当前的播放位置
		if (null != mVV) {
			// mLastPos = mVV.getCurrentPosition();
			// saveProgram(mLastPos);
			mVV.pause();
		}
		Intent intent = getIntent();
		getExtras();
		if (playType == LIVE_PLAY && !isBackLook) {
			intent.putExtra("livePos", livePosition);
			if (!TextUtils.isEmpty(webPage)){
				intent.setClass(this, WebAvoidPicActivity.class);
			} else if (!TextUtils.isEmpty(showUrl) || !TextUtils.isEmpty(url)) {
				intent.setClass(this, WebAvoidActivity.class);
			} else {
				intent.setClass(this, WebAvoidPicActivity.class);
			}
			intent.putExtra("fromSource", true);
			startActivity(intent);
//			finish();
		} else if (playType == VOD_PLAY || playType == SHAKE_PLAY) {
			intent.putExtra("lastPos", mLastPos);
			if (url.equals(path)) {
				intent.setClass(this, WebAvoidPicActivity.class);
			} else {
				intent.setClass(this, WebAvoidActivity.class);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.putExtra("isHalf",true);
			intent.putExtra("fromSource", true);
			startActivity(intent);
		}
//		finish();
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			switch (requestCode){
//				case 101:
//					if (!isPrepared){
//						loadingLayout.setVisibility(View.VISIBLE);
//						loadingLayout.setBackgroundResource(R.color.c_null);
//						loadingTextView.setVisibility(View.VISIBLE);
//					}
//					restartPlay(getIntent());
//					break;
//			}
//	}

	int curSubId;
	public void restartPlay(Intent intent) {
		CommonUtils.showMethodLog("restartPlay");
		if (intent != null) {
			getExtras();
		}
		stopPreLoading();
		hideHalfControl();
		EpisodeEvent e = new EpisodeEvent();
		e.subid = subid;
		curSubId = subid;
		Log.i("tvfan_subId","restart_"+curSubId);
		EventBus.getDefault().post(e);
		mediaControllerFragment.setTitleName();
		mediaControllerFragment.showTitleLayout();
		mediaControllerFragment.setHalfView();
		if (!getIntent().getBooleanExtra("isNative",true)){
			playByWebPlayer();
			return;
		}
		if (mediaControllerFragment.isPaused()){
			mediaControllerFragment.setPaused(false);
			mediaControllerFragment.playBtn.setSelected(false);
		}
		if (playType == VOD_PLAY || playType == SHAKE_PLAY) {
			loadingLayout.setVisibility(View.VISIBLE);
			loadingLayout.setBackgroundResource(R.color.c_null);
			loadingTextView.setVisibility(View.VISIBLE);
		}
		if (playType == LIVE_PLAY) {
			startLoadingLayout.setVisibility(View.VISIBLE);
			loadingLayout.setVisibility(View.GONE);
			if (!mediaControllerFragment.barShow) {
				mediaControllerFragment.openController();
			}
			bufferNum = 0;
			channelStatObj = new JSONObject();
			try {
				channelStatObj.put("channelId",channelId);
				channelStatObj.put("playId",channelNetId);
				channelStatObj.put("type",0);
				channelStatObj.put("stat",0);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		mVideoSource = path;
		Log.i("PlayerActivity", "restart mVideoSource=" + mVideoSource);
		prepareToPlay();

	}
//	public void startP2pLive(){
////		mVideoSource = p2p.getPlayPath(PlayerActivity.this, p2pChannel);
//		needCaching = false;
////		mEventHandler.sendEmptyMessage(EVENT_PLAY);
//	}

	private void resetLoadingBackground() {
		loadingLayout.setBackgroundResource(R.color.c_null);
	}

	public void saveProgram(int dppositon) {
		if (playType == LIVE_PLAY || TextUtils.isEmpty(programName)
				|| isBackLook || playType == SHAKE_PLAY ) {
			return;
		}
		if (videoTotalTime - dppositon <= 10 || playCompleted) {
			dppositon = -1;
		}
		if (activityId != 0) {
			return;
		}
		if (!TextUtils.isEmpty(programId)) {
			NetPlayData temp = new NetPlayData();
			temp.id = Integer.parseInt(programId);
			temp.intro = programName;
			temp.videoPath = path;
			boolean norealUrl = false;
			if (url != null) {
				norealUrl = url.endsWith(".png") || url.endsWith(".jpg");
			}
			if (!norealUrl) {
				temp.url = url;
			}
			temp.name = programName;
			temp.dbposition = dppositon;
			temp.subid = subid;
			if (playType == CACHE_PLAY) {
				temp.isdownload = "1";
			} else {
				temp.isdownload = "0";
			}
			AccessProgram.getInstance(this).save(temp);

		}
	}

	private int getHistoryPlayPosition() {
		/*
		 * pos before opensourceurl
		 */
		if (getIntent().hasExtra("lastPos")) {
			Log.e(TAG, "get history pos from extra");
			int pos = getIntent().getIntExtra("lastPos", 0);
			getIntent().removeExtra("lastPos");
			return pos;
		}
		if (playType == SHAKE_PLAY) {
			return mLastPos;
		}
		if (playType != LIVE_PLAY) {
			int historyPosition = 0;
			if (activityId != 0) {
				return 0;
			}
			if (!TextUtils.isEmpty(programId)) {
				NetPlayData program = AccessProgram.getInstance(this)
						.findByProgramIdAndSubId(programId,
								String.valueOf(subid));
				historyPosition = program.dbposition;
			}
			return historyPosition;
		}
		return 0;
	}

	public boolean clickquit;
	private String cachingWhilePlayingUrl;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (mediaControllerFragment != null
				&& mediaControllerFragment.keydown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void cachingSeek() {
		if (type == null || !needCaching || playType != VOD_PLAY
				|| !type.equals("m3u8")) {
			return;
		}
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_SEEKSERVICE);
		startService(intent);
	}

	private void setCachingPlayUrl() {
		if (playType != CACHE_PLAY) {
			if (changeVideo || changePath) {
				mediaControllerFragment.setSecondaryProgress(0);
				changeSourceUrl = true;
				mUIHandler.removeMessages(UI_EVENT_UPDATE_BUFFER);
			}
			if (cachingWhilePlayingUrl != null) {
				mVV.setVideoPath(cachingWhilePlayingUrl);
				Message msg = mUIHandler.obtainMessage();
				msg.obj = 0f;
				msg.what = UI_EVENT_UPDATE_BUFFER;
				msg.sendToTarget();
			} else {
				if (mVideoSource == null) {
					mVideoSource = "";
				}
				mVV.setVideoPath(mVideoSource);
			}
		} else {
			if (mVideoSource == null) {
				mVideoSource = "";
			}
			mVV.setVideoPath(mVideoSource);
		}
		changeVideo = false;
		changePath = false;
	}

	@Override
	public void finish() {
		if (getIntent().getIntExtra("resultCode", 0) == 101) {
			setResult(RESULT_OK);
		}
		super.finish();
		if (mVV != null && mVV.isPlaying()) {
			mVV.stopPlayback();
		}
		int lefttimes = PreferencesUtils
				.getInt(this, null, "comment_times", -1);
		if (lefttimes == 0) {
			sendBroadcast(new Intent("quit_from_player"));
			PreferencesUtils.putInt(this, null, "comment_times", -1);
		}
		stopPreLoading();
		EventBus.getDefault().removeAllStickyEvents();
		if (getIntent().getBooleanExtra("notice", false)) {
			mediaControllerFragment.goHome();
		}

	}

	private void startPreLoading(String url, int programId, int subId) {
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_START);
		intent.putExtra("url", url);
		intent.putExtra("programId", programId);
		intent.putExtra("subId", subId);
		startService(intent);
		mUIHandler.sendEmptyMessageDelayed(SOURCE_INVALID, MAX_WAIT);
		getUrl = false;
	}

	private void startChangePath(String url) {
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(
				CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_CHANGE_SOURCE_START);
		intent.putExtra("url", url);
		startService(intent);
	}

	public void stopPreLoading() {
		if (playType != VOD_PLAY) {
			return;
		}
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_STOP);
		startService(intent);
		Log.i(TAG, "stop preloading");
	}

	public String crackPath;

	UpdateCrackDialog updateCrackDialog;

	/**
	 * 播放组件更新时，显示和取消更新的dialog
	 *
	 * @param type 调用破解返回类型
	 * @return 如果是“updatePlugins”或者“complete”，返回true，其它返回false
	 */
	private boolean handleCrackUpdate(String type) {
		if (!TextUtils.isEmpty(type) && type.equals("updatePlugins")) {
			if (updateCrackDialog == null) {
				updateCrackDialog = new UpdateCrackDialog(this, R.style.dialog);
				updateCrackDialog.show();
				updateCrackDialog.setCancelable(false);
			}
			return true;
		} else if (!TextUtils.isEmpty(type) && type.equals("complete")) {
			if (updateCrackDialog != null) {
				updateCrackDialog.dismiss();
			}
			if (playType == VOD_PLAY) {
				Log.i(TAG, "加载组件完成，再破解一次，点播");
				startPreLoading(url, Integer.parseInt(programId), subid);
			} else if (playType == LIVE_PLAY) {
				Log.i(TAG, "加载组件完成，再破解一次，直播");
				doLiveCrack(url);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 使用CachingWhilePlaying服务完成后，会回调该方法
	 *
	 * @param preLoadingResultInfo
	 */
	public void onEvent(PreLoadingResultInfo preLoadingResultInfo) {
		if (handleCrackUpdate(preLoadingResultInfo.type)) {
			return;
		}
		if (TextUtils.isEmpty(preLoadingResultInfo.path)
				&& playType == VOD_PLAY) {
			getFragmentHelper().disablePlayVod(subid, 0);
			Toast.makeText(this, "无法连接视频源", Toast.LENGTH_SHORT).show();
			mUIHandler.sendEmptyMessage(UI_ERROR);
			return;
//			finish();
		}
		crackPath = preLoadingResultInfo.crackPath;

		path = preLoadingResultInfo.path;
		if (preLoadingResultInfo.isChangeSource) {
			cachingWhilePlayingUrl = preLoadingResultInfo.path;
			getUrl = true;
			Log.i(TAG, "EventBus onEvent, mVideoSource:"
					+ cachingWhilePlayingUrl);
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		} else {
			this.type = preLoadingResultInfo.type;
			standpath = preLoadingResultInfo.standUrl;
			highpath = preLoadingResultInfo.highUrl;
			superpath = preLoadingResultInfo.superUrl;
			Log.i(TAG, "standpath=" + standpath);
			Log.i(TAG, "highpath=" + highpath);
			Log.i(TAG, "superpath=" + superpath);
			cachingWhilePlayingUrl = preLoadingResultInfo.path;
			needCaching = true;
			getUrl = true;
			Log.i(TAG, "EventBus onEvent, mVideoSource:"
					+ cachingWhilePlayingUrl);
//			crackPath = "http://g3.letv.cn/vod/v2/MTY3LzI0LzI2L2xldHYtdXRzLzE5L3Zlcl8wMF8yMi0xMDAxNDYyNTcwLWF2Yy05NTk1NDAtYWFjLTY0MDAyLTQ4Nzk2MC02Mjk2NzIyNC0yZDk2ZWNiNTk4ZDE0NjBmMjU0ZDFhMzA1ODk3NzM3YS0xNDQyODg2NDI0NDIzLm1wNA==?b=1031&mmsid=35208270&tm=1443065587&key=7a600d15ba05e0eedaafe1acef2c1e94&platid=3&splatid=345&playid=0&tss=ios&vtype=22&cvid=229767770150&payff=0&pip=47824b2d3a814d1d30fd086548bb288d&format=1&sign=mb&dname=mobile&expect=3&tag=mobile&pid=10010701";
//			if (crackPath.startsWith("Letv://")){
//				if (liveService != null){
////					try {
//						crackPath = crackPath.replace("Letv://","");
////						String result = liveService.getVodCrackUrl(crackPath);
//						String result = liveService.getPlayUrl(url, "http://127.0.0.1:8024");
//						EventMessage msg = new EventMessage("video_letv_crack");
//						msg.bundle.putString("url", result);
//						EventBus.getDefault().post(msg);
////					} catch (RemoteException e) {
////						e.printStackTrace();
////					}
//				} else {
//					Log.e("msg_live", "bind live service failed");
//					Intent intent = new Intent();
//					intent.setClass(getApplicationContext(), LeService.class);
//					bindService(intent, conn, Context.BIND_AUTO_CREATE);
//				}
//			} else
			if (isInititaled() || isUseDefault()) {
				if (!isOnpause) {
					mEventHandler.sendEmptyMessage(EVENT_PLAY);
				} else {
					getParseUrlAfterOnPause = true;
				}
			}
			if (mediaControllerFragment != null ) {
				mediaControllerFragment.setCrackResource(standpath, highpath,
						superpath);
				if(isHalf || playType == LIVE_PLAY){
					mediaControllerFragment.crack.setVisibility(View.GONE);
				}
			}
//				switchEpisode = false;
		}
	}

	public void onEvent(BufferedPositionInfo bufferedPositionInfo) {
		Log.i(TAG, "EventBus onEvent, current buffered position:"
				+ bufferedPositionInfo.getCurBufferedPosition());
		if (bufferedPositionInfo.getCurBufferedPosition() < 0) {
			Toast.makeText(this, "无法连接视频源", Toast.LENGTH_SHORT).show();
			mUIHandler.sendEmptyMessage(UI_ERROR);
//			finish();
		}
		mUIHandler.removeMessages(UI_EVENT_UPDATE_BUFFER);
		Message msg = mUIHandler.obtainMessage();
		msg.what = UI_EVENT_UPDATE_BUFFER;
		msg.obj = bufferedPositionInfo.getCurBufferedPosition();
		mUIHandler.sendMessageDelayed(msg, 3000);
	}

	public void onEvent(JiShuData data) {
		if (!isPrepared) {
			loadingLayout.setVisibility(View.VISIBLE);
			loadingLayout.setBackgroundResource(R.color.c_null);
			loadingTextView.setVisibility(View.VISIBLE);
		}
		getIntent().removeExtra("fromSource");
		restartPlay(getIntent());
	}

	public void onEvent(EventMessage msg) {
		if (msg.name.equals("Player_Letv_Ready") && !TextUtils.isEmpty(url)
//                && (url.startsWith("Letv://") || url.startsWith("p2p://"))
				) {
			doLiveCrack(url);
		} else if (msg.name.equals("player_letv_crack_result")) {
			mVideoSource = msg.bundle.getString("result");
			needCaching = false;
			if (!isOnpause) {
				mEventHandler.sendEmptyMessage(EVENT_PLAY);
			} else {
				getParseUrlAfterOnPause = true;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_back) {
			if (enterType == 1) {
				changeOrientation(true);
			} else {
				clickquit = true;
				finish();
			}
		} else if (id == R.id.first_touch_id) {
			firstTouchId.setVisibility(View.GONE);
		} else if (id == R.id.media_loading_source) {
			opensourceurl();
		} else if (id == R.id.media_error_logo) {
			if (loadingType == 2){
				if (!getIntent().getBooleanExtra("isNative",true)){
					playByWebPlayer();
					return;
				}
				errLayout.setVisibility(View.GONE);
				if (getIntent().getBooleanExtra("skipWeb",false)){
					opensourceurl();
				}else {
					if (isPrepared) {
						mVV.stopPlayback();
					} else {
						mVV.stopPlayback();
						restartPlay(getIntent());
					}
				}
			}
		} else if (id == R.id.media_error_reload) {
			if (getDataError){
				mediaControllerFragment.getSubVideoInfo(Integer.parseInt(programId),subid,0,1);
			}else {
				errLayout.setVisibility(View.GONE);
				isError = false;
				restartPlay(getIntent());
			}
		}
	}
	public void playByWebPlayer(){
		switchEpisode = true;
		mVV.stopPlayback();
		stopPreLoading();
		showStartplay();
		Intent webIntent = new Intent(this, WebPlayerActivity.class);
		webIntent.putExtra("url",url);
		startActivity(webIntent);
	}

	protected boolean isPlaying() {
		return mVV.isPlaying();
	}

	protected int getCurrentPosition() {
		return mVV.getCurrentPosition();
	}

	boolean isScaled = false;

	public void screenScale() {
		if (!isScaled) {
			mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
		} else {
			mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
		}
		isScaled = !isScaled;
	}

	public BVideoView getMediaPlayer() {
		return mVV;
	}

	public String getVideoSource() {
		return mVideoSource;
	}

	public String getStandpath() {
		return standpath;
	}

	public String getHighpath() {
		return highpath;
	}

	public String getSuperpath() {
		return superpath;
	}

	public int getDuration() {
		return mVV.getDuration();
	}

	public void setStartTextLine(int index) {
		String text = startText.getText().toString();
		if (text.contains("(")) {
			startText.setText(getResources().getString(
					R.string.net_line_loading_txt)
					+ (index + 1));
			startText.append(text, text.indexOf("("), text.indexOf(")") + 1);
		} else {
			startText.setText(getResources().getString(
					R.string.net_line_loading_txt)
					+ (index + 1));
		}
	}

	/**
	 * 调用ParserUtil直接破解，完成时回调该方法
	 *
	 * @param preLoadingResultInfo
	 */
	@Override
	public void crackResult(PreLoadingResultInfo preLoadingResultInfo) {

// 破解之后在子线程调用该方法，故发消息在主线程处理结果
		Message msg = new Message();
		msg.obj = preLoadingResultInfo;
		msg.what = HANDLE_CRACK_RESULT;
		mUIHandler.sendMessage(msg);
//        handleCrackResult(preLoadingResultInfo);
	}

	private void handleCrackResult(PreLoadingResultInfo preLoadingResultInfo) {
		Log.i(TAG, "handleCrackResult:" + preLoadingResultInfo);
		if (handleCrackUpdate(preLoadingResultInfo.type)) {
			return;
		}
		if (!TextUtils.isEmpty(preLoadingResultInfo.path)) {
			mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
			Log.i(TAG, "cracking live result:" + preLoadingResultInfo.path);
//			if (preLoadingResultInfo.path.startsWith("p2p://")) {
//				if (liveService != null) {
//					preLoadingResultInfo.path = preLoadingResultInfo.path.replace("p2p://", "");
//					preLoadingResultInfo.path = liveService.getBaseUrl(preLoadingResultInfo.path);
//					p2pServerCrackResult = ""; // 本次破解成功，将该值值为“”
//				} else {
//					// cde的p2p服务（即LeService）还没有绑定成功，暂时将该服务器破解结果记录下来
//					p2pServerCrackResult = preLoadingResultInfo.path;
//					Log.e("msg_live","bind live service failed");
//					return;
//				}
//			}
			if (!TextUtils.isEmpty(p2pChannel)
					&& !(Build.MANUFACTURER.equals("LENOVO") || Build.VERSION.SDK_INT == 23)
					) {

//				startP2pLive();
//				if (p2pManager == null)
//					startP2PService();
				mVideoSource = getMobilePlayUrl(preLoadingResultInfo.path);
				if(timerTask!=null){
					timerTask.cancel();
				}
						excuteTimerTask();

			} else {
				mVideoSource = preLoadingResultInfo.path;
			}
			getUrl = true;
			isCrackComplete = true;
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		} else {
			mUIHandler.removeMessages(SWITCH_LIVE_NUMBER);
			mUIHandler.sendEmptyMessageDelayed(SWITCH_LIVE_NUMBER, 5000);
		}
	}


	TimerTask timerTask;
	/**
	 * 设置定时器,每隔5分钟请求一次info.do 接口,下载返回的文件,然后把该文件以及NAT值以及用户MAC值发给服务器处理
	 */
	public void excuteTimerTask(){
		Timer timer = new Timer();
		 timerTask = new TimerTask() {
			@Override
			public void run() {
				//去本地服务器请求接口,然后读取
				String originalpath = null;
				//对播放地址进行处理,获取原始播放地址
				originalpath = url;
				//获取ifno信息字符串
				String info = "p2pTest:" + getinfointerfaceFile(originalpath);
				//获取当前频道id
				channelId = String.valueOf(getIntent().getIntExtra("channelId", 0));
				//获取当前播放源id就是livePosition
				String src = livePosition+"";
				//向服务器发送请求
				String result =postRequest(info,channelId,src,userNat,userMAC);
				Log.d(TAG,"向服务器发送post请求的结果"+result);

			}
		};
		timer.schedule(timerTask,3000l,300000l);
	}

	HttpURLConnection con;

	/**
	 * 使用post请求向服务器发送数据
	 * @param info  p2p信息
	 * @param ch   频道id
	 * @param src  当前播放cpid
	 * @param nat  网络nat值
	 * @param mac  设备mac值
	 * @return  链接是否成功
	 */
	public  String postRequest(String info,String ch,String src,String nat,String mac){
		HttpURLConnection conn=null;
		//参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("p2p", info);
		params.put("ch", ch);
		params.put("src", src);
		params.put("nat", nat);
		params.put("mac", mac);
		//获得请求体
		byte[] data = getRequestData(params, "utf-8").toString().getBytes();
		Log.d(TAG,"data的数据是...."+data);
		try {
			URL url=new URL("http://tv.tvfan.cn:8080/logger/v40/logger!p2p.action");
			conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(5000);
			conn.setDoOutput(true);
			//使用Post方式不能使用缓存
			conn.setUseCaches(false);
//			conn.connect();
			OutputStream out=conn.getOutputStream();
			out.write(data);
			out.flush();
			out.close();
			int response = conn.getResponseCode();            //获得服务器的响应码
			if(response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = conn.getInputStream();
				return dealResponseResult(inptStream);                     //处理服务器的响应结果
			}
		} catch (IOException e) {
			e.printStackTrace();
//			return "err: " + e.getMessage().toString();
			return "err: ";
		}
		return "-1";
	}
	/*
    * Function  :   处理服务器的响应结果（将输入流转化成字符串）
    * Param     :   inputStream服务器的响应输入流
    */
	public  String dealResponseResult(InputStream inputStream) {
		String resultData = null;      //存储处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray()).trim();
		return resultData;
	}
	/*
         * Function  :   封装请求体信息
         * Param     :   params请求体内容，encode编码格式
         */
	public static StringBuffer getRequestData(Map<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
		try {
			for(Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey())
						.append("=")
//						.append(URLEncoder.encode(entry.getValue(), encode))
						.append(entry.getValue())
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	/**
	 * 获取info.do接口请求到的文件并下载到本地
	 * @param path     播放地址
	 * @return
	 */
	StringBuilder sBuilder;
	public String getinfointerfaceFile(String path){
		//http://192.168.1.100:14188/playlist/info.do?url=http://dlivec.tvfan.cn/hnws/playlist.m3u8
		String urlStr = "http://127.0.0.1:14188/playlist/info.do?url="+path;
		try{
			URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStream input=null;
			input=urlConn.getInputStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br =new BufferedReader(isr);
			sBuilder = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				line.replace(" ","");
				if(line.contains("seconds")){
					line=line.substring(9);
					sBuilder.append(line);
				}
				if(line.contains("downsizeB")&&!line.contains("downsizeBbyType")&&!line.contains("downsizeBbyUser")){
					line=line.substring(11);
					sBuilder.append(line);
				}
				if(line.contains("sharesizeB")){
					line=line.substring(12);
					sBuilder.append(line);
				}
				if(line.contains("downsizeBbyType")){
					line=line.substring(17);
					sBuilder.append(line);
				}
			}
			Log.d(TAG,"info信息是,.,,,,,,"+sBuilder.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		if(sBuilder!=null){

			return sBuilder.toString();
		}else{
			return "";
		}
	}
	RelativeLayout.LayoutParams playBtnParam;
	private int videoHeight;

	public void setHalfScreenView() {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) playerLayout
				.getLayoutParams();
		params.height = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PX, videoHeight, getResources()
						.getDisplayMetrics());
		playerLayout.setLayoutParams(params);
		controlLayout.setLayoutParams(params);
		loadingLayout.setLayoutParams(params);
		errLayout.setLayoutParams(params);
	}

	private void setFullScreenView() {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) playerLayout
				.getLayoutParams();
		params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
		playerLayout.setLayoutParams(params);
		controlLayout.setLayoutParams(params);
		loadingLayout.setLayoutParams(params);
		errLayout.setLayoutParams(params);
	}

	public void changeOrientation(boolean byUser) {
		if (byUser){
			enlargeClick();
		}
//		if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180){
//			if (byUser){
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//				requestFullLayout();
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//			}else{
//				requestHalfLayout();
//			}
//		}else if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270){
//			if (byUser){
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//				requestHalfLayout();
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//			}else{
//				requestFullLayout();
//			}
//		}
	}

	public void requestHalfLayout() {
		isHalf = true;
		getIntent().putExtra("isHalf", isHalf);
		setHalfScreenView();
		mediaControllerFragment.setHalfView();
		enterType = 0;
		if (mediaControllerFragment.enlarge != null) {
			mediaControllerFragment.enlarge.setImageResource(R.drawable.programhalf_fullscreen);
		}
//		getSupportFragmentManager().beginTransaction().show(programFragment).commitAllowingStateLoss();
		changeStatuBar(false);
		mediaControllerFragment.dismissAd();
	}

	public void requestFullLayout() {
		setFullScreenView();
		isHalf = false;
		getIntent().putExtra("isHalf", isHalf);
		enterType = 1;
		mediaControllerFragment.setHalfView();
//		getSupportFragmentManager().beginTransaction().hide(programFragment).commitAllowingStateLoss();
		changeStatuBar(true);
		if (mediaControllerFragment.enlarge!=null){
			mediaControllerFragment.enlarge.setImageResource(R.drawable.to_half);
		}
		if (needLoadHelpStub()) {
			mUIHandler.sendEmptyMessage(TOUCH_GUIDE);
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (programFragment != null){
			changeOrientation(false);
		}
	}

	public void changeOrientationOnly() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setFullScreenView();
		if (mediaControllerFragment.enlarge != null) {
			mediaControllerFragment.enlarge.setVisibility(View.GONE);
		}
		changeStatuBar(true);
	}

	public void getVideoHeight(){
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		if (dm.widthPixels>dm.heightPixels){
			videoHeight = (int)((float)dm.heightPixels*9/16);
		} else {
			videoHeight = (int)((float)dm.widthPixels*9/16);
		}
		Log.e("msg_videoheight", ""+videoHeight);
	}
	public void showStartplay() {
		resetLoadingLogo(2);
		loadingLayout.setVisibility(View.GONE);
	}

	public void hideHalfControl() {
		errLayout.setVisibility(View.GONE);
	}
	int loadingType = 1;
	public void resetLoadingLogo(int type){
		switch (type){
			case 1://电视粉Logo
				loadingIcon.setImageResource(R.drawable.play_wait_icon);
				errorText.setText("总有你想看的！");
				errorText.setVisibility(View.VISIBLE);
				retry.setVisibility(View.GONE);
				break;
			case 2://播放按钮
				loadingIcon.setImageResource(R.drawable.start_play);
				errorText.setVisibility(View.GONE);
				retry.setVisibility(View.GONE);
				break;
			case 3://出错icon
				loadingIcon.setImageResource(R.drawable.no_prize);
				errorText.setText("加载失败");
				errorText.setVisibility(View.VISIBLE);
				retry.setVisibility(View.VISIBLE);
				break;
		}
		errLayout.setVisibility(View.VISIBLE);
		loadingType = type;
	}
	public void changeStatuBar(boolean isFull){
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if(isFull){
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}else{
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	public void setVideoTitle(){
		if(mediaControllerFragment != null){
			mediaControllerFragment.setTitleName();
		}
	}
	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.e("msg_method", "onNewIntent");
		if(intent.getBooleanExtra("notice", false)){
			setIntent(intent);
			switchEpisode = true;
			if (playType != intent.getIntExtra("playType", CACHE_PLAY)){
				recreate();
				return;
			}
			playType = getIntent().getIntExtra("playType", CACHE_PLAY);
			if (mediaControllerFragment != null){
				mediaControllerFragment.crack.setVisibility(View.GONE);
			}
			programId = String.valueOf(getIntent().getIntExtra(
					TAG_INTENT_PROGRAMID, 0));
			if("0".equals(programId)){
				programId = String.valueOf(getIntent().getLongExtra(
						TAG_INTENT_PROGRAMID, 0));
			}
			channelId = String.valueOf(getIntent().getIntExtra("channelId", 0));
			subid = getIntent().getIntExtra(TAG_INTENT_SUBID, 0);
			stopPreLoading();
			doInit();
			if (isHalf) {
				loadingLayout.setVisibility(View.GONE);
				setHalfScreenView();
			}
		}
	}

	P2PManager p2pManager;
	String native_dir;

	public void startP2PService() {
		if (P2PManager.get() == null) {
//			String zone = "zt15120802";
			String zone = "http://103.244.165.191:4000/p2p/webvodpeer.ini";
			native_dir = this.getCacheDir() + "p_pie_1";
			Log.d("tvapp", "startP2PService at " + native_dir);
			File ndir = new File(native_dir);
			if (ndir.exists() == false)
				ndir.mkdir();
			else {
				File file = new File(native_dir + "/vodpeer.e");
				if (file.exists()) {
					file.delete();
				}
			}
			p2pManager = P2PManager.init(native_dir, zone);
			p2pManager.setErrorEvent(new Runnable() {

				@Override
				public void run() {
					Log.d("0..............................................", "p2p 00000000000000000      manager to run error event");

				}

			});

			Log.d("startservice", "p2pManager started!");
			p2pManager.start();
			P2PManager.get().set_m3u8_endstring("?");
			P2PManager.get().set_ts_endstring("?");

		} else {
			Log.i(TAG, "p2pManager is not null");
			p2pManager = P2PManager.get();
		}

	}

	public  String getMobilePlayUrl(String url) {
		Log.d("P2PPlayer", "use new url : " + url);
		if (p2pManager != null && url != null) {
			String nurl = p2pManager.getPlayedUrl(url).replace("24188",
					"14188");
			return nurl;
		}
		return null;
	}

	private String getNATValue() {
		String path =native_dir + "/log/nat.txt";
		String content = ""; //文件内容字符串
		//打开文件
		File file = new File(path);
		//如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory())
		{
			Log.d("TestFile", "The File doesn't not exist.");
		}
		else
		{
			try {
				FileInputStream instream = new FileInputStream(file);
				if (instream != null)
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					//分行读取
					while (( line = buffreader.readLine()) != null) {
						content += line + "\n";
					}
					instream.close();
				}
			}
			catch (java.io.FileNotFoundException e)
			{
				Log.d("TestFile", "The File doesn't not exist.");
			}
			catch (IOException e)
			{
				Log.d("TestFile", e.getMessage());
			}
			Log.i("LogFile", content);
		}
		return content;
	}


}
