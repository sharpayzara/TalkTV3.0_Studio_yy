package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.activity.WebPlayerActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.SearchMoreAdaper;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnSearchListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 
 * 节目搜索更多页面
 * 
 * @author hpb
 * 
 */
public class SearchMoreActivity extends BaseActivity implements
		OnItemClickListener, OnRefreshListener2<ListView>, OnSearchListener {
	private String searchtxt;
	private int searchType = 0;
	private PullToRefreshListView searchResultListView;
	private static final int LIST_COUNT = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_more);
		searchtxt = getIntent().getStringExtra("searchtxt");
		searchType = getIntent().getIntExtra("searchType", 0);
		String title = getIntent().getStringExtra("typeName");
		initViews();
		getSupportActionBar().setTitle(
				getString(R.string.rcmd_title_more) + title);
		search(0, LIST_COUNT, searchtxt);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SearchMoreActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SearchMoreActivity");
		super.onResume();
	}

	SearchMoreAdaper moreAdapter;

	private void initViews() {
		initLoadingLayout();
		searchResultListView = (PullToRefreshListView) findViewById(R.id.list_more);
		searchResultListView.setMode(Mode.PULL_FROM_END);
		searchResultListView.setOnRefreshListener(this);
		searchResultListView.setOnItemClickListener(this);
		searchResultListView.setPullToRefreshOverScrollEnabled(false);
		moreAdapter = new SearchMoreAdaper(this, searchList);
		searchResultListView.setAdapter(moreAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		VodProgramData programData = (VodProgramData) parent
				.getItemAtPosition(position);
		if (programData.subSearchType == 0) {
			openProgramDetailActivity(programData.id, programData.topicId,programData.ptype);
		} else if (programData.subSearchType == 1) {
			openPlayActivity(programData);
		}
	}

	private void openProgramDetailActivity(String id, String topicId,int ptype) {
		Intent intent = new Intent(this, PlayerActivity.class);
		long programId = Long.valueOf(id);
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		intent.putExtra("where",2);
		intent.putExtra("ptype",ptype);
		startActivity(intent);
	}

	private void openPlayActivity(VodProgramData vpd) {
		Intent intent = new Intent();
		if (!TextUtils.isEmpty(vpd.subUrl) && vpd.subUrl.contains(".wasu.")){
			intent.setClass(this, WebPlayerActivity.class);
			intent.putExtra("url",vpd.subUrl);
			startActivity(intent);
			return;
		}
		if (vpd.subHighPath != null && !vpd.subHighPath.equals(""))
			intent.putExtra(PlayerActivity.TAG_INTENT_HIGHURL, vpd.subHighPath);
		if (vpd.subSuperPath != null && !vpd.subSuperPath.equals(""))
			intent.putExtra(PlayerActivity.TAG_INTENT_SUPERURL,
					vpd.subSuperPath);
		long topicId = 0;
		if (!TextUtils.isEmpty(vpd.topicId)) {
			topicId = Long.parseLong(vpd.topicId);
		}
		intent.putExtra("topicId", topicId);
		intent.putExtra("title", vpd.subName);
		intent.putExtra("id", Integer.parseInt(vpd.subProgramId));
		intent.putExtra("playType", PlayerActivity.VOD_PLAY);
		intent.putExtra(PlayerActivity.TAG_INTENT_SUBID, Integer.parseInt(vpd.subId));
		if (!TextUtils.isEmpty(vpd.subVideoPath)) {// 不破解
			intent.setClass(this, WebAvoidPicActivity.class);
			intent.putExtra("path", vpd.subVideoPath);
			intent.putExtra("url", vpd.subVideoPath);
			intent.putExtra("where",2);
			startActivity(intent);
		} else if (!TextUtils.isEmpty(vpd.subUrl)) {// 破解
			intent.setClass(this, WebAvoidActivity.class);
//			intent.putExtra("path", vpd.subUrl);
			intent.putExtra("url", vpd.subUrl);
			intent.putExtra("where",2);
			startActivity(intent);
		}

	}

	private ArrayList<VodProgramData> searchList = new ArrayList<VodProgramData>();

	private void search(int first, int count, String keyWord) {
		VolleyRequest.search(this, this, keyWord, first, count);
	}

	@Override
	public void getSearchProgramList(int errCode, int totalCount,
			ArrayList<VodProgramData> programList) {
		hideLoadingLayout();
		ArrayList<VodProgramData> searchResultList = new ArrayList<VodProgramData>();
		searchResultListView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {

			for (VodProgramData item : programList) {
				if (item.name != null) {
					if (item.name.contains("点击查看更多")) {
						continue;
					}
					if (item.name.endsWith("午夜佳片")) {
						searchResultList.add(item);
						continue;
					}
					if (item.subSearchType == searchType) {
						searchList.add(item);
						searchResultList.add(item);
					}
				}
			}
			if (searchType == 0 && searchList.size() == 0) {
				showEmptyLayout("暂无搜索结果");
				return;
			}
			moreAdapter.notifyDataSetChanged();
			if (searchResultList.size() < LIST_COUNT) {
				searchResultListView.setMode(Mode.DISABLED);
			}
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		search(searchList.size(), LIST_COUNT, searchtxt);

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.searchProgram);
	}
}
