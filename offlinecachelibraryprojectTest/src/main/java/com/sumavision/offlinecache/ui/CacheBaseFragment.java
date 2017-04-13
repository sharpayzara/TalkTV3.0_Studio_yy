package com.sumavision.offlinecache.ui;

import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sumavision.offlinecachelibrary.R;
import com.sumavision.offlinelibrary.entity.DownloadInfo;

public abstract class CacheBaseFragment extends Fragment {
	protected Activity mActivity;

	protected boolean initView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	public void onDetach() {
		super.onDetach();
		mActivity = null;
	};

	protected abstract void initViews(View view);

	protected View rootView;
	protected boolean needLoadData;
	protected ListView listView;
	protected RelativeLayout tips;

	protected CopyOnWriteArrayList<DownloadInfo> downloadInfos = new CopyOnWriteArrayList<DownloadInfo>();
	public CopyOnWriteArrayList<DownloadInfo> downloadInfosWaitDelete = new CopyOnWriteArrayList<DownloadInfo>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.offline_caching_content, null);
			listView = (ListView) rootView.findViewById(R.id.offline_listView);
			tips = (RelativeLayout) rootView
					.findViewById(R.id.offline_no_content_tips);
			initViews(rootView);
			needLoadData = true;
			initView = true;
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		return rootView;
	}
}
