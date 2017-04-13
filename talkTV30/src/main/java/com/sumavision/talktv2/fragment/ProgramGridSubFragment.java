package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.JujiAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 
 * 节目页--grid型
 * 
 * @author Administrator
 * @discraption
 * @changeLog
 * 
 */
@SuppressLint("NewApi")
public class ProgramGridSubFragment extends ProgramBaseFragment implements
		OnItemClickListener, OnRefreshListener<ScrollView> {
	private PullToRefreshScrollView scrollview;
	public int subid;

	public static ProgramGridSubFragment newInstance() {
		ProgramGridSubFragment fragment = new ProgramGridSubFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_program_grid);
		fragment.setArguments(bundle);
		return fragment;
	}

	private StaticGridView jiShuGridView;

	@Override
	protected void initViews(View view) {
		programData = ((ProgramDetailHalfActivity) getParentFragment()).programData;
		scrollview = (PullToRefreshScrollView) view
				.findViewById(R.id.scrollview);
		jiShuGridView = (StaticGridView) view.findViewById(R.id.jishu);
		jiShuGridView.setOnItemClickListener(this);
		scrollview.setMode(Mode.PULL_FROM_END);
		scrollview.setOnRefreshListener(this);
		initHeaderView();
		ushowSwitchRequest();
		updateProgramHeader();
		onProgramHeader();
		updateSignLayout();
		if (programData.platformList.size() > 0) {
			ArrayList<JiShuData> list = new ArrayList<JiShuData>(
					programData.platformList.get(0).jishuList);
			this.jiShuDatas.addAll(list);
			if (programData.platformList.size() == 1) {
				singleSourceLayout.setClickable(false);
				sourceDownView.setVisibility(View.GONE);
			}
			if (jiShuDatas.size() < countGrid) {
				scrollview.setMode(Mode.DISABLED);
			}
			loadImage(sourceImg, programData.platformList.get(0).pic,
					R.drawable.play_source_default);
		}
		updateJiShuLayout();
	}

	@Override
	public void onProgramHeader() {
		if (getActivity() != null) {
//			ProgramActivity pActivity = (ProgramActivity) getActivity();
//			pActivity.setFavStatus();
		}
	}

	private void updateJiShuLayout() {
		if (mActivity == null) {
			return;
		}
		if (jiShuDatas != null && jiShuDatas.size() > 0) {
			filterCacheInfo(jiShuDatas);
		} else {
			scrollview.setMode(Mode.DISABLED);
		}
		if (jishuAdapter == null) {
			String name = PreferencesUtils.getString(getActivity(), null, programData.programId + "");
			jishuAdapter = new JujiAdapter(mActivity, JujiAdapter.TYPE_TV,
					jiShuDatas, name);
			jiShuGridView.setAdapter(jishuAdapter);
		} else {
			jishuAdapter.notifyDataSetChanged();
		}
	}

	JujiAdapter jishuAdapter;

	ArrayList<JiShuData> jiShuDatas = new ArrayList<JiShuData>();

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
		jishuAdapter.setName(null);
		int pos = (int) id;
		jishuAdapter.setSelectedPosition(pos);
		jishuAdapter.notifyDataSetChanged();
		subid = jiShuDatas.get(pos).id;
		PreferencesUtils.putString(getActivity(), null, programData.programId + "", jiShuDatas.get(arg2).name);
		DownloadInfo downloadInfo = isCacheVideo(subid);
	}

	@Override
	public void updateJishu(ArrayList<JiShuData> jishuList, int errcode) {
		if (errcode == JSONMessageType.SERVER_CODE_OK) {
			ArrayList<JiShuData> tempList = new ArrayList<JiShuData>(jishuList);
			if (changeSource) {
				jiShuDatas.clear();
			}
			this.jiShuDatas.addAll(tempList);
			this.changeSource = false;
			if (jishuList.size() < countGrid) {
				scrollview.setMode(Mode.DISABLED);
			}
			updateJiShuLayout();
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
		getPlatVideoList(currentPlatPos, jiShuDatas.size(), countGrid);
	}

	@Override
	protected void hideLoadingLayout() {
		super.hideLoadingLayout();
		scrollview.onRefreshComplete();
	}

}
