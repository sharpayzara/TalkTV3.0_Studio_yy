package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramCacheActivity;
import com.sumavision.talktv2.adapter.CacheJujiAdapter;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.PlatVideoParser;
import com.sumavision.talktv2.http.json.PlatVideoRequest;

public class CacheTvFragment extends BaseFragment implements
		OnItemClickListener, OnRefreshListener2<ScrollView> {
	private ProgramData programData;
	private int countGrid = 120;

	public static CacheTvFragment newInstance(long programId) {
		CacheTvFragment fragment = new CacheTvFragment();
		Bundle bundle = new Bundle();
		bundle.putLong("programId", programId);
		bundle.putInt("resId", R.layout.fragment_cache_tv);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long programId = getArguments() != null ? getArguments().getLong(
				"programId") : 0L;
		if (programData == null) {
			programData = new ProgramData();
			programData.programId = programId;
		}
		sourcePlat = ((ProgramCacheActivity) getActivity()).sourcePlat;
	}

	SourcePlatform sourcePlat;
	private PullToRefreshScrollView scrollView;
	private StaticGridView jiShuGridView;

	protected void initViews(View view) {
		scrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.scrollView);
		jiShuGridView = (StaticGridView) view.findViewById(R.id.jishu);
		jiShuGridView.setOnItemClickListener(this);
		scrollView.setMode(Mode.PULL_FROM_END);
		scrollView.setOnRefreshListener(this);
		updateJiShuLayout();
	}

	ArrayList<JiShuData> jiShuDatas = new ArrayList<JiShuData>();

	private void updateJiShuLayout() {
		if (sourcePlat.jishuList != null && sourcePlat.jishuList.size() > 0) {
			jiShuDatas = sourcePlat.jishuList;
			filterCacheInfo(jiShuDatas);
			if (jishuAdapter == null) {
				jishuAdapter = new CacheJujiAdapter(mActivity,
						ProgramData.TYPE_TV, jiShuDatas);
				jiShuGridView.setAdapter(jishuAdapter);
				if (jiShuDatas.size() < countGrid) {
					scrollView.setMode(Mode.DISABLED);
				}
			} else {
				jishuAdapter.notifyDataSetChanged();
			}
			rootView.findViewById(R.id.tip).setVisibility(View.VISIBLE);
		}

	}

	private AccessDownload accessDownload;

	private void filterCacheInfo(ArrayList<JiShuData> jiShuDatas) {
		accessDownload = AccessDownload.getInstance(mActivity);
		ArrayList<DownloadInfo> downloadedInfos = accessDownload
				.queryDownloadInfo(2);
		ArrayList<DownloadInfo> downloadIngInfos = accessDownload
				.queryDownloadInfo(0);
		ArrayList<DownloadInfo> downloadIngInfos2 = accessDownload
				.queryDownloadInfo(1);
		ArrayList<DownloadInfo> downloadIngInfos3 = accessDownload
				.queryDownloadInfo(3);
		downloadIngInfos.addAll(downloadIngInfos2);
		downloadIngInfos.addAll(downloadIngInfos3);
		for (JiShuData jishuData : jiShuDatas) {
			boolean isSet = false;
			for (DownloadInfo downloadedInfo : downloadedInfos) {
				if (programData.programId == downloadedInfo.programId
						&& jishuData.id == downloadedInfo.subProgramId) {
					CacheInfo info = new CacheInfo();
					info.state = 2;
					jishuData.cacheInfo = info;
					isSet = true;
					break;
				}
			}
			for (DownloadInfo downloadingInfo : downloadIngInfos) {
				if (isSet) {
					break;
				}
				if (programData.programId == downloadingInfo.programId
						&& jishuData.id == downloadingInfo.subProgramId) {
					CacheInfo info = new CacheInfo();
					info.state = 0;
					jishuData.cacheInfo = info;
					isSet = true;
					break;
				}
			}
			if (!isSet) {
				CacheInfo info = new CacheInfo();
				info.state = 5;
				jishuData.cacheInfo = info;
			}

		}
	}

	CacheJujiAdapter jishuAdapter;
	int currentEpisodePosition;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		currentEpisodePosition = arg2;
		jishuAdapter.notifyDataSetChanged();
		JiShuData jiShuData = jiShuDatas.get(arg2);
		CacheInfo info = jiShuData.cacheInfo;
		if (info.state == 5) {
			info.state = 6;
			jishuAdapter.notifyDataSetChanged();
		} else if (info.state == 6) {
			info.state = 5;
			jishuAdapter.notifyDataSetChanged();
		}
	}

	PlatVideoParser platVideoParser = new PlatVideoParser();

	public void getPlatVideoList(int first, int count) {
		VolleyHelper.post(new PlatVideoRequest(sourcePlat.id,
				programData.programId, 0, first, count).make(), new ParseListener(
				platVideoParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				scrollView.onRefreshComplete();
				hideLoadingLayout();
				if (platVideoParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					sourcePlat.jishuList.addAll(platVideoParser.subList);
					updateJiShuLayout();
					if (platVideoParser.subList.size() < countGrid) {
						scrollView.setMode(Mode.DISABLED);
					}
				} else {
					Log.e("getPlatVideoList", "加载失败");
				}

			}
		}, this);
	}

	@Override
	public void reloadData() {

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		getPlatVideoList(jiShuDatas.size(), countGrid);
	}

}
