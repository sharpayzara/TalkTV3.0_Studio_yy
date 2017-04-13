package com.sumavision.talktv2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.JujiAdapter;
import com.sumavision.talktv2.bean.EpisodeEvent;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.LoadSubListParser;
import com.sumavision.talktv2.http.json.LoadSubListRequest;
import com.sumavision.talktv2.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ProgramSubFragment extends Fragment {
	
	private PullToRefreshListView episodeList;
	private StaticGridView episodeGrid;
	private PullToRefreshScrollView episodeScroll;
	private JujiAdapter adapter;
	private TextView back;
	
	private int platformId;
	private int subId;
	
	private ProgramData programData;
	private final int LOADING_NUMBER = 50;
	private boolean isOver;
	
	View loading;
	
	private ProgramDetailHalfActivity fragment;
	
	private ArrayList<JiShuData> episodeData = new ArrayList<JiShuData>();


	public static ProgramSubFragment newInstance(int platformid,int subId) {
		ProgramSubFragment fragment = new ProgramSubFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("platformId", platformid);
		bundle.putInt("subId", subId);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		getExtras();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_program_juji, null);
		episodeList = (PullToRefreshListView) view.findViewById(R.id.episode_list);
		episodeGrid = (StaticGridView) view.findViewById(R.id.episode_grid);
		episodeScroll = (PullToRefreshScrollView) view.findViewById(R.id.episode_scrollview);
		back = (TextView) view.findViewById(R.id.episode_fragment_back);
		episodeScroll.setMode(Mode.PULL_FROM_END);
		episodeList.setMode(Mode.PULL_FROM_END);
		loading = view.findViewById(R.id.loading);
		loading.setVisibility(View.VISIBLE);
		updateView();
		getPlatformData((int)programData.programId,platformId,0, LOADING_NUMBER);
		initEvents();
		fragment = ((ProgramDetailHalfActivity)getParentFragment());
		return view;
	}
	
	private void getExtras() {
		programData = ((ProgramDetailHalfActivity) getParentFragment()).programData;
		platformId = getArguments().getInt("platformId");
		subId = getArguments().getInt("subId");
	}
	
	private void updateView() {
		if (programData.pType == ProgramData.TYPE_TV 
				|| programData.pType == ProgramData.TYPE_DONGMAN) {
			adapter = new JujiAdapter(getActivity(), ProgramData.TYPE_TV, episodeData);
			episodeGrid.setVisibility(View.VISIBLE);
			episodeGrid.setAdapter(adapter);
			episodeScroll.setVisibility(View.VISIBLE);
			episodeScroll.getRefreshableView().setVisibility(View.VISIBLE);
		} else {
			episodeList.setVisibility(View.VISIBLE);
			episodeList.getRefreshableView().setVisibility(View.VISIBLE);
			adapter = new JujiAdapter(getActivity(), programData.subType == 2 ? JujiAdapter.TYPE_PIC : JujiAdapter.TYPE_MOVIE, episodeData);
			episodeList.setAdapter(adapter);
		}
	}
	private void getPlatformData(int programId,int platformId, final int first,int count){
		final LoadSubListParser loadSubListParser = new LoadSubListParser();
		VolleyHelper.post(
				new LoadSubListRequest(
						programId,platformId,first, count).make(),
				new ParseListener(loadSubListParser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						loading.setVisibility(View.GONE);
						episodeList.onRefreshComplete();
						episodeScroll.onRefreshComplete();
						if (loadSubListParser.errCode == JSONMessageType.SERVER_CODE_OK) {
							List<JiShuData> temp = loadSubListParser.subList;
							if (temp == null || temp.size() == 0){
								return;
							}
							fragment.filterCacheInfo(temp, programData);
							episodeData.addAll(temp);
							updateEpisodeView();
							if (first == 0){
								EpisodeEvent event = new EpisodeEvent();
								event.subid = subId;
								onEvent(event);
							}
							if(temp != null && temp.size()<LOADING_NUMBER){
								isOver = true;
								episodeList.setMode(Mode.DISABLED);
								episodeScroll.setMode(Mode.DISABLED);
							}
						}
					}
				}, null);
	}
//	private void getPlatformData(int first, int count) {
//		final PlatVideoParser platVideoParser = new PlatVideoParser();
//		VolleyHelper.post(
//				new PlatVideoRequest(
//						platformId,
//						programData.programId, 0, first, count).make(),
//				new ParseListener(platVideoParser) {
//					@Override
//					public void onParse(BaseJsonParser parser) {
//						loading.setVisibility(View.GONE);
//						episodeList.onRefreshComplete();
//						episodeScroll.onRefreshComplete();
//						if (platVideoParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//							ArrayList<JiShuData> temp = platVideoParser.subList;
//							fragment.filterCacheInfo(temp, programData);
//							episodeData.addAll(temp);
//							updateEpisodeView();
//							if(temp != null && temp.size()<LOADING_NUMBER){
//								episodeList.setMode(Mode.DISABLED);
//								episodeScroll.setMode(Mode.DISABLED);
//							}
//						}
//					}
//				}, null);
//	}
	
	private void updateEpisodeView() {
		adapter.notifyDataSetChanged();
	}
	
	public void onEvent(EpisodeEvent e) {
		if (episodeData != null) {
			for (int i = 0; i < episodeData.size(); i++) {
				if (episodeData.get(i).id == e.subid) {
					if (adapter != null && this.isVisible() && !this.isRemoving()) {
						adapter.setSelectedPosition(i);
						adapter.notifyDataSetChanged();
//						fragment.setCurrentJuji(episodeData, i);
						break;
					}
				}
			}
		}
	}
	
	private void initEvents() {
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
		episodeList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getPlatformData((int)programData.programId,platformId,episodeData.size(), LOADING_NUMBER);
			}
		});
		episodeScroll.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				getPlatformData((int)programData.programId,platformId,episodeData.size(), LOADING_NUMBER);
			}
		});
		episodeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = (int) id;
//				fragment.getCommentData(episodeData.get(pos).topicId, 0, 10);
				adapter.setSelectedPosition(pos);
				adapter.notifyDataSetChanged();
				if (episodeData.get(pos).id == fragment.curVideo.subId) {
					return;
				}
				if (pos == adapter.getCount()-1 && isOver){
					fragment.setLastFlag(true);
				}else {
					fragment.setLastFlag(false);
				}
				fragment.switchVideo((int) programData.programId, episodeData.get(pos).id);
			}
		});
		episodeGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setName(null);
				int pos = (int) id;
//				fragment.getCommentData(episodeData.get(pos).topicId, 0, 10);
				adapter.setSelectedPosition(pos);
				adapter.notifyDataSetChanged();
				if (episodeData.get(pos).id == fragment.curVideo.subId) {
					return;
				}
				if (pos == adapter.getCount()-1 && isOver){
					fragment.setLastFlag(true);
				}else {
					fragment.setLastFlag(false);
				}
				fragment.switchVideo((int) programData.programId, episodeData.get(pos).id);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		VolleyHelper.cancelRequest(Constants.loadSubList);
	}

}
