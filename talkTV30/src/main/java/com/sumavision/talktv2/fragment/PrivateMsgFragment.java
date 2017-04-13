package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.UserMailActivity;
import com.sumavision.talktv2.adapter.PrivateMsgAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.MailDeleteRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnMyPrivateMsgListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 私信(消息)列表
 * 
 * @author suma-hpb
 * 
 */
public class PrivateMsgFragment extends BaseFragment implements
		OnRefreshListener2<ListView>, OnItemClickListener,
		OnMyPrivateMsgListener, OnItemLongClickListener,
		OnSharedPreferenceChangeListener {

	private PullToRefreshListView myPrivateMsgListView;
	private ArrayList<MailData> list = new ArrayList<MailData>();

	public static PrivateMsgFragment newInstance() {
		PrivateMsgFragment fragment = new PrivateMsgFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_message);
		fragment.setArguments(bundle);
		return fragment;
	}

	public void clearAll() {
		if (list.size() > 0) {
			deleteAll = true;
			String[] userIds = new String[list.size()];
			int index = 0;
			for (MailData mail : list) {
				userIds[index] = String.valueOf(mail.sid);
				index++;
			}
			delete(userIds);
		}

	}

	SharedPreferences pushSp;

	protected void initViews(View view) {
		initLoadingLayout();
		myPrivateMsgListView = (PullToRefreshListView) view
				.findViewById(R.id.listView);
		myPrivateMsgListView.setOnRefreshListener(this);
		myPrivateMsgListView.setOnItemClickListener(this);
		myPrivateMsgListView.setPullToRefreshOverScrollEnabled(false);
		myPrivateMsgListView.getRefreshableView().setOnItemLongClickListener(
				this);
		adapter = new PrivateMsgAdapter(mActivity, list);
		myPrivateMsgListView.setAdapter(adapter);
		pushSp = getActivity().getSharedPreferences(Constants.pushMessage,
				Context.MODE_PRIVATE);
	};

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("PrivateMsgFragment");
		pushSp.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("PrivateMsgFragment");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		start = 0;
		getMyPrivateMsgData(10);
	}

	ResultParser mParser = new ResultParser();

	private void delete(String[] ids) {
		VolleyHelper.post(new MailDeleteRequest(ids).make(), new ParseListener(
				mParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				onDeleteResult(mParser.errCode);
			}
		}, this);
	}

	boolean deleteAll = false;
	int selectedId = -1;

	private void onDeleteResult(int code) {
		if (code == JSONMessageType.SERVER_CODE_OK) {
			if (deleteAll) {
				list.clear();
				adapter.notifyDataSetChanged();
				showEmptyLayout("您还没有私信");
			} else {
				list.remove(selectedId);
				adapter.notifyDataSetChanged();
				if (list.size() == 0) {
					showEmptyLayout("您还没有私信");
				}
			}
		} else {
			Toast.makeText(mActivity, "删除失败", Toast.LENGTH_SHORT).show();
		}
	}

	int start;

	private void getMyPrivateMsgData(int count) {
		if (start == 0) {
			myPrivateMsgListView.setMode(Mode.BOTH);
		}
		if (list.size() == 0) {
			showLoadingLayout();
		}
		VolleyUserRequest.getMyPrivateMsg(mActivity, start, count, this, this);

	}

	private void updateUI(ArrayList<MailData> mailList) {
		if (mailList != null) {
			if (start == 0) {
				list.clear();
			}
			list.addAll(mailList);
			if (list.size() == 0) {
				showEmptyLayout("您还没有私信");
			} else {
				adapter.notifyDataSetChanged();
				if (mailCount == list.size()) {
					myPrivateMsgListView.setMode(Mode.PULL_FROM_START);
				}
			}
		}
	}

	@Override
	public void reloadData() {
		start = 0;
		getMyPrivateMsgData(10);
	}

	private PrivateMsgAdapter adapter;

	public static final int PRIVATE_MSG = 1;

	private void openPrivateMsgPage(int id, String name, String iconURL,boolean isVip) {
		Intent intent = new Intent(mActivity, UserMailActivity.class);
		intent.putExtra("otherUserId", id);
		intent.putExtra("otherUserName", name);
		intent.putExtra("otherUserIconURL", iconURL);
		intent.putExtra("from", PRIVATE_MSG);
		intent.putExtra("isVip", isVip);
		startActivityForResult(intent, PRIVATE_MSG);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position != 0) {
			if (position - 1 == list.size() || position - 1 < 0)
				return;
			int sid = list.get(position - 1).sid;
			openPrivateMsgPage(sid, list.get(position - 1).sUserName,
					list.get(position - 1).sUserPhoto,list.get(position-1).isVip);
		}
	}

	private int mailCount;

	@Override
	public void getMyPrivateMessage(int errCode, int mailCount,
			ArrayList<MailData> mailList) {
		hideLoadingLayout();
		myPrivateMsgListView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			this.mailCount = mailCount;
			if (mailList.size() < 10) {
				myPrivateMsgListView.setMode(Mode.PULL_FROM_START);
			}
			updateUI(mailList);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		start = 0;
		getMyPrivateMsgData(10);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		start = list.size();
		int count = 10;
		getMyPrivateMsgData(count);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, final long id) {
		// DeleteDialogFragment fragment = DeleteDialogFragment
		// .newInstance(getString(R.string.cache_center_delete));
		// fragment.setOnClickDeleteListener(new OnClickDeleteListener() {
		//
		// @Override
		// public void onDeleteClick() {
		// selectedId = (int) id;
		// int s = list.get(selectedId).sid;
		// deleteAll = false;
		// delete(new String[] { String.valueOf(s) });
		// }
		// });
		// fragment.show(getFragmentManager(), "delete");
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.endsWith(String.valueOf(UserNow.current().userID))) {
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pushSp.unregisterOnSharedPreferenceChangeListener(this);
		VolleyHelper.cancelRequest(Constants.mailDelete);
		VolleyHelper.cancelRequest(Constants.mailUserList);
	}
}
