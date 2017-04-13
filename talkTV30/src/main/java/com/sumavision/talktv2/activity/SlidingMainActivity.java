package com.sumavision.talktv2.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.kugou.fanxing.core.FanxingManager;
import com.sumavision.itv.lib.dlna.model.DlnaGlobal;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.adapter.MyPagerAdapter;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.DialogInfo;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UpdateData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VersionData;
import com.sumavision.talktv2.bean.WelcomeData;
import com.sumavision.talktv2.components.CustomViewPager;
import com.sumavision.talktv2.dlna.services.DlnaControlService;
import com.sumavision.talktv2.fragment.CommonDialogFragment;
import com.sumavision.talktv2.fragment.CommonDialogFragment.OnCommonDialogListener;
import com.sumavision.talktv2.fragment.FoundIndexFragment;
import com.sumavision.talktv2.fragment.RecommendFragment;
import com.sumavision.talktv2.fragment.TalkTvMenuFragment;
import com.sumavision.talktv2.fragment.TvLiveFragment;
import com.sumavision.talktv2.fragment.lib.HotLibFragment;
import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.HttpEvent;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ChannelStatRequest;
import com.sumavision.talktv2.http.json.GetAddressParser;
import com.sumavision.talktv2.http.json.GetAddressRequest;
import com.sumavision.talktv2.http.json.GetAppNewVersionParser;
import com.sumavision.talktv2.http.json.GetAppNewVersionRequest;
import com.sumavision.talktv2.http.json.PlayInfoRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.UserDetailParser;
import com.sumavision.talktv2.http.json.UserDetailRequest;
import com.sumavision.talktv2.http.listener.OnLogInListener;
import com.sumavision.talktv2.http.listener.OnWelcomeListListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.service.AppUpdateService;
import com.sumavision.talktv2.service.DateChangeReceiver;
import com.sumavision.talktv2.service.LiveAlertService;
import com.sumavision.talktv2.service.TvBaiduPushMessageReceiver;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.BitmapUtils;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.PullParser;
import com.sumavision.talktv2.utils.SmartBarUtils;
import com.sumavision.talktv2.utils.StatusBarUtils;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;
import com.tvata.p2p.P2PManager;
import com.umeng.analytics.MobclickAgent;
import com.wechat.tools.AdManager;
import com.wechat.tools.st.CustomerSpotView;
import com.wechat.tools.st.SpotDialogListener;
import com.wechat.tools.st.SpotManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class SlidingMainActivity extends SlidingFragmentActivity implements
		OnPageChangeListener, OnClickListener, OnWelcomeListListener,
		OnSharedPreferenceChangeListener {
	private SlidingMenu mSlidingMenu;
	private CustomViewPager mViewPager;
	private TalkTvMenuFragment mTalkTvMenuFragment;
	Intent liveAlertIntent;
	private ImageButton dlnaResume;
//	public boolean useQihoWall = false;
	SharedPreferences pushSharedPreferences,defaultSharedPreferences;

	private PullParser pullParser = new PullParser();
	private boolean liveModule;
	
	private ImageView activityIcon;
	private int activityId;
	
	private RelativeLayout youmiLayout;
	private RelativeLayout youmiWindow;
	private Button youmiQuit;
	private Button youmiDownload;
	private DateChangeReceiver dateChangeReceiver;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		if (SmartBarUtils.hasSmartBar()) {
			getWindow().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		}
		StatusBarUtils.setImmerseTheme(this, R.color.navigator_bg_color);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initYoumiAd();
		String meta = AppUtil.getMetaData(this, "UMENG_CHANNEL");
		// 设置应用详情页底部按钮颜色
		// QihooAdAgent.setDetailButtonColors(getResources().getColor(R.color.white),
		// getResources().getColor(R.color.fav_item_pressed));
		dlnaResume = (ImageButton) findViewById(R.id.dlna_resume);
		View mCustomView = getLayoutInflater().inflate(
				R.layout.custom_actionbar_main, null);
		ImageView historyItem = (ImageView) mCustomView
				.findViewById(R.id.action_history);
		ImageView searchItem = (ImageView) mCustomView
				.findViewById(R.id.action_search);
		ImageView cacheItem = (ImageView) mCustomView
				.findViewById(R.id.action_cache);
		ImageView advertItem = (ImageView) mCustomView
				.findViewById(R.id.iv_advert);
		activityIcon = (ImageView) mCustomView
				.findViewById(R.id.iv_activity);
		activityIcon.setOnClickListener(this);
		activityIcon.setVisibility(View.GONE);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setSplitBackgroundDrawable(
				getResources().getDrawable(R.drawable.navigator_bg));
		getSupportActionBar().setCustomView(
				mCustomView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
		if (SmartBarUtils.hasSmartBar()) {
			Drawable d = getResources().getDrawable(R.drawable.ic_action_back);
			SmartBarUtils.setBackIcon(getActionBar(), d);
			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
			historyItem.setVisibility(View.GONE);
			searchItem.setVisibility(View.GONE);
			cacheItem.setVisibility(View.GONE);
			advertItem.setVisibility(View.GONE);
		} else {
			historyItem.setOnClickListener(this);
			searchItem.setOnClickListener(this);
			cacheItem.setOnClickListener(this);
			advertItem.setVisibility(View.GONE);
			// new AlimamaHelper(this).addTaobaoWall(advertItem);
		}
		mCustomView.findViewById(R.id.imgv_logo).setOnClickListener(this);
		setBehindContentView(R.layout.behind_layout);
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setBehindScrollScale(0);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mTalkTvMenuFragment = TalkTvMenuFragment.newInstance();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, mTalkTvMenuFragment).commit();
		DlnaGlobal.remote = false;
		// startService(new Intent(this, DlnaControlService.class));
		liveAlertIntent = new Intent(this, LiveAlertService.class);
		startService(liveAlertIntent);

		isNeedShowAvoid = PreferencesUtils.getBoolean(this, null, "isavoid");
		liveModule = PreferencesUtils
				.getBoolean(this, null, "liveModule", true);
		pushSharedPreferences = getSharedPreferences(Constants.pushMessage,
				Context.MODE_PRIVATE);
		pushSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		defaultSharedPreferences = PreferencesUtils.getSharedPreferences(this,null);
		defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		initViewPager();
		if (UserNow.current().userID > 0) {
			VolleyUserRequest.login(null, null);
			checkAddressRequest();
			VolleyHelper.post(new UserDetailRequest().make(), new ParseListener(new UserDetailParser()) {
				@Override
				public void onParse(BaseJsonParser parser) {
					updateFoundRedTip();
				}
			}, null);
		} else {
			updateFoundRedTip();
		}
		new Thread(new ReadXml()).start();

		hasQuitAd = PreferencesUtils.getBoolean(this, null, "hasQuitAd", false);
		youmiLayout = (RelativeLayout) findViewById(R.id.youmiad_layout);
		youmiWindow = (RelativeLayout) findViewById(R.id.youmi_window);
		youmiWindow.setVisibility(View.GONE);
		youmiQuit = (Button) findViewById(R.id.youmi_quit);
		youmiDownload = (Button) findViewById(R.id.youmi_download);
		youmiWindow.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		youmiQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (P2PManager.get() != null) {
					P2PManager.get().stop();
				}
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		youmiDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spotView != null && spotView.isAttachedOnWindow()) {
					SpotManager.getInstance(SlidingMainActivity.this).handlerClick();
				}
				hideYoumiAd();
			}
		});
		dateChangeReceiver = new DateChangeReceiver();
		IntentFilter dateFilter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
		registerReceiver(dateChangeReceiver,dateFilter);
		//没有退出广告的渠道数组
		quitExceptArray = getResources().getStringArray(R.array.no_exit_ad_list);
		avoidQuit = isExcepted();
		EventBus.getDefault().register(this);
		if (!PreferencesUtils.getString(this,null,"curDate_found_award_date","2015")
				.equals(CommonUtils.getDateString())){
			PreferencesUtils.remove(this,null,"curDate_found_award_ids");
		}
		if (PreferencesUtils.getBoolean(this,null,"welcome_start",false)){
			PreferencesUtils.putBoolean(this,null,"welcome_start",false);
			detectNewVersion();
		}
	}
	private void detectNewVersion() {
		if (AppUtil.isServiceRunning(this, AppUtil.getPackageName(this)
				+ ".services.AppUpdateService")) {
			DialogUtil.alertToast(getApplicationContext(), "正在下载新版本");
		} else {
			getAppNewVersion();
		}
	}
	private VersionData versionData;

	public void onEvent(HttpEvent e) {
		if (e.getParser() instanceof GetAppNewVersionParser) {
			GetAppNewVersionParser parser = (GetAppNewVersionParser) e
					.getParser();
			getNewVersion(parser.errCode, parser.errMsg, parser.versionData);
		}
	}
	private void getAppNewVersion() {
		VolleyHelper.post(new GetAppNewVersionRequest(this).make(),
				new GetAppNewVersionParser());
	}
	public void showNewVersionDialog(Context context, String title, String msg,
									 int force) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		if (force == 0) {
			builder.setPositiveButton("现在更新",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							starAppDownloadService();
							DialogUtil.alertToast(getApplicationContext(),
									"新版本已经开始下载，您可在通知栏观看下载进度");
						}

					});
			builder.setNegativeButton("稍后再说",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		} else {
			builder.setNegativeButton("现在更新",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							starAppDownloadService();
							DialogUtil.alertToast(getApplicationContext(),
									"新版本已经开始下载，您可在通知栏观看下载进度");
						}

					});
			builder.setPositiveButton("退出应用",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
		}
		builder.setCancelable(false).create().show();
	}
	public void starAppDownloadService() {
		Intent intent = new Intent(this, AppUpdateService.class);
		intent.putExtra("url", versionData.downLoadUrl);
		intent.putExtra("name", "电视粉");
		startService(intent);
	}
	public void getNewVersion(int errCode, String msg, VersionData version) {
		if (errCode == JSONMessageType.SERVER_CODE_OK
				&& !TextUtils.isEmpty(version.versionId)) {
			this.versionData = version;
			showNewVersionDialog(this, versionData.versionId, versionData.info,
					versionData.force);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (SmartBarUtils.hasSmartBar()) {
			getSupportMenuInflater().inflate(R.menu.slide_main, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onAtionClick(item.getItemId());
		return super.onOptionsItemSelected(item);
	}

	boolean isNeedShowAvoid;

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIsStateAlreadySaved = true;
		MobclickAgent.onPause(this);
	}
	Thread logoUpdateThread;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (logoUpdateThread != null && !logoUpdateThread.isInterrupted()) {
			logoUpdateThread.interrupt();
			logoUpdateThread = null;
		}
		stopService(new Intent(this, DlnaControlService.class));
		if (dateChangeReceiver != null){
			unregisterReceiver(dateChangeReceiver);
		}
		if (defaultSharedPreferences != null){
			defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		}
		EventBus.getDefault().unregister(this);
	}

	public void getWelcomeListTask() {
		VolleyRequest.welcomeList(null, 0, 10, this);
	}

	public void changePage(int item) {
		mViewPager.setCurrentItem(item);
	}

	boolean mIsStateAlreadySaved = false;

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (!isNeedShowAvoid) {
			openstatement();
		}
	}

	private void openstatement() {
		if (mIsStateAlreadySaved) {
			return;
		}
		DialogInfo info = new DialogInfo();
		info.title = "免责声明";
		info.confirm = "确定";
		info.cancel = "拒绝";
		info.contentColorResId = R.color.light_black;
		info.content = getString(R.string.statementcontent);
		CommonDialogFragment dialog = CommonDialogFragment.newInstance(info,
				false);
		dialog.setOnClickListener(new OnCommonDialogListener() {

			@Override
			public void onPositiveButtonClick() {
				PreferencesUtils.putBoolean(getApplicationContext(),
						"otherInfo", "isShowHelp", false);
				PreferencesUtils.putBoolean(getApplicationContext(), null,
						"isavoid", true);
				if (!PreferencesUtils.getBoolean(getApplicationContext(), null,
						"mainGuide")) {
					Intent intent = new Intent(getApplicationContext(),
							GuideActivity.class);
					intent.putExtra("type", GuideActivity.GUIDE_MAIN);
					startActivity(intent);
				}

			}

			@Override
			public void onNeutralButtonClick() {
			}

			@Override
			public void onNegativeButtonClick() {
				PreferencesUtils.putBoolean(getApplicationContext(),
						"otherInfo", "isShowHelp", false);
				finish();

			}
		});
		dialog.show(getSupportFragmentManager(), "statement");
	}

	// FragmentAdapter adapter;
	RecommendFragment recommendFragment;
	TvLiveFragment tvLiveFragment;
	HotLibFragment hotLibFragment;
	FoundIndexFragment foundFragment;
	PagerSlidingTabStrip pageTabs;

	private void initViewPager() {
		pageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		ArrayList<String> titles = new ArrayList<String>();
		recommendFragment = RecommendFragment.newInstance();
		hotLibFragment = HotLibFragment.newInstance();
		foundFragment = FoundIndexFragment.newInstance();
		titles.add(getString(R.string.home_page_recommend));
		fragments.add(recommendFragment);
		if (liveModule) {
			tvLiveFragment = TvLiveFragment.newInstance();
			titles.add(getString(R.string.tv_live));
			fragments.add(tvLiveFragment);
		}
		titles.add(getString(R.string.hot_program_lib));
		fragments.add(hotLibFragment);
		titles.add(getString(R.string.found));
		fragments.add(foundFragment);
		MyPagerAdapter adapter = new MyPagerAdapter(
				getSupportFragmentManager(), titles, fragments);
		mViewPager.setAdapter(adapter);
		pageTabs.setOnPageChangeListener(this);
		pageTabs.setViewPager(mViewPager, -1);
		setTabsValue();
	}

	private void setTabsValue() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		pageTabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP,
				getResources().getInteger(R.integer.tab_text_size), dm));
		pageTabs.setTextColorResource(R.color.light_black);
		pageTabs.setIndicatorColorResource(R.color.navigator_bg_color);
		pageTabs.setSelectedTextColorResource(R.color.navigator_bg_color);
		pageTabs.setTabBackground(0);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			getSlidingMenu().setMode(SlidingMenu.LEFT);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
