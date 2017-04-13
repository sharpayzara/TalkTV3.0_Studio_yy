package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MyMedalListAdapter;
import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnMyBadgeListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;

public class MyBadgeActivity extends BaseActivity implements OnItemClickListener, OnClickListener, OnMyBadgeListener, OnRefreshListener2<ListView> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_mybadge);
		setContentView(R.layout.activity_badge);
		initMyPage();
		getMyBadgeData(0, 10);
	}

	private PullToRefreshListView myListView;
	private TextView myErrText;
	private ProgressBar myProgressBar;
	private TextView mediaNum, lingxianTxt;

	private void initMyPage() {
		myListView = (PullToRefreshListView) findViewById(R.id.listView);
		View header = getLayoutInflater().inflate(R.layout.header_badge, null);
		myListView.getRefreshableView().addHeaderView(header);
		myListView.setPullToRefreshOverScrollEnabled(false);
		mediaNum = (TextView) header.findViewById(R.id.medal_number);
		lingxianTxt = (TextView) header.findViewById(R.id.lingxian);
		myErrText = (TextView) findViewById(R.id.err_text);
		myErrText.setOnClickListener(this);
		myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		myListView.setOnItemClickListener(this);
		myAdapter = new MyMedalListAdapter(this, badgeList);
		myListView.setAdapter(myAdapter);
		myListView.setMode(Mode.PULL_FROM_END);
		myListView.setOnRefreshListener(this);
	}

	private void getMyBadgeData(int start, int count) {
		if (badgeList.size() == 0) {
			myErrText.setVisibility(View.GONE);
			myProgressBar.setVisibility(View.VISIBLE);
		}
		VolleyUserRequest.getMyBadge(this, start, count, this, this);
	}

	private MyMedalListAdapter myAdapter;
	private ArrayList<BadgeData> badgeList = new ArrayList<BadgeData>();

	private void updateMyListView(ArrayList<BadgeData> list) {
		if (list == null || list.size() == 0) {
			if (badgeList.size() == 0) {
				myErrText.setText("您还没有获得任何勋章");
				myErrText.setVisibility(View.VISIBLE);
			}
		} else {
			if (badgeList.size() == 0) {
				mediaNum.setText(String.valueOf(UserNow.current().badgeCount));
				lingxianTxt.setText("领先" + UserNow.current().badgeRate + "用户");
			}
			badgeList.addAll(list);
			myAdapter.notifyDataSetChanged();
		}
		myListView.onRefreshComplete();
		if (badgeList.size() == UserNow.current().badgeCount) {
			myListView.setMode(Mode.DISABLED);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BadgeData medaldata = badgeList.get(position - 2);
		if (medaldata != null) {
			openBadgeDetailActivity(medaldata.badgeId);
		}

	}

	private void openBadgeDetailActivity(long badgeId) {
		Intent intent = new Intent(this, BadgeDetailActivity.class);
		intent.putExtra("badgeId", (int) badgeId);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.err_text:
			getMyBadgeData(0, 10);
			break;
		default:
			break;
		}
	}

	@Override
	public void myBadgeResult(int errCode, ArrayList<BadgeData> bageList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			updateMyListView(bageList);
		} else {
			if (badgeList.size() == 0) {
				myErrText.setVisibility(View.VISIBLE);
			}
		}
		myProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMyBadgeData(badgeList.size(), 10);

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.badgeList);
	}
}
