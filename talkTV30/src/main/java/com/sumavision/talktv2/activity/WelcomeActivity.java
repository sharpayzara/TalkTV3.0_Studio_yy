package com.sumavision.talktv2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UpdateData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.HttpErrorEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.MarketNotModuleParser;
import com.sumavision.talktv2.http.json.MarketNotModuleRequest;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.service.NetManagerReceiver;
import com.sumavision.talktv2.service.PushUtils;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.BitmapUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImeiUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.WebpUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import de.greenrobot.event.EventBus;

//import com.vbyte.p2p.P2PModule;

/**
 * 欢迎页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class WelcomeActivity extends Activity {

	private NetManagerReceiver mNetworkStateReceiver = new NetManagerReceiver(
			null);

	String vid;
	boolean updated = false;
	private final boolean isRequestComment = true;
	private int lefttimes;
	
	private ArrayList<String> moduleName;
	private boolean liveModule = true;
	private String[] noAdList;
	private boolean isMatch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.openActivityDurationTrack(false);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_welcome);
		vid = PreferencesUtils.getString(this, null,
				Constants.KEY_REQUEST_COMMENT, null);
		if (vid == null || !vid.equals(AppUtil.getAppVersionId(this))) {
			vid = AppUtil.getAppVersionId(this);
			updated = true;
			PreferencesUtils.putString(this, null,
					Constants.KEY_REQUEST_COMMENT, vid);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateReceiver, filter);
		MobclickAgent.onEvent(getApplicationContext(), "qidong");
		LocalInfo.getUserData(this);

		isNeedShowHelp = PreferencesUtils.getBoolean(this, "otherInfo",
				"isShowHelp", true);
		if (AppUtil.getMetaData(WelcomeActivity.this, "UMENG_CHANNEL").equals("xiaomi")){
			isNeedShowHelp = false;
		}
		if ((isNeedShowHelp || updated) && isRequestComment) {
			PreferencesUtils.putInt(this, null, Constants.KEY_COMMENT_TIMES, 3);
		}
		lefttimes = PreferencesUtils.getInt(this, null,
				Constants.KEY_COMMENT_TIMES, -1);
		if (lefttimes > -1) {
			lefttimes--;
			PreferencesUtils.putInt(this, null, Constants.KEY_COMMENT_TIMES,
					lefttimes);
		}
		long cacheRecommendTime = PreferencesUtils.getLong(this, null,
				Constants.RECOMMEND_CACHE_KEY, 0);
		if (cacheRecommendTime != 0
				&& (System.currentTimeMillis() / 1000 - cacheRecommendTime) >= 300) {
			PreferencesUtils.putString(this, null,
					Constants.RECOMMEND_CACHE_CONTENT, "");
		}
		if (Constants.canSwitch){
			if (!TextUtils.isEmpty(PreferencesUtils.getString(this, null, "req_host"))){
				Constants.host = PreferencesUtils.getString(this, null, "req_host");
				VolleyRequest.url = Constants.host;
			}
		}
//		try {
//			if (AppUtil.getMetaData(this, "UMENG_CHANNEL").equals("anZhi")) {
//				findViewById(R.id.img_anzhi).setVisibility(View.VISIBLE);
//			}
//		} catch (Exception e) {
//
//		}
//		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		String imei = manager.getDeviceId();
		ImeiUtil.getInstance(getApplication()).initRecord();
		String imei = ImeiUtil.getInstance(getApplication()).getUniqueId();
		if (!TextUtils.isEmpty(imei)) {
			UserNow.current().imei = imei;
			PreferencesUtils.putString(this, null, "imei", imei);
		} else {
			String mac = AppUtil.getMac(this);
			UserNow.current().imei = mac;
			if (!TextUtils.isEmpty(mac)) {
				PreferencesUtils.putString(this, null, "imei", mac);
			} else {
				UUID uuid = UUID.randomUUID();
				Log.i("mylog", uuid.toString());
				PreferencesUtils.putString(this, null, "imei", uuid.toString());
			}
		}
//		P2PModule p2pModule = P2PModule.getInstance("vbyte-v7a");
//		p2pModule.setAppInfo("tD5WDNkiKKLBLPWRdh8O", "eoYqPx32sLLGV1UpIn28hJ5rk", "BICjoxQbzKhebiOmrSf5OmC2t", imei);
		if (AppUtil.getNetStatus(this)) {
			handler.removeMessages(OPEN_MAIN_PAGE);
			checkLogoAvailable(false);
			splashImg = (ImageView) findViewById(R.id.splash_image);
//			splashImg.setImageResource(R.drawable.splash_image);
			splashImg.setImageBitmap(WebpUtils.getAssetBitmap(this,
					"webp/splash_img_new.webp"));
			
 			String meta = AppUtil.getMetaData(this, "UMENG_CHANNEL");
			final MarketNotModuleParser moduleParser = new MarketNotModuleParser();
			PreferencesUtils.putBoolean(this,null,"welcome_start",true);
			noAdList = getResources().getStringArray(R.array.no_youmi_xunfei_ad_list);
			isMatch = isExcepted(this, noAdList);
			VolleyHelper.post(new MarketNotModuleRequest(meta).make(), new ParseListener(moduleParser) {
				@Override
				public void onParse(BaseJsonParser parser) {
					if (moduleParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						moduleName = moduleParser.moduleName;
						if (isMatch) {
							moduleParser.quitAd = false;
							moduleParser.stopAd = false;
							moduleParser.xunfeiAd = false;
							moduleParser.jvxiaoAd = false;
							moduleParser.amgWelcome = false;
							moduleParser.kugouHalf = false;
						}
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.hasQuitAd, moduleParser.quitAd);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.hasPauseAd, moduleParser.stopAd);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.kedaxunfei, moduleParser.xunfeiAd);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.kugouAd, moduleParser.kugouAd);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.vipActivity, moduleParser.vipActivity);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.jvxiaoAd, moduleParser.jvxiaoAd);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.amgWelcome, moduleParser.amgWelcome);
						PreferencesUtils.putBoolean(WelcomeActivity.this, null, Constants.kugouHalf, moduleParser.kugouHalf);
					}
					if (moduleName != null && moduleName.size() > 0) {
						for (String s : moduleName) {
							if (s.equals("liveModule")) {
								liveModule = false;
							}
						}
					}
					PreferencesUtils.putBoolean(WelcomeActivity.this, null, "liveModule", liveModule);

				}
			}, null);
//			if (UpdateData.current().isNeedUpdateLogo
//					&& UpdateData.current().isOnNewLogoTime) {
//				getLogoFile();
//			}
			/*if (!isNeedShowHelp
					&& PreferencesUtils.getBoolean(WelcomeActivity.this,null,Constants.amgWelcome,false)){
				SpreadScreenAdManager.getInstance(this).requestSpreadScreenAd(
						"adp_2f93a5a0bacc25c478e5f0ef3c1198fb2361aad7", mAdRequestListener);
				openMainTabActivity(3000);
			} else {
			}*/
				if (UpdateData.current().isNeedUpdateLogo
						&& UpdateData.current().isOnNewLogoTime) {
					getLogoFile();
				} else {
					openMainTabActivity(3000);
				}
		} else {
			handler.sendEmptyMessage(OPEN_CACHE_CENTER);
		}

		if (!PushUtils.hasBind(this)) {
			initWithApiKey();
		} else {
			PushManager.resumeWork(this);
		}
