package com.sumavision.talktv2.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.fragment.CacheJujiListFragment;
import com.sumavision.talktv2.fragment.CacheTvFragment;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

public class ProgramCacheActivity extends BaseActivity {
	private ProgramData programData = new ProgramData();
	private CacheJujiListFragment jujiListFragment;
	private CacheTvFragment tvFragment;
	private String TAG = "ProgramCacheActivity";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setTitle("选择剧集");
		getExtra();
		instantsJujiFragment();
	}

	public SourcePlatform sourcePlat;

	private void getExtra() {
		Bundle bundle = getIntent().getBundleExtra("bundle");
		if (bundle != null) {
			programData.programId = bundle.getLong("programId");
			programData.pType = bundle.getInt("pType");
			programData.pic = bundle.getString("programPic");
			programData.name = bundle.getString("programName");
			sourcePlat = (SourcePlatform) bundle.getSerializable("plat");
		}
	}

	TextActionProvider confirmAction;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.program_cache, menu);
		confirmAction = (TextActionProvider) menu.findItem(R.id.action_confirm)
				.getActionProvider();
		if (confirmAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		confirmAction.setShowText(R.string.action_confirm);
		confirmAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSubmitCache();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private void onSubmitCache() {
		try {
			ArrayList<JiShuData> datas = getCacheInfoData();
			if (datas != null && datas.size() > 0) {
				Collections.reverse(datas);
				AccessDownload accessDownload = AccessDownload
						.getInstance(ProgramCacheActivity.this);
				handler.sendEmptyMessage(MSG_SHOW_DIALOG);
				for (JiShuData jishuData : datas) {
					DownloadInfo downloadInfo = new DownloadInfo();
					downloadInfo.programId = (int) programData.programId;
					downloadInfo.subProgramId = jishuData.id;
					downloadInfo.programPic = programData.pic;

					InternalExternalPathInfo internalExternalPathInfo = CommonUtils
							.getInternalExternalPath(this);
					String path = null;

					// 获取用户设置的存储路径，0外部存储，1内部存储
					int type = PreferencesUtils.getInt(this, null,
							"cache_path_type");
					if (type == 0) {
						if (internalExternalPathInfo.removableSDcard != null) {
							path = internalExternalPathInfo.removableSDcard;
						} else {
							// 如果没有外置，则判断有没有内置的，如果有则设置默认路径为内置
							if (internalExternalPathInfo.emulatedSDcard != null) {
								PreferencesUtils.putInt(this, null,
										"cache_path_type", 1);
								path = internalExternalPathInfo.emulatedSDcard;
							} else {
								handler.sendEmptyMessage(MSG_HIDE_DIALOG);
								Toast.makeText(getApplicationContext(),
										"无外置sd卡，无法缓存，请修改存储路径为手机存储",
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					} else {
						if (internalExternalPathInfo.emulatedSDcard != null) {
							path = internalExternalPathInfo.emulatedSDcard;
						} else {
							if (internalExternalPathInfo.removableSDcard != null) {
								PreferencesUtils.putInt(this, null,
										"cache_path_type", 0);
								path = internalExternalPathInfo.removableSDcard;
							} else {
								handler.sendEmptyMessage(MSG_HIDE_DIALOG);
								Toast.makeText(getApplicationContext(),
										"无内置存储空间，无法缓存，请修改存储路径为SD卡",
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}

					downloadInfo.sdcardDir = path;
					if (programData.name != null
							&& !programData.name.equals(jishuData.name)) {
						downloadInfo.programName = programData.name + "\n"
								+ jishuData.name;
					} else {
						downloadInfo.programName = programData.name == null ? "电视粉节目"
								: programData.name;
					}
					downloadInfo.initUrl = getInitUrl(jishuData);
					downloadInfo.state = DownloadInfoState.WAITTING;
					accessDownload.save(downloadInfo);
				}
				ArrayList<DownloadInfo> downloadingInfos = accessDownload
						.queryDownloadInfo(DownloadInfoState.DOWNLOADING);
				if (downloadingInfos == null || downloadingInfos.size() == 0) {
					ArrayList<DownloadInfo> waittings = accessDownload
							.queryDownloadInfo(DownloadInfoState.WAITTING);
					if (waittings != null && waittings.size() > 0) {
						DownloadInfo downloadInfo = waittings.get(0);
						downloadInfo.state = DownloadInfoState.DOWNLOADING;
						accessDownload.updateDownloadState(downloadInfo);
					}
					startCacheService();
				}

				handler.sendEmptyMessage(MSG_HIDE_DIALOG);
				setResult(RESULT_OK);
				Toast.makeText(getApplicationContext(), "请到缓存中心查看进度",
						Toast.LENGTH_SHORT).show();
			} else {
				setResult(RESULT_CANCELED);
			}
			finish();

		} catch (NullPointerException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(MSG_HIDE_DIALOG);
			Toast.makeText(getApplicationContext(), "选集出错", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("programCache");
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("programCache");
		super.onPause();
	}

	private void instantsJujiFragment() {
		if (programData.programId == 0) {
			finish();
			return;
		}
		switch (programData.pType) {
		case ProgramData.TYPE_TV:
		case ProgramData.TYPE_DONGMAN:
			tvFragment = CacheTvFragment.newInstance(programData.programId);
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, tvFragment)
					.commitAllowingStateLoss();

			break;
		default:
			jujiListFragment = CacheJujiListFragment
					.newInstance(programData.programId);
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, jujiListFragment)
					.commitAllowingStateLoss();
			break;
		}
	}

	private void startCacheService() {
		// Intent intent = new Intent(this, DownloadService.class);
		Intent intent = new Intent(this, DownloadService.class);

		intent.putExtra(DownloadService.APPNAME_KEY,
				getString(R.string.app_name));
		intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
		startService(intent);
	}

	public String getInitUrl(JiShuData jishuData) {
		if (TextUtils.isEmpty(jishuData.videoPath)) {
			return jishuData.url + "-webparse";
		}
		return jishuData.videoPath;
	}

	public ArrayList<JiShuData> getCacheInfoData() {
		ArrayList<JiShuData> jishuDatas = sourcePlat.jishuList;
		if (jishuDatas != null) {
			return filterPendingData(jishuDatas);
		}
		return null;
	}

	private ArrayList<JiShuData> filterPendingData(
			ArrayList<JiShuData> jishuDatas) {
		ArrayList<JiShuData> list = new ArrayList<JiShuData>();
		for (JiShuData temp : jishuDatas) {
			if (temp.cacheInfo != null && temp.cacheInfo.state == 6) {
				list.add(temp);
			}
		}
		return list;
	}

	// 任务处理对话框
	private ProgressDialog dialog;

	private void showDialog() {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
		dialog = new ProgressDialog(this);
		dialog.setMessage("缓存处理中...");
		// dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
	}

	private final int MSG_SHOW_DIALOG = 1;
	private final int MSG_HIDE_DIALOG = 2;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_DIALOG:
				showDialog();
				break;
			case MSG_HIDE_DIALOG:
				dismissDialog();
				break;
			default:
				break;
			}

		};
	};
}
