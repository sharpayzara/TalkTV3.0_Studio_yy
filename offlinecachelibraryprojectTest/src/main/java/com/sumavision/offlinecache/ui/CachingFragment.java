package com.sumavision.offlinecache.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sumavision.offlinecachelibrary.R;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.VideoFormat;
import com.sumavision.offlinelibrary.util.CommonUtils;

public class CachingFragment extends CacheBaseFragment implements
		OnItemClickListener {
	public static final String TAG = "CachingFragment";
	String appName, appEnName;

	public static CachingFragment newInstance(String appName, String appEnName) {
		CachingFragment fragment = new CachingFragment();
		Bundle bundle = new Bundle();
		bundle.putString("appName", appName);
		bundle.putString("appEnName", appEnName);
		fragment.setArguments(bundle);
		return fragment;
	}

	long lastBytes = 0;
	long currBytes;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			lastBytes = currBytes;
			currBytes = CommonUtils.getNetSpeed(getActivity());
			mHandler.sendEmptyMessage(7);
			mHandler.postDelayed(this, 1000);
		}
	};

	Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 7) {
				if (cacheAdapter != null) {
					cacheAdapter.notifyDataSetChanged();
				}
			}

			super.handleMessage(msg);
		}
	};

	public void refreshReceiver() {
		if (cachingDownloadReceiver != null && getActivity() != null) {
			getActivity().unregisterReceiver(cachingDownloadReceiver);
			registerReceiver();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		currBytes = CommonUtils.getNetSpeed(getActivity());
		mHandler.postDelayed(runnable, 1000);
		appName = getArguments().getString("appName");
		appEnName = getArguments().getString("appEnName");
		super.onCreate(savedInstanceState);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_ERROR);
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_REFRESH);
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_PAUSE);
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_ERROR_RETURY);
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_PARSE_ERROR);
		getActivity().registerReceiver(cachingDownloadReceiver, filter);
	}

	@Override
	protected void initViews(View view) {
		listView.setOnItemClickListener(this);
		registerReceiver();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshData();
		if (downloadInfos != null && downloadInfos.size() > 0) {
			startRemainTask();
		}
	}

	private void startRemainTask() {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> temps = am.getRunningServices(30);
		int size = temps.size();
		if (size > 0) {
			boolean onGoing = false;
			for (int i = 0; i < size; i++) {
				if (temps.get(i).service.getClassName().equals(
						DownloadService.class.getName())
						&& temps.get(i).service.getPackageName().equals(
								getActivity().getPackageName())) {
					onGoing = true;
					break;
				}
			}
			if (!onGoing) {

				Intent intent = new Intent(getActivity(), DownloadService.class);
				intent.putExtra(DownloadService.APPNAME_KEY,
						getString(R.string.app_name));
				intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
				getActivity().startService(intent);

			}
		}
	}

	private CachingAdapter cacheAdapter;

	public void refreshData() {
		if (editState || !initView) {
			return;
		}
		// // 保存编辑状态
		// if (editState && downloadInfosWaitDelete.size() > 0) {
		// Log.d(TAG, "updatePendingState");
		// AccessDownload.getInstance(getActivity()).updatePendingState(
		// downloadInfos);
		// }
		getData();
	}

	private void getData() {
		downloadInfos.clear();
		ArrayList<DownloadInfo> allDownloadingInfos = AccessDownload
				.getInstance(mActivity).queryDownloadInfo();
		downloadInfos.addAll(allDownloadingInfos);

		if (downloadInfos != null && downloadInfos.size() > 0) {
			tips.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			if (cacheAdapter == null) {
				cacheAdapter = new CachingAdapter(downloadInfos);
				listView.setAdapter(cacheAdapter);
			} else {
				cacheAdapter.notifyDataSetChanged();
			}
		} else {
			tips.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}

	}

	public void selectAllItem() {
		operateAllItem(true);
	}

	public void disSelectAllItem() {
		operateAllItem(false);
	}

	private void operateAllItem(boolean select) {
		if (downloadInfos != null && downloadInfos.size() > 0) {
			for (int i = 0; i < downloadInfos.size(); i++) {
				downloadInfos.get(i).pendingState = select ? 1 : 0;
			}
			downloadInfosWaitDelete.clear();
			cacheAdapter.notifyDataSetChanged();
			if (select) {
				downloadInfosWaitDelete.addAll(downloadInfos);

			}
		}
	}

	public void deleteSelectedItem() {
		if (downloadInfosWaitDelete.size() == 0) {
			return;
		}
		if (downloadInfosWaitDelete.size() == downloadInfos.size()) {
			downloadInfos.clear();
		} else {
			downloadInfos.removeAll(downloadInfosWaitDelete);
			// if (hasDownloadingInDetele) {
			// addDownloading = changeWatingToDownloadingStatus();
			// }
		}
		cacheAdapter.notifyDataSetChanged();
		// 删除数据库
		new Thread(deleteRunnable).start();

	}

	private Runnable deleteRunnable = new Runnable() {

		@Override
		public void run() {
			if (downloadInfosWaitDelete.size() == 0) {
				return;
			}
			ArrayList<DownloadInfo> deleteSegTableList = new ArrayList<DownloadInfo>();
			int currentDownloadIndex = -1;
			for (int j = 0; j < downloadInfosWaitDelete.size(); j++) {
				DownloadInfo downloadInfo = downloadInfosWaitDelete.get(j);
				if (downloadInfo.state == DownloadInfoState.DOWNLOADING) {
					currentDownloadIndex = j;
					int action = DownloadService.ACTION_DELETE_DOWNLOADING;
					startDownloadService(downloadInfo, action);
				} else {
					deleteSegTableList.add(downloadInfo);
				}

			}
			if (currentDownloadIndex != -1)
				downloadInfosWaitDelete.remove(currentDownloadIndex);
			if (deleteSegTableList.size() > 0) {
				AccessSegInfo.getInstance(getActivity())
						.deleteByProgramIdAndSubId(deleteSegTableList);
			}
			AccessDownload.getInstance(getActivity())
					.deleteFromSegsByProgramIdAndSubId(downloadInfosWaitDelete);
			AccessDownload.getInstance(mActivity).deleteProgramSub(
					downloadInfosWaitDelete);

			for (DownloadInfo item : downloadInfosWaitDelete) {
				String dir = DownloadUtils.getFileDir(item);
				CommonUtils.deleteFile(new File(dir));
			}
			downloadInfosWaitDelete.clear();
		}
	};

	private long lastClicktime = 0;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		final DownloadInfo downloadInfo = downloadInfos.get((int) id);
		boolean startServiceFlag = false;
		int action = 0;
		if (editState) {
			CacheActivity ca = (CacheActivity) getActivity();
			if (downloadInfo.pendingState == 0) {
				downloadInfo.pendingState = 1;
				downloadInfosWaitDelete.add(downloadInfo);
				if (downloadInfos.size() == downloadInfosWaitDelete.size()
						&& !ca.selectedAll) {
					ca.setSelectText(true);
				}
			} else {
				downloadInfo.pendingState = 0;
				downloadInfosWaitDelete.remove(downloadInfo);
				if (ca.selectedAll) {
					ca.setSelectText(false);
				}
			}
		} else {
			if (downloadInfo.state == DownloadInfoState.DOWNLOADING) {
				if ((System.currentTimeMillis() - lastClicktime) < 1000) {
					return;
				}
				downloadInfo.state = DownloadInfoState.PAUSE;
				AccessDownload.getInstance(mActivity).updateDownloadState(
						downloadInfo);
				// changeWatingToDownloadingStatus();
				action = DownloadService.ACTION_PAUSE;
				startServiceFlag = true;
			} else if (downloadInfo.state == DownloadInfoState.WAITTING) {
				downloadInfo.state = DownloadInfoState.PAUSE;
				AccessDownload.getInstance(mActivity).updateDownloadState(
						downloadInfo);
			} else {
				if (AccessDownload.getInstance(mActivity)
						.isDownloadingExecute()) {
					downloadInfo.state = DownloadInfoState.WAITTING;
				} else {
					if ((System.currentTimeMillis() - lastClicktime) < 1000) {
						return;
					}
					downloadInfo.state = DownloadInfoState.DOWNLOADING;
					action = DownloadService.ACTION_DOWNLOAD_NEW_TASK;
					startServiceFlag = true;
				}
				AccessDownload.getInstance(mActivity).updateDownloadState(
						downloadInfo);
			}

			if (startServiceFlag) {
				startDownloadService(downloadInfo, action);
			}

		}
		lastClicktime = System.currentTimeMillis();
		cacheAdapter.notifyDataSetChanged();
	}

	private void startDownloadService(DownloadInfo downloadInfo, int action) {
		Intent intent = new Intent(mActivity, DownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(DownloadService.ACTION_KEY, action);
		// bundle.putInt(DownloadService.ACTION_KEY, addDownloading);
		intent.putExtra("bundle", bundle);
		intent.putExtra(DownloadService.APPNAME_KEY, appName);
		intent.putExtra(DownloadService.APP_EN_NAME_KEY, appEnName);
		intent.putExtra(DownloadManager.extra_loadinfo, downloadInfo);
		mActivity.startService(intent);
	}

	public boolean editState = false;

	public void setState() {
		editState = !editState;
		if (cacheAdapter != null) {
			cacheAdapter.notifyDataSetChanged();
		}
	}

	public boolean getEditState() {
		return editState;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(cachingDownloadReceiver);
		if (mHandler != null && runnable != null) {
			mHandler.removeCallbacks(runnable);
		}
	}

	public class CachingAdapter extends ArrayAdapter<DownloadInfo> {
		public CachingAdapter(CopyOnWriteArrayList<DownloadInfo> objects) {
			super(getActivity(), 0, objects);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(mActivity);
				convertView = inflater.inflate(R.layout.offline_caching_item,
						null);
				viewHolder.nameView = (TextView) convertView
						.findViewById(R.id.name);
				viewHolder.progressView = (TextView) convertView
						.findViewById(R.id.progress_txt);
				viewHolder.leftView = (ImageView) convertView
						.findViewById(R.id.cache_center_caching_select_status_img1);
				viewHolder.rightView = (ImageView) convertView
						.findViewById(R.id.cache_center_caching_download_status_img1);
				viewHolder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.progress);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final DownloadInfo info = downloadInfos.get(position);
			if (info.pendingState == 1) {
				Log.d(TAG, "cachingAdapter-->>" + info.programId
						+ info.subProgramId + "pendingState:"
						+ info.pendingState);
			}

			String name = info.programName;
			if (!TextUtils.isEmpty(name)) {
				viewHolder.nameView.setText(name);
			}

			int progress = info.progress;
			String stringProgress = String.valueOf(progress);
			viewHolder.progressBar.setProgress(progress);
			if (info.state == DownloadInfoState.DOWNLOADING) {
				long curr = currBytes - lastBytes;
				if (lastBytes == 0) {
					curr = 0;
				}
				viewHolder.progressView.setText(stringProgress + "%("
						+ (curr / 1024) + "k/s)");
				viewHolder.rightView
						.setImageResource(R.drawable.cache_status_begin);
			} else if (info.state == DownloadInfoState.WAITTING
					|| info.state == DownloadInfoState.DOWNLOADING_FOR_NETWORK) {
				if (!TextUtils.isEmpty(stringProgress)) {
					viewHolder.progressView.setText(stringProgress + "%");
				}
				viewHolder.rightView
						.setImageResource(R.drawable.cache_status_wait);
			} else if (info.state == DownloadInfoState.PAUSE) {
				if (!TextUtils.isEmpty(stringProgress)) {
					viewHolder.progressView.setText(stringProgress + "%");
				}
				viewHolder.rightView
						.setImageResource(R.drawable.cache_status_stop);
			} else if (info.state == DownloadInfoState.ERROR) {
				viewHolder.rightView
						.setImageResource(R.drawable.cache_status_error);
				viewHolder.progressView.setText("下载失败");
			} else if (info.state == DownloadInfoState.PARSE_ERROR) {
				viewHolder.rightView
						.setImageResource(R.drawable.cache_status_error);
				viewHolder.progressView.setText("解析失败");
			}

			viewHolder.leftView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (info.pendingState) {
					case 0:
						viewHolder.leftView
								.setImageResource(R.drawable.cache_center_cached_subitem_list_select);
						info.pendingState = 1;
						downloadInfosWaitDelete.add(info);
						break;
					case 1:
						viewHolder.leftView
								.setImageResource(R.drawable.cache_center_cached_subitem_list_unselect);
						info.pendingState = 0;
						downloadInfosWaitDelete.remove(info);
						break;

					default:
						break;
					}
				}
			});

			if (editState) {
				viewHolder.leftView.setVisibility(View.VISIBLE);
				viewHolder.rightView.setVisibility(View.GONE);
				if (info.pendingState == 1) {
					viewHolder.leftView
							.setImageResource(R.drawable.cache_center_cached_subitem_list_select);

				} else {
					viewHolder.leftView
							.setImageResource(R.drawable.cache_center_cached_subitem_list_unselect);

				}
			} else {
				viewHolder.leftView.setVisibility(View.GONE);
				viewHolder.rightView.setVisibility(View.VISIBLE);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		public TextView nameView;
		public TextView progressView;
		public ImageView leftView;
		public ImageView rightView;
		public ProgressBar progressBar;

	}

	private BroadcastReceiver cachingDownloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (editState) {
				DownloadInfo info = (DownloadInfo) intent
						.getSerializableExtra(DownloadManager.extra_loadinfo);
				if (info != null) {
					switch (info.state) {
					case DownloadInfoState.DOWNLOADED:
						// 删除已完成的任务
						for (DownloadInfo item : downloadInfos) {
							if (item.programId == info.programId
									&& item.subProgramId == info.subProgramId) {
								downloadInfos.remove(item);
								cacheAdapter.notifyDataSetChanged();
								break;
							}
						}
						break;
					case DownloadInfoState.DOWNLOADING:
					case DownloadInfoState.ERROR:
						changeInfoState(info);
						break;

					default:
						break;
					}
				}
			} else {
				if (intent.getAction().equals(
						DownloadService.NOTIFICATION_ID + "_"
								+ DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
					Log.i(TAG, "receive broadcast "
							+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
				}
				refreshData();
			}

		}

		private void changeInfoState(DownloadInfo info) {
			for (DownloadInfo item : downloadInfos) {
				if (item.programId == info.programId
						&& item.subProgramId == info.subProgramId) {
					item.state = info.state;
					cacheAdapter.notifyDataSetChanged();
				}
			}
		}
	};
}