//		if (arg0 == 3) {
//			pageTabs.changeRedTip(arg0, false);
//			if (PreferencesUtils.getBoolean(this, Constants.pushMessage,
//					Constants.KEY_FOUND)) {
//				PreferencesUtils.putBoolean(this, Constants.pushMessage,
//						Constants.KEY_FOUND, false);
//				foundFragment.reloadData();
//			}
//		}
		if (liveModule) {
			if (arg0 == 1 || arg0 == 3) {
				hotLibFragment.moveToTop();
			}
		} else {
			if (arg0 == 0 || arg0 == 2) {
				hotLibFragment.moveToTop();
			}
		}
	}

	public void hideFoundRedTip() {
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.KEY_FOUND, false);
		if (mViewPager.getCurrentItem() != 3) {
			pageTabs.changeRedTip(3, true);
		} else {
			foundFragment.reloadData();
		}
	}

	private long lastTime;
	private boolean hasQuitAd = true;
	private String[] quitExceptArray;
	private boolean avoidQuit;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getSlidingMenu().isMenuShowing()) {
				return super.onKeyDown(keyCode, event);
			} else {
				AccessDownload accessDownload = AccessDownload
						.getInstance(this);
				ArrayList<DownloadInfo> downloadInfos = accessDownload
						.queryDownloadInfo(DownloadInfoState.DOWNLOADING);
				if (downloadInfos != null && downloadInfos.size() > 0) {
					showCacheDialog();
					return true;
				} else if (!hasQuitAd || avoidQuit) {
					if (backEvent()) return super.onKeyDown(keyCode, event);
				} else if (hasQuitAd) {
					if (youmiWindow.isShown()) {
						if (spotView != null && spotView.isAttachedOnWindow()) {
							hideYoumiAd();
						} else {
							youmiWindow.setVisibility(View.GONE);
						}
					} else if (!quitShowed){
						showYoumiAd();
					} else {
						if (backEvent()) return super.onKeyDown(keyCode, event);
					}
				}
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggle();
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean backEvent() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime >= 0
                && currentTime - lastTime <= 2000) {
			if (P2PManager.get() != null)
				P2PManager.get().stop();
			android.os.Process.killProcess(android.os.Process.myPid());
			return true;
        } else {
            handler.sendEmptyMessage(1);
            lastTime = currentTime;
        }
		return false;
	}

	private boolean isExcepted() {
		try {
			String temp = AppUtil.getMetaData(this, "UMENG_CHANNEL");
			if (TextUtils.isEmpty(temp)){
				return false;
			}
			for (int i=0; i<quitExceptArray.length; i++){
				if(temp.equals(quitExceptArray[i])){
					return true;
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	private void showCacheDialog() {
		DialogInfo info = new DialogInfo();
		info.title = "正在为您缓存";
		info.confirm = "确定";
		info.neutral = "后台运行";
		info.content = "正在为您缓存\n退出将暂停任务";
		info.cancel = "取消";
		CommonDialogFragment dialog = CommonDialogFragment.newInstance(info,
				false);
		dialog.setOnClickListener(new OnCommonDialogListener() {

			@Override
			public void onPositiveButtonClick() {
				pauseDownload();
				finish();
			}

			private void pauseDownload() {
				AccessDownload accessDownload = AccessDownload
						.getInstance(SlidingMainActivity.this);
				ArrayList<DownloadInfo> downloadInfos = accessDownload
						.queryDownloadInfo();
				if (downloadInfos != null && downloadInfos.size() > 0) {
					for (DownloadInfo downloadInfo : downloadInfos) {
						if(downloadInfo.state <= 1){
							downloadInfo.state = DownloadInfoState.PAUSE;
							accessDownload.updateDownloadState(downloadInfo);
						}
					}
					DownloadInfo downloadInfo = downloadInfos.get(0);
					downloadInfo.state = DownloadInfoState.PAUSE;
					accessDownload.updateDownloadState(downloadInfo);
					Intent intent = new Intent(SlidingMainActivity.this,
							DownloadService.class);
					Bundle bundle = new Bundle();
					bundle.putInt(DownloadService.ACTION_KEY,
							DownloadService.ACTION_PAUSE);
					intent.putExtra("bundle", bundle);
					intent.putExtra(DownloadService.APPNAME_KEY,
							getString(R.string.app_name));
					intent.putExtra(DownloadService.APP_EN_NAME_KEY,
							"tvfanphone");
					intent.putExtra(DownloadManager.extra_loadinfo,
							downloadInfo);
					intent.putExtra("exit", true);
					startService(intent);
					
				}
			}

			@Override
			public void onNeutralButtonClick() {
				finish();
			}

			@Override
			public void onNegativeButtonClick() {

			}
		});
		dialog.show(getSupportFragmentManager(), "dialog");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_logo:
			MobclickAgent.onEvent(this, "zuocehua");
			showMenu();
			break;
		case R.id.iv_activity:
			//首页标题栏广告
			MobclickAgent.onEvent(this, "kugoufanxing");
			AdStatisticsUtil.adCount(this, Constants.homePage);
			FanxingManager.goMainUi(this);
			PreferencesUtils.putString(this, null, "curDate", CommonUtils.getDateString());
			break;
		default:
			onAtionClick(v.getId());
			break;
		}
	}

	private void onAtionClick(int id) {
		switch (id) {
		case R.id.action_history:
			MobclickAgent.onEvent(this, "lishijilu");
			startActivity(new Intent(this, PlayHistoryActivity.class));
//			String temp = "{\"push\":{\"type\":2,\"title\":\"每日抽奖\",\"text\":\"cjjjj\"}}";
//			new TvBaiduPushMessageReceiver().parseMessage(this,temp);
			break;
		case R.id.action_search:
//			String temp1 = "{\"push\":{\"text\":\"坦克12344\",\"programId\":57660,\"title\":\"WCA2015\",\"pushPic\":\"\",\"subId\":1641909}}";
//			temp1 = "{\"push\":{\"title\":\"直播1\",\"text\":\"244内容\",\"channel\":{\"id\":15,\"name\":\"北京卫视\",\"play\":[{\"url\":\"http://live.letv.com/weishi/play/index.shtml?channel=bjws\",\"videoPath\":\"\"},{\"url\":\"http://m.wasu.cn/content,freewap,RDUCHS-NB3G-IPS-04142898201890759,193524,38,6609216.page?profile=wasuClientH5_cj\",\"videoPath\":\"\"},{\"videoPath\":\"http://gslb.tv.sohu.com/live?cid=222612&type=hls\"},{\"videoPath\":\"http://58.135.196.138:8090/live/a08e874562b90c6e65030729506618f2/index.m3u8\"},{\"url\":\"pa://cctv_p2p_hdbtv1\",\"videoPath\":\"\"}]}}}";
//			new TvBaiduPushMessageReceiver().parseMessage(this,temp1);
			MobclickAgent.onEvent(this, "search");
			startActivity(new Intent(this, SearchActivity.class));
			break;
		case R.id.action_cache:
//			String temp2 = "{\"push\":{\"text\":\"简介12234\",\"programId\":59498,\"title\":\"聊斋新编\",\"pushPic\":\"\",\"subId\":0,\"pType\":11}}";
//			temp2 = "{\"push\":{\"text\":\"啥啥啥\",\"program\":{\"programId\":62157,\"subId\":1700847,\"ptype\":1},\"title\":\"盗墓笔记推\"}}";
//			new TvBaiduPushMessageReceiver().parseMessage(this,temp2);
			MobclickAgent.onEvent(this, "syhuancun");
			startActivity(new Intent(this, MyCacheActivity.class));
			break;
		}
	}

	@Override
	public void getWelcomeList(int errCode, ArrayList<WelcomeData> welcomeList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			String FileDIR = Constants.SDCARD_FLASH_FOLDER
					+ UpdateData.current().logoFileName;
			File flashDir = new File(FileDIR);
			if (!flashDir.exists()) {
				if (!TextUtils.isEmpty(UpdateData.current().logoDownURL)) {
					logoUpdateThread = new Thread() {
						public void run() {
							try {
								BitmapUtils.loadImageFromUrlAnd2File(UpdateData
										.current().logoDownURL + ".jpg");
							} catch (Exception e) {
								e.printStackTrace();
							}
							LocalInfo.saveFlashData(SlidingMainActivity.this);
						}
					};
					logoUpdateThread.start();
				}
			}
		}
	}

	private void updateFoundRedTip(){
		if (UserNow.current().dayLoterry == 0 || pushSharedPreferences.getBoolean(Constants.KEY_FOUND,false)){
			pageTabs.changeRedTip(3, true);
		} else {
			pageTabs.changeRedTip(3, false);
		}
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Constants.KEY_FOUND) || key.equals(Constants.KEY_DAYLOTTERY)) {
			updateFoundRedTip();
//			boolean value = sharedPreferences.getBoolean(key, false);
//			if (value) {
//				hideFoundRedTip();
//			}
		}else if(key.equals("curDate")){
			updateFestivalLogo(1);
		}else {
			if (mTalkTvMenuFragment != null && mTalkTvMenuFragment.isAdded()) {
				mTalkTvMenuFragment.changeTip(key);
			}
		}

	}

	private void loginPoint(){
		if (UserNow.current().userID>0
				&& !PreferencesUtils.getString(this,null,"curDate_login","2015")
				.equals(CommonUtils.getDateString())){
			if (!TextUtils.isEmpty(UserNow.current().name)
					&& !TextUtils.isEmpty(UserNow.current().passwd))
			VolleyUserRequest.login(new OnLogInListener() {
				@Override
				public void loginResult(int errCode, int changePoint, String errMsg) {
					if (errCode == JSONMessageType.SERVER_CODE_OK){
						PreferencesUtils.putString(SlidingMainActivity.this,null,"curDate_login",CommonUtils.getDateString());
					}
				}
			}, null);
		}
	}
	public void updateFestivalLogo(int id) {
		if (activityIcon == null || id == 0) {
			return;
		}
		activityId = id;
		if (PreferencesUtils.getString(this,null,"curDate","2015").equals(CommonUtils.getDateString())){
			activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty1));
		}else {
			activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty2));
		}
		activityIcon.setVisibility(View.VISIBLE);
