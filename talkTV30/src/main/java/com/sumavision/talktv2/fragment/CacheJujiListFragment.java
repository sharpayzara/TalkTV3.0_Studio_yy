package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramCacheActivity;
import com.sumavision.talktv2.adapter.CacheJujiAdapter;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.PlatVideoParser;
import com.sumavision.talktv2.http.json.PlatVideoRequest;
import com.sumavision.talktv2.utils.Constants;

public class CacheJujiListFragment extends BaseFragment implements
		OnItemClickListener, OnRefreshListener<ListView> {
	private ProgramData programData;

	public static CacheJujiListFragment newInstance(long programId) {
		CacheJujiListFragment fragment = new CacheJujiListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_cache_movie);
		bundle.putLong("programId", programId);
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
	private RelativeLayout jiShuLayout;
	private PullToRefreshListView listView;
	private ListView jishuListView;

	protected void initViews(View view) {
		jiShuLayout = (RelativeLayout) view.findViewById(R.id.jishu_layout);
		listView = (PullToRefreshListView) view.findViewById(R.id.jishu);
		listView.setMode(Mode.PULL_FROM_END);
		jishuListView = listView.getRefreshableView();
		listView.setOnRefreshListener(this);
		listView.setPullToRefreshOverScrollEnabled(false);
		updateJiShuLayout();
	}

	ArrayList<JiShuData> jiShuDatas = new ArrayList<JiShuData>();
	private void updateJiShuLayout() {
		View header = inflater.inflate(R.layout.program_cache_header, null);
		if (sourcePlat.jishuList != null && sourcePlat.jishuList.size() > 0) {
			jiShuLayout.setVisibility(View.VISIBLE);
			jiShuDatas.addAll(sourcePlat.jishuList);
			filterCacheInfo(jiShuDatas);
			if (jiShuDatas.size() == 0) {
				jishuListView.setEmptyView(header);
			} else {
				jishuListView.addHeaderView(header, null, false);
			}
			jishuAdapter = new CacheJujiAdapter(mActivity,
					ProgramData.TYPE_MOVIE, jiShuDatas);
			jishuListView.setAdapter(jishuAdapter);
			jishuListView.setOnItemClickListener(this);

		} else {
			listView.setMode(Mode.DISABLED);
		}
	}


	private AccessDownload accessDownload;

	private void filterCacheInfo(ArrayList<JiShuData> jiShuDatas) {
		accessDownload =  AccessDownload
				.getInstance(mActivity);
		ArrayList<DownloadInfo> downloadedInfos = accessDownload
				.queryDownloadInfo(DownloadInfoState.DOWNLOADED);
		ArrayList<DownloadInfo> downloadIngInfos = accessDownload
				.queryDownloadInfo(DownloadInfoState.WAITTING);
		ArrayList<DownloadInfo> downloadIngInfos2 = accessDownload
				.queryDownloadInfo(DownloadInfoState.DOWNLOADING);
		ArrayList<DownloadInfo> downloadIngInfos3 = accessDownload
				.queryDownloadInfo(DownloadInfoState.PAUSE);
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
		if (arg1.isEnabled() && arg3 >= 0) {
			currentEpisodePosition = (int) arg3;
			jishuAdapter.notifyDataSetChanged();
			JiShuData jiShuData = jiShuDatas.get(currentEpisodePosition);
			CacheInfo info = jiShuData.cacheInfo;
			if (info.state == 5) {
				info.state = 6;
				jishuAdapter.notifyDataSetChanged();
			} else if (info.state == 6) {
				info.state = 5;
				jishuAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void reloadData() {

	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		getPlatVideoList(sourcePlat.jishuList.size(), 20);
	}

	PlatVideoParser platVideoParser = new PlatVideoParser();

	public void getPlatVideoList(int first, int count) {
		VolleyHelper.post(new PlatVideoRequest(sourcePlat.id,
				programData.programId, 0, first, count).make(), new ParseListener(
				platVideoParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() == null || getActivity().isFinishing()) {
					return;
				}
				listView.onRefreshComplete();
				hideLoadingLayout();
				if (platVideoParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					if (platVideoParser.subList.size() < 20) {
						listView.setMode(Mode.DISABLED);
					}
					updateJishu(platVideoParser.subList);
				} else {
					listView.setMode(Mode.DISABLED);
					Log.e("getPlatVideoList", "加载失败");
				}

			}
		}, this);
	}

	public void updateJishu(ArrayList<JiShuData> jishuList) {
		sourcePlat.jishuList.addAll(jishuList);
		jiShuDatas.addAll(jishuList);
		filterCacheInfo(jiShuDatas);
		jishuAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.platFormProgramSubList);
	}
}
