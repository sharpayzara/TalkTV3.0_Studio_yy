package com.sumavision.talktv2.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.badpx.webp.support.WebpDecoder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sumavision.cachingwhileplaying.CachingWhilePlayingService;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.activity.help.MediaControlFragmentHelperImpl;
import com.sumavision.talktv2.bean.AddressData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.SimpleClass;
import com.sumavision.talktv2.fragment.ShakeProgramDetialFragment;
import com.sumavision.talktv2.fragment.VideoControllerFragment;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.tvata.p2p.P2PManager;

import org.litepal.LitePalApplication;

import java.io.File;

import crack.cracker.JarCracker;

/**
 * application单例
 * 
 * @author suma-hpb
 * 
 */
public class TalkTvApplication extends LitePalApplication {

	public final String DISK_CACHE_DIR = "/TVFan/imageCache";
	public AddressData mAddressData;

	public TalkTvApplication(){
		super();
	}

	@Override
	public void onCreate() {
		
		final Context mContext = this;
		new Runnable(){
			@Override
			public void run() {
				
				new SimpleClass((Application)mContext);
				WebpDecoder.setup();
				new MediaControlFragmentHelperImpl();
				if (CommonUtils.externalMemoryAvailable()) {
					String root = getExternalFilesDir(null) + "/";
					String imgDir = root + DISK_CACHE_DIR;
					String imgSplah = Constants.SDCARD_FLASH_FOLDER;
					if (!new File(imgSplah).exists()) {
						new File(imgSplah).mkdirs();
					}
					File dir = new File(imgDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
				}
				final float countale = getResources().getDisplayMetrics().density;
				if (countale >= 2.0) {
					Constants.PIC_SUFF = Constants.PIC_BIG;
				} else if (countale >= 1.5) {
					Constants.PIC_SUFF = Constants.PIC_MIDDLE;
				} else {
					Constants.PIC_SUFF = Constants.PIC_SMALL;
				}
				PlayerActivity.programHalfFragmentName = ProgramDetailHalfActivity.class
						.getName();
				PlayerActivity.shakeProgramDetailFragmentName = ShakeProgramDetialFragment.class
						.getName();
				PlayerActivity.controllerFragmentName = VideoControllerFragment.class
						.getName();
				startCachingWhilePlayingService(mContext);
				if (CommonUtils.getCurProcessName(mContext).equals(getPackageName())) {
					startCachingWhilePlayingService(mContext);
//					new CrackJarInit(mContext).init(0);
					JarCracker.getInstance().init(mContext);
//					startP2PService();
				}
//				if (getPackageName().equals(CommonUtils.getCurProcessName(mContext))){
//					new CrackJarInit(mContext).init(0);
//					new CrackJarInit(mContext).init(1);
//					new BaiduPanBduss(mContext, Constants.host).init();
//				}
				TalkTvExcepiton.getInstance().init(getApplicationContext());
				VolleyQueueManage.init(getApplicationContext());
				ImageLoaderHelper.initImageLoader(getApplicationContext());
				JSONMessageType.USER_ALL_SDCARD_FOLDER = getExternalFilesDir(null)
						+ File.separator
						+ "TVFan/";
				JSONMessageType.USER_PIC_SDCARD_FOLDER = getExternalFilesDir(null)  + "/TVFan/";
			}
		}.run();
		super.onCreate();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ImageLoader.getInstance().clearMemoryCache();
	}

	private void startCachingWhilePlayingService(Context context) {
		Intent intent = new Intent(context, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_START_SERVICE);
		intent.putExtra("appType", CachingWhilePlayingService.app_type.tvfan);
		startService(intent);
	}

	public void startP2PService() {
		String zone = "zt15120802";
		String native_dir = this.getCacheDir() + "p_pie_1";
		Log.d("tvapp", "startP2PService at " + native_dir);
		File ndir = new File(native_dir);
		if (ndir.exists() == false)
			ndir.mkdir();
		P2PManager.init(native_dir, zone);
		P2PManager.get().setErrorEvent(new Runnable() {

			@Override
			public void run() {
				Log.d("0..............................................", "p2p 00000000000000000      manager to run error event");

			}

		});

		Log.d("startservice", "p2pManager started!");
		P2PManager.get().start();
		P2PManager.get().set_m3u8_endstring("?");
		P2PManager.get().set_ts_endstring("?");


	}

}
