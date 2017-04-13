package com.sumavision.talktv2.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv.videoplayer.activity.HeZiWebActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VersionData;
import com.sumavision.talktv2.http.listener.OnAppNewVersionListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.service.AppUpdateService;
import com.sumavision.talktv2.service.PushUtils;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置页面
 * 
 * @author suma-hpb
 * 
 */
public class SettingActivity extends BaseActivity implements OnClickListener,
		OnAppNewVersionListener {
	private final int MSG_CLEAN_OVER = 1;
	private final int MSG_CLEAN_ERROR = 2;

	private final int CACHE_SDCARD = 0;
	private final int CACHE_PHONE = 1;
	ArrayList<String> dirs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_setting);
		setContentView(R.layout.activity_setting);
		initViews();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SettingActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SettingActivity");
		super.onResume();
	}

	private ImageView pushMsgIndicator, playSwitch, autoPlay;
	private TextView cachePath;

	private void initViews() {
		pushMsgIndicator = (ImageView) findViewById(R.id.indicator);
		playSwitch = (ImageView) findViewById(R.id.imgv_play_switch);
		autoPlay = (ImageView) findViewById(R.id.imgv_auto_play);
		findViewById(R.id.about).setOnClickListener(this);
		findViewById(R.id.help).setOnClickListener(this);
		findViewById(R.id.notification_layout).setOnClickListener(this);
		findViewById(R.id.rlayout_auto_play).setOnClickListener(this);
		findViewById(R.id.rlayout_halfauto_play).setOnClickListener(this);
		findViewById(R.id.boxconfig).setOnClickListener(this);
		findViewById(R.id.new_version).setOnClickListener(this);
		findViewById(R.id.score).setOnClickListener(this);
		findViewById(R.id.tv_clear).setOnClickListener(this);
		cachePath = (TextView) findViewById(R.id.cache_path_type);

		int type = getCachePath();
		dirs = CommonUtils.getStoragePath(this);
		if (dirs.size() == 0) {
			cachePath.setText("无SD卡");
		} else if (dirs.size() == 1) {
			if (Environment.isExternalStorageRemovable()) {
				cachePath.setText("SD卡");
			} else {
				cachePath.setText("手机内存");
			}
		} else {
			if (type == 0) {
				cachePath.setText("SD卡");
			} else {
				cachePath.setText("手机内存");
			}
		}
		findViewById(R.id.tv_cache_path).setOnClickListener(this);

		setPushMsgIndicator(PreferencesUtils.getBoolean(this, null, "isOn",
				true));
		setAutoPlay(PreferencesUtils.getBoolean(this,
				PlayerActivity.SP_AUTO_PLAY, "auto", true));
		setHalfAutoPlay(PreferencesUtils.getBoolean(this,
					null, "half_auto", true));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case R.id.help:
			startActivity(new Intent(this, HelpActivity.class));
			break;
		case R.id.new_version:
			if (AppUtil.isServiceRunning(this, AppUtil.getPackageName(this)
					+ ".services.AppUpdateService")) {
				DialogUtil.alertToast(this, "正在下载新版本");
			} else {
				getAppNewVersion();
			}
			break;
		case R.id.score:
			openScoreActivity();
			break;
		case R.id.boxconfig:
			openboxconfig();
			break;
		case R.id.rlayout_auto_play:
			boolean play = PreferencesUtils.getBoolean(this,
					PlayerActivity.SP_AUTO_PLAY, "auto", true);
			play = !play;
			PreferencesUtils.putBoolean(this, PlayerActivity.SP_AUTO_PLAY,
					"auto", play);
			setAutoPlay(play);
			break;
		case R.id.notification_layout:
			boolean isOn = PreferencesUtils
					.getBoolean(this, null, "isOn", true);
			PreferencesUtils.putBoolean(this, null, "isOn", !isOn);
			setPushMsgIndicator(!isOn);
			break;
		case R.id.rlayout_halfauto_play:
			boolean auto = PreferencesUtils.getBoolean(this,
					null, "half_auto", true);
			auto = !auto;
			PreferencesUtils.putBoolean(this, null, "half_auto", auto);
			setHalfAutoPlay(auto);
			break;
		case R.id.tv_clear:
			deleteMemoryRoot();
			break;
		case R.id.tv_cache_path:
			showCachePathDialog();
			break;
		default:
			break;
		}
	}

	private void deleteMemoryRoot() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				deleteFileExceptCache(new File(
						JSONMessageType.USER_ALL_SDCARD_FOLDER));
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				handler.sendEmptyMessage(MSG_CLEAN_OVER);
				super.onPostExecute(result);
			}
		}.execute();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_CLEAN_OVER:
				Toast.makeText(SettingActivity.this, "清理成功！", Toast.LENGTH_LONG)
						.show();
				break;
			case MSG_CLEAN_ERROR:
				Toast.makeText(SettingActivity.this, "清理出错！", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
		};
	};

	private void openboxconfig() {
		Intent intent = new Intent(this, HeZiWebActivity.class);
		intent.putExtra("url", "http://10.1.1.1:8890");
		intent.putExtra("title", "配置盒子");
		startActivity(intent);
	}

	private void openScoreActivity() {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + getPackageName()));
			startActivity(intent);
		} catch (ActivityNotFoundException E) {
			Toast.makeText(SettingActivity.this, "亲，您还没有安装应用市场哦！",
					Toast.LENGTH_SHORT).show();
		}
	}

	private VersionData versionData;

	private void getAppNewVersion() {
		VolleyRequest.getAppNewVersion(this, this, this);
	}

	public void showNewVersionDialog(Context context, String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
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
		builder.create().show();
	}

	public void starAppDownloadService() {
		Intent intent = new Intent(this, AppUpdateService.class);
		intent.putExtra("url", versionData.downLoadUrl);
		intent.putExtra("name", "电视粉");
		startService(intent);
	}

	// private void setPushMsgIndicatorFile(int isOpen) {
	// if (isOpen == 1) {
	// pushMsgIndicator.setImageResource(R.drawable.my_fast_open);
	//
	// } else if (isOpen == 2) {
	// pushMsgIndicator.setImageResource(R.drawable.my_fast_close);
	//
	// }
	// }

	private void setAutoPlay(boolean on) {
		if (on) {
			playSwitch.setImageResource(R.drawable.my_fast_open);
		} else {
			playSwitch.setImageResource(R.drawable.my_fast_close);
		}
	}
	
	private void setHalfAutoPlay(boolean on) {
		if (on) {
			autoPlay.setImageResource(R.drawable.my_fast_open);
		} else {
			autoPlay.setImageResource(R.drawable.my_fast_close);
		}
	}

	private void setPushMsgIndicator(boolean isOn) {
		if (isOn) {
			pushMsgIndicator.setImageResource(R.drawable.my_fast_open);
			if (!PushUtils.hasBind(this)) {
				initWithApiKey();
			}
		} else {
			pushMsgIndicator.setImageResource(R.drawable.my_fast_close);
			// if (PushUtils.hasBind(this)) {
			// outWithApiKey();
			// }
		}
	}

	// 初始化推送服务
	private void initWithApiKey() {

		// Push: 无账号初始化，用api key绑定
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				PushUtils.getMetaValue(SettingActivity.this, "api_key"));

	}

	// // 注销服务
	// private void outWithApiKey() {
	// PushManager.stopWork(getApplicationContext());
	// }

	@Override
	public void getNewVersion(int errCode, String msg, VersionData version) {
		if (errCode == JSONMessageType.SERVER_CODE_OK
				&& !TextUtils.isEmpty(version.versionId)) {
			versionData = version;
			showNewVersionDialog(this, versionData.versionId, versionData.info);
		} else {
			DialogUtil.alertToast(getApplicationContext(), "当前版本已经是最新版本");
		}

	}

	public void deleteFileExceptCache(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory() && !file.getName().equals("cache")) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					this.deleteFileExceptCache(files[i]);
				}
			}
			file.delete();
		}
	}

	private void showCachePathDialog() {
		ArrayList<String> dirs = CommonUtils.getStoragePath(this);
		// 只有一个也无需设置
		if (dirs.size() == 1 || dirs.size() == 0) {
			Toast.makeText(this, "手机只有一种存储空间，无需设置", Toast.LENGTH_SHORT).show();
			return;
		}
		AccessDownload accessDownload = AccessDownload.getInstance(this);
		ArrayList<DownloadInfo> downloadInfos = accessDownload
				.queryDownloadInfo();
		if (downloadInfos != null && downloadInfos.size() > 0) {
			Toast.makeText(this, "当前有正在缓存的任务，无法设置", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		boolean[] checkedItem = new boolean[] { false, false };
		checkedItem[getCachePath()] = true;
		final String[] items = new String[] {
				getStorageRoom(new File(dirs.get(1)), 0),
				getStorageRoom(new File(dirs.get(0)), 1) };
		final String[] typeItems = new String[] { "SD卡", "手机内存" };
		builder.setSingleChoiceItems(items, getCachePath(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setCachePath(which);
						cachePath.setText(typeItems[which]);
						dialog.dismiss();
					}
				});
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private int getCachePath() {
		return PreferencesUtils.getInt(this, null, "cache_path_type");
	}

	private void setCachePath(int value) {
		if (value == CACHE_SDCARD || value == CACHE_PHONE) {
			PreferencesUtils.putInt(this, null, "cache_path_type", value);
		}
	}

	private String getStorageRoom(File path, int type) {
		StatFs localStatFs = new StatFs(path.getPath());
		long l1 = localStatFs.getBlockCount();
		long l2 = localStatFs.getAvailableBlocks();
		long l3 = localStatFs.getBlockSize();
		long nSDTotalSize = (l1 * l3);
		long nSDFreeSize = (l2 * l3);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String allSize = decimalFormat.format(nSDTotalSize
				/ (1024 * 1024 * 1024.0));
		String freeSize = decimalFormat.format(nSDFreeSize
				/ (1024 * 1024 * 1024.0));
		String space = "";
		if (type == 0) {
			space = String
					.format("SD卡\n剩余：%1$sG / 总量：%2$sG", freeSize, allSize);
		} else {
			space = String.format("手机内存\n剩余：%1$sG / 总量：%2$sG", freeSize,
					allSize);
		}
		return space;
	}
}