//		RotateAnimation rotateStart = new RotateAnimation(0, 20,
//				Animation.RELATIVE_TO_SELF, 0.55f,
//				Animation.RELATIVE_TO_SELF, 0.0f);
//		rotateStart.setDuration(1000);
//		rotateStart.setRepeatMode(Animation.REVERSE);
//		rotateStart.setRepeatCount(Animation.INFINITE);
//		activityIcon.startAnimation(rotateStart);
	}

	private GetAddressParser aparser = new GetAddressParser();

	public void checkAddressRequest() {
		VolleyHelper.post(new GetAddressRequest().make(), new ParseListener(
				aparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (aparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					((TalkTvApplication) getApplication()).mAddressData = aparser.address;
				}
			}
		}, null);
	}

	private class ReadXml implements Runnable {
		@Override
		public void run() {
			try {
				InputStream is = getAssets().open("Provinces.xml");
				pullParser.parse(is, 0, "Province", "ID", "ProvinceName", null);
				is = getAssets().open("Cities.xml");
				pullParser.parse(is, 1, "City", "ID", "CityName", "PID");
				is = getAssets().open("Districts.xml");
				pullParser
						.parse(is, 2, "District", "ID", "DistrictName", "CID");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showYoumiAd() {
		setCustomerSpotAd();
	}
	
	public void hideYoumiAd() {
		youmiLayout.removeView(spotView);
		youmiWindow.setVisibility(View.GONE);
	}
	
	public void initYoumiAd() {
		AdManager.getInstance(this).init("8ada6bbe5d21150d", "12842458dcb443ee");
		loadYoumiSpot();
	}
	
	public void loadYoumiSpot() {
		SpotManager.getInstance(this).loadSpotAds();
		SpotManager.getInstance(this).setAnimationType(
				SpotManager.ANIM_ADVANCE);
		SpotManager.getInstance(this).setSpotOrientation(
				SpotManager.ORIENTATION_LANDSCAPE);
	}
	
	protected void onRestart() {
		super.onRestart();
		loadYoumiSpot();
	};

	CustomerSpotView spotView;
	public void setCustomerSpotAd() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 获取广告图片不能在UI上操作。
				spotView = SpotManager.getInstance(SlidingMainActivity.this).cacheCustomerSpot(SlidingMainActivity.this, youmiListener);
				if (spotView != null) {
					spotView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, SlidingMainActivity.this.getResources().getDisplayMetrics());
							int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, SlidingMainActivity.this.getResources().getDisplayMetrics());
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
							params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
							if (spotView != null){
								if(spotView.getParent()!= null){
									youmiLayout.removeView(spotView);
								}
								youmiLayout.addView(spotView, params);
								youmiWindow.setVisibility(View.VISIBLE);
							}
						}
					});
				} else {
					hasQuitAd = false;
				}
			}
		}).start();
	}
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					DialogUtil.alertToast(getApplicationContext(),
							"再按一次退出电视粉");
