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
import com.sumavision.talktv2.activity.UserMailActivity;
import com.sumavision.talktv2.adapter.MyFansAdapter;
import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnAddGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnMyFansListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;

/**
 * 粉丝
 * 
 */
public class FansFragment extends BaseFragment implements OnClickListener,
		OnRefreshListener2<ListView>, OnItemClickListener, OnMyFansListener,
		OnAddGuanzhuListener {
	public static FansFragment newInstance() {
		FansFragment fragment = new FansFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_myfans);
		fragment.setArguments(bundle);
		return fragment;
	}

	private PullToRefreshListView myFansListView;
	private ArrayList<User> list = new ArrayList<User>();
	private int otherid;

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		myFansListView = (PullToRefreshListView) view
				.findViewById(R.id.listView);
		myFansListView.setOnRefreshListener(this);
		myFansListView.setOnItemClickListener(this);
		myFansListView.setPullToRefreshOverScrollEnabled(false);
		adapter = new MyFansAdapter(this, list);
		myFansListView.setAdapter(adapter);
		otherid = getArguments().getInt("id");
		loadData();
	}

	private boolean firstLoad = true;

	public void loadData() {
		if (firstLoad) {
			first = 0;
			getMyFansData(20);
		}
	}

	int first;

	private void getMyFansData(int count) {
		if (first == 0) {
			myFansListView.setMode(Mode.BOTH);
		}
		VolleyUserRequest.getOtherFans(mActivity, first, count, otherid, this,
				this);
		if (list.size() == 0) {
			showLoadingLayout();
		}
	}

	private MyFansAdapter adapter;

	private void updateUI(ArrayList<User> userList) {
		if (userList != null) {
			if (first == 0) {
				list.clear();
			}
			list.addAll(userList);
			if (list.size() == 0) {
				showEmptyLayout("您还没有粉丝");
			} else {
				adapter.notifyDataSetChanged();
				myFansListView.onRefreshComplete();
				if (userList.size() < 20) {
					myFansListView.setMode(Mode.PULL_FROM_START);
				}
			}
		}
	}

	private int addPosition;

	private void onAddGuanzhuOver() {
		list.get(addPosition).isFriend = 1;
		adapter.notifyDataSetChanged();
		// if (mActivity instanceof DynamicActivity) {
		// ((DynamicActivity) mActivity).refreshFollow();
		// }
	}

	@Override
	public void reloadData() {
		first = 0;
		getMyFansData(20);
	}

	public void refresh() {
		first = 0;
		getMyFansData(20);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.guanzhu:
			addPosition = (Integer) v.getTag(R.id.item_fellow_pos);
			addGuanzhu((Integer) v.getTag(R.id.item_fellow_userId));
			break;
		case R.id.privatemsg:
			User currentUser = (User) v.getTag();
			openSendPrivateMsg(currentUser.userId, currentUser.name);
			break;
		default:
			break;
		}
	}

	private void addGuanzhu(int id) {
		showLoadingLayout();
		VolleyUserRequest.addGuanzhu(id, this, this);
	}

	private void openSendPrivateMsg(int id, String name) {
		Intent intent = new Intent(mActivity, UserMailActivity.class);
		intent.putExtra("otherUserName", name);
		intent.putExtra("otherUserId", id);
		startActivity(intent);
	}

	public static final int MY_FANS = 1;

	private void openOtherUserCenterActivity(int id) {
		Intent intent = new Intent(mActivity, UserCenterActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("from", MY_FANS);
		startActivityForResult(intent, MY_FANS);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position - 1 == list.size() || position - 1 < 0)
			return;
		int otherUserId = list.get(position - 1).userId;
		openOtherUserCenterActivity(otherUserId);
	}

	@Override
	public void getMyFans(int errCode, ArrayList<User> userList) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			updateUI(userList);
			firstLoad = false;
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void addGuanzhuResult(int errCode, ArrayList<BadgeData> badgeList) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			DialogUtil.alertToast(mActivity, "添加关注成功!");
			onAddGuanzhuOver();
		} else {
			DialogUtil.alertToast(mActivity, "添加关注失败!");
		}
		for (int i = 0; i < badgeList.size(); i++) {
			String name = UserNow.current().newBadge.get(i).name;
			if (name != null) {
				DialogUtil.showBadgeAddToast(mActivity, name);
			}
		}
		UserNow.current().newBadge = null;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		first = 0;
		getMyFansData(20);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		first = list.size();
		int end = 20;
		getMyFansData(end);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.fensiList);
		VolleyHelper.cancelRequest(Constants.guanZhuAdd);
	}
}
