package com.sumavision.offlinecache.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.offlinecachelibrary.R;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.offlinelibrary.util.ImageLoaderHelper;
import com.umeng.socialize.utils.Log;

public class CachedFragment extends CacheBaseFragment implements
		OnItemClickListener {

	protected static final String TAG = "CachedFragment";

	public static CachedFragment newInstance(String className) {
		CachedFragment fragment = new CachedFragment();
		Bundle bundle = new Bundle();
		bundle.putString("clsName", className);
		fragment.setArguments(bundle);
		return fragment;
	}

	private ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String playerClsName = getArguments() != null ? getArguments()
				.getString("clsName") : null;
		try {
			playerCls = Class.forName(playerClsName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void refreshReceiver() {
		if (downloadReceiver != null && getActivity() != null) {
			getActivity().unregisterReceiver(downloadReceiver);
			IntentFilter filter = new IntentFilter();
			filter.addAction(DownloadService.NOTIFICATION_ID + "_"
					+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			getActivity().registerReceiver(downloadReceiver, filter);
		}
	}

	@Override
	protected void initViews(View view) {
		listView.setOnItemClickListener(this);
		((ImageView) view
				.findViewById(R.id.offline_cache_center_cached_tips_img))
				.setImageResource(R.drawable.cache_center_cached_tips);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.NOTIFICATION_ID + "_"
				+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		getActivity().registerReceiver(downloadReceiver, filter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshData();
	}

	private CachedAdapter cacheAdapter;

	public void refreshData() {
		if (editState || !initView) {
			return;
		}
		getData();
	}

	private void getData() {
		downloadInfos.clear();
		ArrayList<DownloadInfo> list = AccessDownload.getInstance(mActivity)
				.queryDownloadInfo(DownloadInfoState.DOWNLOADED);
		downloadInfos.addAll(list);

		if (downloadInfos != null && downloadInfos.size() > 0) {
			tips.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		} else {
			tips.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			// editState = false;
		}
		cacheAdapter = new CachedAdapter(downloadInfos);
		listView.setAdapter(cacheAdapter);
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
			cacheAdapter.notifyDataSetChanged();
			downloadInfosWaitDelete.clear();
			if (select) {
				downloadInfosWaitDelete.addAll(downloadInfos);
			}
		}
	}

	public void deleteSelectedItem() {
		if (downloadInfosWaitDelete.size() == 0) {
			return;
		}

		if (downloadInfos.size() == downloadInfosWaitDelete.size()) {
			downloadInfos.clear();
		} else {
			downloadInfos.removeAll(downloadInfosWaitDelete);

		}
		new Thread(deleteRunnable).start();
		cacheAdapter.notifyDataSetChanged();

	}

	private Runnable deleteRunnable = new Runnable() {

		@Override
		public void run() {
			AccessDownload.getInstance(mActivity).deleteProgramSub(
					downloadInfosWaitDelete);
			for (DownloadInfo item : downloadInfosWaitDelete) {
				String dir = DownloadUtils.getFileDir(item);
				CommonUtils.deleteFile(new File(dir));
			}
			downloadInfosWaitDelete.clear();
		}
	};

	public void onComplete(DownloadInfo downloadInfo) {
		refreshData();
	}

	private Class<?> playerCls;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DownloadInfo downloadInfo = downloadInfos.get(position);
		if (!editState) {
			if (downloadInfo != null) {
				File file = new File(downloadInfo.fileLocation);
				if (file.exists()) {
					openPlayerActivity(downloadInfo, playerCls);
				} else {
					Toast.makeText(getActivity(), "文件不存在或已被意外删除",
							Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			CacheActivity ca = (CacheActivity) getActivity();
			if (downloadInfo.pendingState == 1) {
				downloadInfo.pendingState = 0;
				downloadInfosWaitDelete.remove(downloadInfo);
				if (ca.selectedAll) {
					ca.setSelectText(false);
				}
			} else {
				downloadInfo.pendingState = 1;
				downloadInfosWaitDelete.add(downloadInfo);
				if (downloadInfosWaitDelete.size() == downloadInfos.size()
						&& !ca.selectedAll) {
					ca.setSelectText(true);
				}
			}
			cacheAdapter.notifyDataSetChanged();

		}
	}

	private void openPlayerActivity(DownloadInfo downloadInfo, Class<?> cls) {
		if (downloadInfo.initUrl.endsWith("-webparse")) {
			downloadInfo.initUrl = downloadInfo.initUrl.substring(0,
					downloadInfo.initUrl.indexOf("-webparse"));
		}
		Intent intent = new Intent(mActivity, cls);
		intent.putExtra("path", downloadInfo.fileLocation);
		intent.putExtra("historyurl", downloadInfo.initUrl);
		intent.putExtra("url", downloadInfo.initUrl);
		intent.putExtra("playType", 3);
		intent.putExtra("title", downloadInfo.programName);
		intent.putExtra("id", downloadInfo.programId);
		intent.putExtra("detailid", downloadInfo.subProgramId);
		intent.putExtra("subid", downloadInfo.subProgramId);
		intent.putExtra("nameHolder", downloadInfo.programName);
		intent.putExtra("needVParse", false);
		startActivity(intent);
	}

	public boolean editState = false;

	public void setState() {
		editState = !editState;
		if (cacheAdapter != null) {
			cacheAdapter.notifyDataSetChanged();
		}
	}

	public class CachedAdapter extends ArrayAdapter<DownloadInfo> {
		public CachedAdapter(CopyOnWriteArrayList<DownloadInfo> objects) {
			super(getActivity(), 0, objects);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(mActivity);
				convertView = inflater.inflate(
						R.layout.cache_center_cached_grid_item, null);
				viewHolder.selectPic = (ImageView) convertView
						.findViewById(R.id.cache_center_caching_select_status_img);
				viewHolder.nameView = (TextView) convertView
						.findViewById(R.id.textView);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final DownloadInfo info = downloadInfos.get(position);

			String name = info.programName;
			if (!TextUtils.isEmpty(name)) {
				try {
					SpannableString msp = new SpannableString(name);
					msp.setSpan(new AbsoluteSizeSpan(17, true), 0,
							name.lastIndexOf("\n"),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

					viewHolder.nameView.setText(msp);
				} catch (Exception e) {
					viewHolder.nameView.setText(name);
				}
			}

			if (!TextUtils.isEmpty(info.programPic))
				imageLoaderHelper.loadImage(viewHolder.imageView,
						info.programPic, R.drawable.cache_default);

			viewHolder.selectPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (info.pendingState) {
					case 0:
						viewHolder.selectPic
								.setImageResource(R.drawable.cache_center_cached_subitem_list_select);
						info.pendingState = 1;
						downloadInfosWaitDelete.add(info);
						break;
					case 1:
						viewHolder.selectPic
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
				viewHolder.selectPic.setVisibility(View.VISIBLE);

				if (info.pendingState == 1) {
					viewHolder.selectPic
							.setImageResource(R.drawable.cache_center_cached_subitem_list_select);
				} else {
					viewHolder.selectPic
							.setImageResource(R.drawable.cache_center_cached_subitem_list_unselect);
				}

			} else {
				viewHolder.selectPic.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		public TextView nameView;
		public ImageView imageView;
		public ImageView selectPic;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(downloadReceiver);
	}

	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "receive broadcast"
					+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			refreshData();
		}
	};
}
