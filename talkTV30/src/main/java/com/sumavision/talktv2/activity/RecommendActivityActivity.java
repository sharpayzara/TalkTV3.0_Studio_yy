package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.activity.DLNAControllActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.MedalListAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PlayNewData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.activities.ActivityListParser;
import com.sumavision.talktv2.http.json.activities.ActivityListRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 推荐活动(徽章活动列表)
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RecommendActivityActivity extends BaseActivity implements OnItemClickListener, OnClickListener, OnRefreshListener2<ListView>, OnScrollListener {

	boolean needLoadData;
	private ImageButton dlnaResume;
	private TextActionProvider myPrice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("徽章墙");

		setContentView(R.layout.activity_recommend_activity);
		initAllPage();
		getActivityListData(0, 20, true);
	}

	private void initAllPage() {
		initLoadingLayout();
		hideLoadingLayout();
		dlnaResume = (ImageButton) findViewById(R.id.dlna_resume);
		dlnaResume.setOnClickListener(this);
		allListView = (PullToRefreshListView) findViewById(R.id.listView);
		allListView.setPullToRefreshOverScrollEnabled(false);
		allListView.setOnItemClickListener(this);
		allListView.setOnRefreshListener(this);
		allListView.setOnScrollListener(this);
		allListView.setMode(Mode.PULL_FROM_START);
		allAdapter = new MedalListAdapter(this, allList);
		allListView.setAdapter(allAdapter);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("RecommendActivityActivity");
		super.onPause();
	}

	private PullToRefreshListView allListView;
	private ArrayList<PlayNewData> allList = new ArrayList<PlayNewData>();

	ActivityListParser listParser;

	private void getActivityListData(int start, int count, final boolean refresh) {
		if (allList.size() == 0) {
			showLoadingLayout();
		}
		listParser = new ActivityListParser();
		VolleyHelper.post(new ActivityListRequest(this, start, count).make(), new ParseListener(listParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				allListView.onRefreshComplete();
				if (listParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					if (refresh) {
						allList.clear();
					}
					if (listParser.activityList != null && listParser.activityList.size() > 0) {
						allList.addAll(listParser.activityList);
						allAdapter.notifyDataSetChanged();
					} else {
						showEmptyLayout("暂无活动");
					}
					if (listParser.activityList.size() < 20) {
						overloading = true;
					} else {
						overloading = false;
					}
					loading = false;
					hideLoadingLayout();
				}
			}
		}, this);

	}

	MedalListAdapter allAdapter;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position - 1 == allList.size() || position - 1 < 0)
			return;
		PlayNewData data = allList.get(position - 1);
		// int allId = data.id;
		// int state = data.state;
		// String activityName = data.name;
		if (data.typeId == 6) {
			// Intent intent = new Intent(this, ActivityActivity.class);
			Intent intent = new Intent(this, ActivityActivity.class);
			long aid = data.id;
			intent.putExtra("activityId", aid);
			intent.putExtra("activityName", data.name);
			intent.putExtra("state", data.state);
			intent.putExtra("joinStatus", data.joinStatus);
			startActivity(intent);
		} else {
			// if (UserNow.current().userID != 0)
			// openActivityDetailActivity(allId, 0, state, activityName);
			// else
			// openLoginActivity();
		}
	}

	// private void openActivityDetailActivity(int id, int from, int state,
	// String name) {
	// Intent intent = new Intent(this, ActivitiesDetailActivity.class);
	// intent.putExtra("id", id);
	// intent.putExtra("from", from);
	// intent.putExtra("state", state);
	// intent.putExtra("name", name);
	// startActivity(intent);
	// }

	private void openLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dlna_resume:
			Intent intent = new Intent(this, DLNAControllActivity.class);
			intent.putExtra("isResume", true);
			startActivity(intent);
			break;
		case R.id.err_text:
			getActivityListData(0, 20, true);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_activity, menu);
		myPrice = (TextActionProvider) menu.findItem(R.id.action_price).getActionProvider();
		if (myPrice == null){
			return super.onCreateOptionsMenu(menu);
		}
		myPrice.setShowText(R.string.my_gift);
		myPrice.setOnClickListener(ActionProviderClickListener, "gift");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("RecommendActivityActivity");
		super.onResume();
		if (DLNAControllActivity.needShowResumeBtn) {
			dlnaResume.setVisibility(View.VISIBLE);
		} else {
			dlnaResume.setVisibility(View.GONE);
		}
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.activityList);
	}

	OnClickListener ActionProviderClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (UserNow.current().userID == 0) {
				openLoginActivity();
			} else {
				String tag = (String) v.getTag();
				if ("gift".equals(tag)) {
					if (UserNow.current().userID > 0) {
						startActivity(new Intent(RecommendActivityActivity.this, MyGiftActivity.class));
					} else {
						startActivity(new Intent(RecommendActivityActivity.this, LoginActivity.class));
					}
				}
			}

		}
	};

	private boolean loading = false;
	private boolean overloading = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1 && overloading) {
				ToastHelper.showToast(this, "已经滑动到底部");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view.getLastVisiblePosition() > (totalItemCount - 3) && !loading && !overloading && totalItemCount > 0) {
			loading = true;
			getActivityListData(allList.size(), 20, false);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getActivityListData(0, 20, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

}
