package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.FriendSearchAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.RecommendUserListParser;
import com.sumavision.talktv2.http.json.RecommendUserListRequest;
import com.sumavision.talktv2.http.listener.OnRecommendUserListListener;
import com.sumavision.talktv2.http.listener.OnSearchUserListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 搜索好友
 * 
 * @author suma-hpb
 * 
 */
public class FriendSearchActivity extends BaseActivity implements
		OnClickListener, OnRecommendUserListListener, OnSearchUserListener,
		OnRefreshListener2<ListView>, OnItemClickListener {

	private Button clearBtn, searchBtn;
	private EditText searchInput;

	private PullToRefreshListView ptrListView;
	private FriendSearchAdapter searchAdapter;
	private ListView recommendListView;
	private boolean issearch = false;
	private TextView emptyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.search_friend);
		setContentView(R.layout.activity_friend_search);
		initLoadingLayout();
		searchInput = (EditText) findViewById(R.id.search_edit);
		clearBtn = (Button) findViewById(R.id.search_cancle);
		searchBtn = (Button) findViewById(R.id.btn_search);
		recommendListView = (ListView) findViewById(R.id.list_recommend);
		ptrListView = (PullToRefreshListView) findViewById(R.id.list_search);
		ptrListView.setMode(Mode.DISABLED);
		ptrListView.setOnRefreshListener(this);
		ptrListView.setOnItemClickListener(this);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		searchAdapter = new FriendSearchAdapter(this, searchUserList);
		ptrListView.setAdapter(searchAdapter);
		clearBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		emptyText = (TextView) findViewById(R.id.empty_result);
		ptrListView.setEmptyView(emptyText);
		ptrListView.setVisibility(View.GONE);

		final RecommendUserListParser recommendpaParser = new RecommendUserListParser();
		VolleyHelper.post(new RecommendUserListRequest().make(),
				new ParseListener(recommendpaParser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						getRecommendUserList(recommendpaParser.errCode,
								recommendpaParser.userList);
					}
				}, this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("FriendSearchActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("FriendSearchActivity");
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_cancle:
			searchInput.setText("");
			ptrListView.setVisibility(View.GONE);
			recommendListView.setVisibility(View.VISIBLE);
			AppUtil.hideKeyoard(this);
			issearch = false;
			break;
		case R.id.btn_search:
			// if (UserNow.current().userID == 0) {
			// startActivity(new Intent(this, LoginActivity.class));
			// } else {
			issearch = true;
			onSearchSubmitClick(0);
			// }
			break;
		default:
			break;
		}

	}

	String keyword;
	private boolean reload;

	private void onSearchSubmitClick(int start) {
		keyword = searchInput.getText().toString();
		if (!TextUtils.isEmpty(keyword)) {
			if (start == 0) {
				reload = true;
				showLoadingLayout();
			} else {
				reload = false;
			}
			VolleyUserRequest.searchUser(this, start, 20, keyword, this);
		} else {
			Toast.makeText(this, R.string.search_tip, Toast.LENGTH_SHORT)
					.show();
			issearch = false;
		}
	}

	private ArrayList<User> recommendUserList = new ArrayList<User>();
	private ArrayList<User> searchUserList = new ArrayList<User>();

	@Override
	public void getRecommendUserList(int errCode, ArrayList<User> userList) {
		ptrListView.onRefreshComplete();
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			recommendUserList.addAll(userList);
			FriendSearchAdapter adapter = new FriendSearchAdapter(this,
					this.recommendUserList);
			recommendListView.setAdapter(adapter);
			recommendListView.setOnItemClickListener(this);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void getSearchUserList(int errCode, int count,
			ArrayList<User> userList) {
		hideLoadingLayout();
		ptrListView.setVisibility(View.VISIBLE);
		recommendListView.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (reload) {
				searchUserList.clear();
			}
			this.searchUserList.addAll(userList);
			searchAdapter.notifyDataSetChanged();
			ptrListView.onRefreshComplete();
			if (this.searchUserList.size() < count) {
				ptrListView.setMode(Mode.PULL_FROM_END);
			} else {
				ptrListView.setMode(Mode.DISABLED);
			}
		} else {
			Toast.makeText(this, R.string.search_failed, Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		onSearchSubmitClick(searchUserList.size());

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (issearch) {
			Intent intent = new Intent(this, UserCenterActivity.class);
			intent.putExtra("id", searchUserList.get((int) id).userId);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, UserCenterActivity.class);
			intent.putExtra("id", recommendUserList.get((int) id).userId);
			startActivity(intent);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.searchUser);
	}

}
