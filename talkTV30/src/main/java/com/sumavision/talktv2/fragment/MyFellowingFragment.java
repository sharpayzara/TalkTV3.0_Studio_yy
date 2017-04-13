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
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.UserCenterActivity;
import com.sumavision.talktv2.adapter.MyFellowingAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.GuanZhuUpdateEvent;
import com.sumavision.talktv2.http.listener.OnDeleteGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnMyFollowListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 我的关注
 * 
 * @author suma-hpb
 * 
 */
public class MyFellowingFragment extends BaseFragment implements
		OnClickListener, OnItemClickListener, OnRefreshListener2<ListView>,
		OnMyFollowListener, OnDeleteGuanzhuListener {

	public static MyFellowingFragment newInstance() {
		MyFellowingFragment fragment = new MyFellowingFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_myfellowing);
		fragment.setArguments(bundle);
		return fragment;
	}

	private PullToRefreshListView myFellowingListView;
	private ArrayList<User> followUserList = new ArrayList<User>();
	MyFellowingAdapter adapter;
	private int otherid;

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		myFellowingListView = (PullToRefreshListView) view
				.findViewById(R.id.listView);
		otherid = getArguments().getInt("id");
		adapter = new MyFellowingAdapter(this, otherid, followUserList);
		myFellowingListView.setAdapter(adapter);
		myFellowingListView.setOnRefreshListener(this);
		myFellowingListView.setOnItemClickListener(this);
		myFellowingListView.setPullToRefreshOverScrollEnabled(false);

		if (otherid == UserNow.current().userID) {

		} else {

		}
		loadData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("MyFellowingFragment");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyFellowingFragment");
		super.onPause();
	}

	public void onEvent(GuanZhuUpdateEvent e) {
		start = 0;
		getMyFellowingData(20);
	}

	private boolean firstLoad = true;

	public void loadData() {
		if (firstLoad) {
			start = 0;
			getMyFellowingData(20);
		}
	}

	public void refresh() {
		start = 0;
		getMyFellowingData(20);
	}

	int start;

	private void getMyFellowingData(int count) {
		if (start == 0) {
			myFellowingListView.setMode(Mode.BOTH);
		}
		if (followUserList.size() == 0) {
			showLoadingLayout();
		}
		VolleyUserRequest.getMyFollow(mActivity, otherid, start, count, this,
				this);
	}

	private void updateUI(int totalCont, ArrayList<User> list) {
		if (list != null) {
			if (start == 0) {
				this.followUserList.clear();
			}
			this.followUserList.addAll(list);
			if (followUserList.size() == 0) {
				showEmptyLayout("你还没有粉过任何人");
			} else {
				adapter.notifyDataSetChanged();
				myFellowingListView.onRefreshComplete();
				if (list.size() < 20) {
					myFellowingListView.setMode(Mode.PULL_FROM_START);
				}

			}
		}
	}

	private int deletePosition;

	private void OnDeleteOver() {
		if (deletePosition >= 0 && deletePosition < followUserList.size()) {
			followUserList.remove(deletePosition);
			if (followUserList.size() == 0) {
				showEmptyLayout("你还没有粉过任何人");
				adapter.notifyDataSetChanged();
				myFellowingListView.onRefreshComplete();
			} else {
				adapter.notifyDataSetChanged();
				myFellowingListView.onRefreshComplete();
			}
			// if (mActivity instanceof DynamicActivity) {
			// ((DynamicActivity) mActivity).refreshFans();
			// }
		}

	}

	@Override
	public void reloadData() {
		start = 0;
		getMyFellowingData(20);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.guanzhu:
			deletePosition = (Integer) v.getTag(R.id.item_pos);
			int userId = (Integer) v.getTag(R.id.item_id);
			deleteGuanzhu(userId);
			break;
		default:
			break;
		}
	}

	private void deleteGuanzhu(int id) {
		VolleyUserRequest.deleteGuanzhu(id, this, this);
	}

	private void openOtherUserCenterActivity(int id) {
		Intent intent = new Intent(mActivity, UserCenterActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position - 1 == followUserList.size() || position - 1 < 0)
			return;
		int otherUserId = followUserList.get(position - 1).userId;
		openOtherUserCenterActivity(otherUserId);
	}

	@Override
	public void getMyFollow(int errCode, int guanzhuCont, ArrayList<User> list) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			firstLoad = false;
			updateUI(guanzhuCont, list);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void deleteGuanzhuResult(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			DialogUtil.alertToast(mActivity, "取消关注成功");
			EventBus.getDefault().post(new GuanZhuUpdateEvent());
			OnDeleteOver();
		} else {
			DialogUtil.alertToast(mActivity, "取消关注失败");
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		start = 0;
		getMyFellowingData(20);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int count = 20;
		start = followUserList.size();
		getMyFellowingData(count);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.guanzhuList);
		VolleyHelper.cancelRequest(Constants.guanZhuCancel);
		EventBus.getDefault().unregister(this);
	}
}