//					if (P2PManager.get() != null)
//						P2PManager.get().stop();
//					android.os.Process.killProcess(android.os.Process.myPid());
					break;
				case 2:
					DialogUtil.updateScoreToast((String)msg.obj);
					break;
			}
		}
	};
	boolean quitShowed;
	SpotDialogListener youmiListener = new SpotDialogListener() {
		@Override
		public void onShowSuccess() {
			Log.i("YoumiAdDemo", "展示成功");
			AdStatisticsUtil.adCount(SlidingMainActivity.this,Constants.quitAd);
			quitShowed = true;
		}

		@Override
		public void onShowFailed() {
			Log.i("YoumiAdDemo", "展示失败");
			if(backEvent()){
				finish();
			}
		}

		@Override
		public void onSpotClosed() {
			Log.i("YoumiAdDemo", "展示关闭");
		}

		@Override
		public void onSpotClick() {
			Log.i("YoumiAdDemo", "点击");
		}

	};

	public void onEvent(EventMessage msg){
		if (msg.name.equals("SlidingMainActivity")){
			if (!TextUtils.isEmpty(msg.bundle.getString("message"))){
				Message tipMsg = handler.obtainMessage(2);
				tipMsg.obj = msg.bundle.getString("message");
				handler.sendMessage(tipMsg);
			}
		} else if (msg.name.equals("SlidingMainActivity_playinfo")){
			VolleyHelper.post(new PlayInfoRequest(this, msg.bundle).make(), new ParseListener(new ResultParser()) {
				@Override
				public void onParse(BaseJsonParser parser) {

				}
			},null);
		} else if (msg.name.equals("SlidingMainActivity_channelStat")){
			String content = msg.bundle.getString("content");
			VolleyHelper.post(new ChannelStatRequest(this,content).make(), new ParseListener(new ResultParser()) {
				@Override
				public void onParse(BaseJsonParser parser) {

				}
			},null);
		}
	}
	public void onEvent(UserInfoEvent event){
		updateFoundRedTip();
	}
}
