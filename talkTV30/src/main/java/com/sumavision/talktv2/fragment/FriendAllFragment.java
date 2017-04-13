package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramActivity;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.activity.UserCenterActivity;
import com.sumavision.talktv2.adapter.EventAdapter;
import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnEventRoomListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 所有人页面
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FriendAllFragment extends BaseFragment implements OnItemClickListener, OnClickListener, OnRefreshListener2<ListView>, OnEventRoomListener {

	public static FriendAllFragment newInstance() {
		FriendAllFragment fragment = new FriendAllFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_all_friend);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("FriendAllFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("FriendAllFragment");
	}

	@Override
	protected void initViews(View view) {
		initAllPager(rootView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getAllUserList(0, 20);
	}

	private PullToRefreshListView myAllListView;
	private ArrayList<EventData> allList = new ArrayList<EventData>();
	EventAdapter adapter;

	private void initAllPager(View view) {
		initLoadingLayout();
		myAllListView = (PullToRefreshListView) view.findViewById(R.id.listView);
		myAllListView.setOnRefreshListener(this);
		myAllListView.setOnItemClickListener(this);
		myAllListView.setPullToRefreshOverScrollEnabled(false);
		adapter = new EventAdapter(mActivity, allList);
		myAllListView.setAdapter(adapter);
	}

	private void getAllUserList(int start, int count) {
		if (allList.size() == 0) {
			showLoadingLayout();
		}
		if (start == 0) {
			refresh = true;
			myAllListView.setMode(Mode.BOTH);
		} else {
			refresh = false;
		}
		VolleyUserRequest.getEventRoom(start, count, this, this);
	}

	private boolean refresh;

	private void updateAllListView(ArrayList<EventData> evenList) {
		if (eventTotalCount == 0) {
			showEmptyLayout("暂无信息");
		} else {
			if (refresh) {
				allList.clear();
			}
			allList.addAll(evenList);
			adapter.notifyDataSetChanged();
		}
		myAllListView.onRefreshComplete();
		if (eventTotalCount == allList.size()) {
			myAllListView.setMode(Mode.PULL_FROM_START);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		EventData temp = allList.get((int) arg3);
		switch (temp.toObjectType) {
		case 1: // program
			openProgramDetailActivity(String.valueOf(temp.toObjectId), "");
			break;
		case 9:
			openOtherUserCenterActivity(temp.toObjectId);
			break;
		default:
			break;
		}
	}

	private void openProgramDetailActivity(String id, String topicId) {
		Intent intent = new Intent(getActivity(), PlayerActivity.class);
		long programId = Long.valueOf(id);
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		startActivity(intent);
	}

	private void openOtherUserCenterActivity(int id) {
		Intent intent = new Intent(getActivity(), UserCenterActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.err_text:
			getAllUserList(0, 20);
			break;
		default:
			break;
		}
	}

	private int eventTotalCount;

	@Override
	public void eventRoomResult(int errCode, int eventCount, ArrayList<EventData> evenList) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (allList.size() == 0) {
				showErrorLayout();
			} else {
				DialogUtil.alertToast(mActivity, "加载更多失败!");
			}
			break;
		case JSONMessageType.SERVER_CODE_OK:
			hideLoadingLayout();
			eventTotalCount = eventCount;
			updateAllListView(evenList);
			break;
		default:
			break;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getAllUserList(0, 20);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getAllUserList(allList.size(), 20);
	}

	@Override
	public void reloadData() {
		getAllUserList(0, 20);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.eventRoomList);
	}

}
