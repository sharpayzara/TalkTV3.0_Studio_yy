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
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.activity.DLNAControllActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.SpecialColumnAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ColumnVideoListParser;
import com.sumavision.talktv2.http.json.ColumnVideoListRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 推荐页中电影、电视剧、综艺等
 * 
 * @author suma-hpb
 * @version
 * @description
 */

public class SpecialRecommendColumnActivity extends BaseActivity implements OnItemClickListener, OnClickListener, OnRefreshListener2<ListView>, OnScrollListener {

	private int columnId;
	private boolean firstload = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		String title_txt = i.getStringExtra("title");
		getSupportActionBar().setTitle(title_txt);
		columnId = i.getIntExtra("id", 0);
		setContentView(R.layout.rcmd_program_viewpager_item);
		initViews();
		if (firstload) {
			request(columnId, 0, 20, true);
		}
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SpecialRecommendColumnActivity");
		super.onPause();
	}

	boolean needLoadData;
	private PullToRefreshListView listView;
	private TextView errTextView;
	private ImageButton dlnaResume;

	private void initViews() {
		initLoadingLayout();
		dlnaResume = (ImageButton) findViewById(R.id.dlna_resume);
		dlnaResume.setOnClickListener(this);
		listView = (PullToRefreshListView) findViewById(R.id.listView);
		errTextView = (TextView) findViewById(R.id.err_text);
		listView.setOnRefreshListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(this);
		listView.setMode(Mode.PULL_FROM_START);
		listView.setPullToRefreshOverScrollEnabled(false);
		adapter = new SpecialColumnAdapter(this, columnList);
		listView.setAdapter(adapter);
		errTextView.setOnClickListener(this);
	}

	private ArrayList<VodProgramData> columnList = new ArrayList<VodProgramData>();

	ColumnVideoListParser listParser = new ColumnVideoListParser();

	private void request(int columnId, int first, int count, final boolean refresh) {
		VolleyHelper.post(new ColumnVideoListRequest(columnId, first, count).make(), new ParseListener(listParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (listParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					firstload = false;
					hideLoadingLayout();
					listView.onRefreshComplete();
					if (refresh) {
						columnList.clear();
					}
					columnList.addAll(listParser.programList);
					adapter.notifyDataSetChanged();
					loading = false;
					if (listParser.programList.size() < 20) {
						overloading = true;
					} else {
						overloading = false;
					}
				} else {
					showErrorLayout();
				}
			}
		}, null);
	}

	SpecialColumnAdapter adapter;

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("SpecialRecommendColumnActivity");
		super.onResume();
		if (DLNAControllActivity.needShowResumeBtn) {
			dlnaResume.setVisibility(View.VISIBLE);
		} else {
			dlnaResume.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		int listPosition = position - 1;
		if (listPosition == columnList.size() || listPosition < 0) {
			return;
		}
		VodProgramData tempTv = columnList.get(listPosition);
		String tvId = tempTv.id;
		String tvTopicId = tempTv.topicId;
		MobclickAgent.onEvent(this, "jiemu", tempTv.name);
		openProgramDetailActivity(tvId, tvTopicId, tempTv.updateName, 0L);

	}

	private void openProgramDetailActivity(String id, String topicId, String updateName, long cpId) {
		Intent intent = new Intent(this, PlayerActivity.class);
		long programId = Long.valueOf(id);
		long longTopicId = Long.valueOf(topicId);
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		intent.putExtra("topicId", longTopicId);
		intent.putExtra("cpId", cpId);
		intent.putExtra("updateName", updateName);
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
			request(columnId, 0, 20, true);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		request(columnId, 0, 20, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.columnVideoList);
	}

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
		if (view.getLastVisiblePosition() > (totalItemCount - 3) && !loading && !overloading && totalItemCount > 0 && !firstload) {
			loading = true;
			request(columnId, columnList.size(), 20, false);
		}
	}
}
