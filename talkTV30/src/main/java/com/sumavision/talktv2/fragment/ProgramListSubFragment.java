package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.JujiAdapter;
import com.sumavision.talktv2.annotation.ViewInject;
import com.sumavision.talktv2.annotation.ViewUtils;
import com.sumavision.talktv2.annotation.event.OnItemClick;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;

/**
 * 节目list列表剧集页
 * 
 * @author Administrator
 * @description
 */
public class ProgramListSubFragment extends ProgramBaseFragment implements
		OnItemClickListener, OnRefreshListener2<ListView> {

//	@ViewInject(R.id.game_list)
	private PullToRefreshListView jishuListView;
	private JujiAdapter jishuAdapter;
	public int subid;

	public static ProgramListSubFragment newInstance() {
		ProgramListSubFragment fragment = new ProgramListSubFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_program_juji);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubIsList(true);
		programData = ((ProgramDetailHalfActivity) getParentFragment()).programData;
	}

	@Override
	protected void initViews(View view) {
		ViewUtils.inject(this, view);
		jishuListView.setOnRefreshListener(this);
		jishuListView.setMode(Mode.PULL_FROM_END);
		jishuListView.setPullToRefreshOverScrollEnabled(false);
//		initHeaderView();
		ushowSwitchRequest();
//		updateProgramHeader();
//		onProgramHeader();
//		updateSignLayout();
		if (programData != null && programData.platformList != null
				&& programData.platformList.size() > 0) {
			ArrayList<JiShuData> list = new ArrayList<JiShuData>(
					programData.platformList.get(0).jishuList);
			this.jiShuDatas.addAll(list);
			if (programData.platformList.size() == 1) {
				singleSourceLayout.setClickable(false);
				sourceDownView.setVisibility(View.GONE);
			}
			loadImage(sourceImg, programData.platformList.get(0).pic,
					R.drawable.play_source_default);
		}
		updateJiShuLayout();
	}

	ArrayList<JiShuData> jiShuDatas = new ArrayList<JiShuData>();

	protected void updateJiShuLayout() {
		jishuListView.onRefreshComplete();
		if (jiShuDatas != null && jiShuDatas.size() > 0) {
			filterCacheInfo(jiShuDatas);
		} else {
			jishuListView.setMode(Mode.DISABLED);
		}
		if (jishuAdapter == null && jiShuDatas != null && jiShuDatas.size() != 0) {
			jishuAdapter = new JujiAdapter(getActivity(),
					JujiAdapter.TYPE_MOVIE, jiShuDatas);
			jishuListView.setAdapter(jishuAdapter);
		} else if (jishuAdapter != null) {
			jishuAdapter.notifyDataSetChanged();
		}
		if (jiShuDatas.size() < countList) {
			jishuListView.setMode(Mode.DISABLED);
		}

	}

	@Override
//	@OnItemClick(R.id.game_list)
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int pos = (int) id;
		jishuAdapter.setSelectedPosition(pos);
		jishuAdapter.notifyDataSetChanged();
		subid = jiShuDatas.get(pos).id;
		setSelectedPlayItemPos(pos);
		DownloadInfo downloadInfo = isCacheVideo(subid);
//		play(jiShuDatas, pos);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getPlatVideoList(currentPlatPos, jiShuDatas.size(), countList);

	}

	@Override
	public void onProgramHeader() {
		if (getActivity() != null) {
//			ProgramActivity pActivity = (ProgramActivity) getActivity();
//			pActivity.setFavStatus();
		}
	}

	@Override
	public void updateJishu(ArrayList<JiShuData> jishuList, int errcode) {
		jishuListView.onRefreshComplete();
		if (errcode == JSONMessageType.SERVER_CODE_OK) {
			ArrayList<JiShuData> tempList = new ArrayList<JiShuData>(jishuList);
			if (changeSource) {
				jiShuDatas.clear();
				jishuListView.setMode(Mode.PULL_FROM_END);
			}
			this.changeSource = false;
			this.jiShuDatas.addAll(tempList);
			if (jishuList.size() < countList) {
				jishuListView.setMode(Mode.DISABLED);
			}
			updateJiShuLayout();
		}

	}
}