//		AdManager.getInstance(this).init("8ada6bbe5d21150d", "12842458dcb443ee", false);
	}
	boolean isAlive = true;
/*
	AdRequestListener mAdRequestListener = new AdRequestListener() {
		@Override
		public void onRequestSucceed() {
			Log.v("msg_welcom", "开屏广告信息请求成功");
			if (isAlive){
				SpreadScreenAdManager.getInstance(WelcomeActivity.this).showSpreadScreenAd(SpreadScreenAdListener,true);
			}
			handler.removeMessages(OPEN_MAIN_PAGE);
		}

		@Override
		public void onRequestFail(int errorCode) {
			// 关于错误码errorCode的解读：-1为网络连接失败 ,-1000为该广告已经下架
			Log.v("msg_welcom", "开屏广告信息请求失败，错误码为:" + errorCode);
//			startActivity(new Intent(WelcomeActivity.this, SlidingMainActivity.class));
//			finish();
			openMainTabActivity(0);
		}
	};
	SpreadScreenAdListener SpreadScreenAdListener = new SpreadScreenAdListener() {
		@Override
		public void onSpreadScreenAdFail() {
			Log.v("msg_welcom", "开屏广告展示失败");
			startActivity(new Intent(WelcomeActivity.this, SlidingMainActivity.class));
			finish();
		}

		@Override
		public void onSpreadScreenAdComplete(Context context) {
			Log.v("msg_welcom", "开屏展示成功完成");
			AdStatisticsUtil.adCount(WelcomeActivity.this,20,0);
			openMainTabActivity(0);
//			startActivity(new Intent(WelcomeActivity.this, SlidingMainActivity.class));
//			finish();
		}

		@Override
		public void onSpreadScreenCloseAd(Activity activity) {
			Log.v("msg_welcom", "跳过开屏广告");
			activity.finish();
			openMainTabActivity(0);
		}
	};
*/
	boolean isBusy;

	private void initWithApiKey() {
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				PushUtils.getMetaValue(this, "api_key"));

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("WelcomeActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("WelcomeActivity");
		MobclickAgent.onPause(this);
	}

	private void openMainTabActivity(long delayMillis) {
		handler.removeMessages(OPEN_MAIN_PAGE);
		handler.sendEmptyMessageDelayed(OPEN_MAIN_PAGE, delayMillis);
	}

	private boolean isNeedShowHelp;

	private static final int REQUEST_HELP = 10;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_HELP) {
			openMainTabActivity(1000);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private final int OPEN_MAIN_PAGE = 1;
	private final int OPEN_CACHE_CENTER = 3;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case OPEN_CACHE_CENTER:
				Intent i = new Intent(WelcomeActivity.this,
						MyCacheActivity.class);
				i.putExtra("from", 1);
				startActivity(i);
				finish();
				break;
			case OPEN_MAIN_PAGE:
				isAlive = false;
				if (isNeedShowHelp && !AppUtil.getMetaData(WelcomeActivity.this, "UMENG_CHANNEL").equals("xiaomi")) {
					Intent intent = new Intent(WelcomeActivity.this,
							HelpActivity.class);
					intent.putExtra("fromSplash", true);
					startActivity(intent);
				} else {
					Intent intent = new Intent(WelcomeActivity.this,
							SlidingMainActivity.class);
					startActivity(intent);
				}
				finish();
				break;
			default:
				break;
			}
			return false;
		}
	});

	private String FileDIR;
	private ImageView splashImg;

	private void getLogoFile() {
		FileDIR = Constants.SDCARD_FLASH_FOLDER
				+ UpdateData.current().logoFileName;
		File f = new File(FileDIR);
		if (f.exists()) {
			splashImg.setImageBitmap(BitmapUtils.getLocalBitmap(FileDIR));
		}
		handler.removeMessages(OPEN_MAIN_PAGE);
		handler.sendEmptyMessageDelayed(OPEN_MAIN_PAGE, 3000);
	}

	private void checkLogoAvailable(boolean isSave) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		String start = UpdateData.current().startTime;
		String end = UpdateData.current().endTime;
		if (now.compareTo(start) >= 0 && now.compareTo(end) <= 0) {
			UpdateData.current().isOnNewLogoTime = true;
		} else {
			UpdateData.current().isOnNewLogoTime = false;
		}
		if (now.compareTo(end) > 0) {
			AppUtil.clearOldLogo();
			UpdateData.current().isNeedUpdateLogo = false;
		} else {
			if (UpdateData.current().logoFileName != null
					&& !UpdateData.current().logoFileName.equals(UpdateData
							.current().logoServerFileName)) {
				AppUtil.clearOldLogo();
				UpdateData.current().logoFileName = UpdateData.current().logoServerFileName;
				UpdateData.current().isNeedUpdateLogo = true;
			} else {
				UpdateData.current().isNeedUpdateLogo = true;
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		handler.removeMessages(OPEN_CACHE_CENTER);
		handler.removeMessages(OPEN_MAIN_PAGE);
		unregisterReceiver(mNetworkStateReceiver);
	}


	public void onEvent(HttpErrorEvent e) {
		Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
	}
	private boolean isExcepted(Context context, String[] array) {
		try {
			String temp = AppUtil.getMetaData(context, "UMENG_CHANNEL");
			if (TextUtils.isEmpty(temp)){
				return false;
			}
			for (int i=0; i<array.length; i++){
				if(temp.equals(array[i])){
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
