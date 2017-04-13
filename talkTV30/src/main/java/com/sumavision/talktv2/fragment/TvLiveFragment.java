package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LiveDetailActivity;
import com.sumavision.talktv2.adapter.ChannelAdapter;
import com.sumavision.talktv2.adapter.ChannelTypeAdapter;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendTag;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.LiveParser;
import com.sumavision.talktv2.http.json.LiveRequest;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 电视直播
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class TvLiveFragment extends BaseFragment implements OnItemClickListener,
		OnRefreshListener2<ListView>, OnClickListener, OnScrollListener {
	private ListView typeListView;
	private PullToRefreshListView channelListView;
	

	public static TvLiveFragment newInstance() {
		TvLiveFragment fragment = new TvLiveFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_tvlive);
		fragment.setArguments(bundle);
		return fragment;
	}

	boolean pageAnalytic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (isFirstLoading) {
				showLoadingLayout();
			}
			getchannelProgramData();
			pageAnalytic = true;
			MobclickAgent.onEvent(mActivity, "zhibo");
			MobclickAgent.onPageStart("TvLiveFragment");
//			if (!PreferencesUtils.getBoolean(getActivity(), null, "liveGuide")) {
//				Intent intent = new Intent(getActivity(), GuideActivity.class);
//				intent.putExtra("type", GuideActivity.GUIDE_LIVE);
//				startActivity(intent);
//			}
		} else {
			if (pageAnalytic) {
				pageAnalytic = false;
				MobclickAgent.onPageEnd("TvLiveFragment");
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (pageAnalytic) {
			pageAnalytic = false;
			MobclickAgent.onPageEnd("TvLiveFragment");
		}
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		isFirstLoading = true;
		typeListView = (ListView) view.findViewById(R.id.list_category);
		channelListView = (PullToRefreshListView) view
				.findViewById(R.id.list_program);
		channelListView.setPullToRefreshOverScrollEnabled(false);
		typeListView.setOnItemClickListener(this);
		channelListView.setOnRefreshListener(this);
		channelListView.setOnScrollListener(this);
		channelListView.setMode(Mode.PULL_FROM_START);
		channelAdapter = new ChannelAdapter(mActivity, this, channelList);
		channelListView.setAdapter(channelAdapter);
	}

	boolean isFirstLoading = true;
	LiveParser liveParser;

	public void getchannelProgramData() {
		if (isFirstLoading) {
			isFirstLoading = false;
//			channelList.clear();
//			typeName.clear();
//			if (channelAdapter != null) {
//				channelAdapter.notifyDataSetChanged();
//			}
//			if (typeAdapter != null) {
//				typeAdapter.notifyDataSetChanged();
//			}
			
			String umengValue = "";
			try {
				umengValue = AppUtil
						.getMetaData(getActivity(), "UMENG_CHANNEL");
			} catch (Exception e) {
				umengValue = "";
			}
			liveParser = new LiveParser(umengValue);
			VolleyHelper.post(new LiveRequest(getActivity()).make(), new ParseListener(liveParser) {
				@Override
				public void onParse(BaseJsonParser parser) {
					hideLoadingLayout();
					if (liveParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						channelList.clear();
						typeName.clear();
						channelList.addAll(liveParser.channelInfo);
						typeName.addAll(liveParser.channelList);
						updateTypeView(liveParser.channelList);
					} else {
						showErrorLayout();
					}
				}
			}, null);
		}
	}
	
	private ChannelTypeAdapter typeAdapter;
	private ChannelAdapter channelAdapter;
	private ArrayList<ShortChannelData> channelList = new ArrayList<ShortChannelData>();
	private ShortChannelData currentChannel;
	private ArrayList<String> typeName = new ArrayList<String>();
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MobclickAgent.onEvent(getActivity(), "zbzuocelanmu");
		String name = typeName.get(position);
		for (int i = 0; i < channelList.size(); i++) {
			ShortChannelData data = channelList.get(i);
			if (name.equals(data.typeName)) {
				channelListView.getRefreshableView().setSelection(i + 1);
				break;
			}
		}
		typeAdapter.setSelectedPos(position);
		typeAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void reloadData() {
		isFirstLoading = true;
		showLoadingLayout();
		getchannelProgramData();
	}

	boolean refresh = false;
	
	private void updateTypeView(ArrayList<String> list) {
		if (getActivity() != null) {
			typeAdapter = new ChannelTypeAdapter(mActivity, list);
			typeListView.setAdapter(typeAdapter);
			
			channelListView.onRefreshComplete();
			channelAdapter.notifyDataSetChanged();
			if (recTag != null){
				recName = liveParser.leftChannel.get(recTag.id);
				onItemClick(null,null,getPosByName(recName),0);
				recTag = null;
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isFirstLoading = true;
		getchannelProgramData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void onClick(View v) {
		currentChannel = channelAdapter.getItem((Integer) v.getTag());
		switch (v.getId()) {
		case R.id.tvframe:
			if (currentChannel.livePlay) {
				MobclickAgent.onEvent(getActivity(), "zbjiemudianji");
				liveThroughNet(currentChannel.netPlayDatas,
						currentChannel.programId, currentChannel.channelId,currentChannel.toWeb,currentChannel.programName);
			}
			break;
		case R.id.layout_goplay:
			if (currentChannel.livePlay) {
				MobclickAgent.onEvent(getActivity(), "zbyoucedianjibofang");
				liveThroughNet(currentChannel.netPlayDatas,
						currentChannel.programId, currentChannel.channelId,currentChannel.toWeb,currentChannel.programName);
			}
			break;
		case R.id.infoBtn:
			MobclickAgent.onEvent(getActivity(), "zbyoucejiemudan");
			PreferencesUtils.putInt(mActivity, null, "channelId",
					currentChannel.channelId);
			Intent i = new Intent(mActivity, LiveDetailActivity.class);
			i.putExtra("channelName", currentChannel.channelName);
			i.putExtra("channelId", currentChannel.channelId);
			i.putExtra("pic", currentChannel.channelPicUrl);
			i.putExtra("toWeb",currentChannel.toWeb);
			startActivity(i);
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyRequest.cancelRequest(Constants.channelTypeDetailList);
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			int firstVisibleItem = view.getFirstVisiblePosition();
			int lastVisibleItem = view.getLastVisiblePosition();
			int size = 0;
			if (channelList != null) {
				size = channelList.size();
			}
			if (size > 0) {
				if (lastVisibleItem == view.getCount() - 1) {
					typeAdapter.setSelectedPos(typeName.size() - 1);
					typeAdapter.notifyDataSetInvalidated();
					typeListView.setSelection(typeName.size() - 1);
					Toast.makeText(getActivity(), "无更多内容", Toast.LENGTH_SHORT).show();
					return;
				}
				if (firstVisibleItem < size && firstVisibleItem != 0) {
					ShortChannelData data = channelList.get(firstVisibleItem - 1);
					String name = data.typeName;
					for (int i = 0; i < typeName.size(); i++) {
						if (name.equals(typeName.get(i))) {
							typeAdapter.setSelectedPos(i);
							typeAdapter.notifyDataSetInvalidated();
							typeListView.setSelection(i);
						}
					}
				} else if (firstVisibleItem < size && firstVisibleItem == 0) {
					typeAdapter.setSelectedPos(0);
					typeAdapter.notifyDataSetInvalidated();
					typeListView.setSelection(0);
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	RecommendTag recTag;
	String recName = "";
	public void onEvent(EventMessage msg){
		if (msg.name.equals("TvLiveFragment")){
			recTag = (RecommendTag) msg.bundle.getSerializable("tag");
			if (liveParser.leftChannel != null && liveParser.leftChannel.size()>0){
				recName = liveParser.leftChannel.get(recTag.id);
				onItemClick(null,null,getPosByName(recName),0);
				recTag = null;
			}
		}
	}
	public int getPosByName(String name){
		for (int i=0; i<typeName.size(); i++){
			if (typeName.get(i).equals(name)){
				return i;
			}
		}
		return 0;
	}
}
