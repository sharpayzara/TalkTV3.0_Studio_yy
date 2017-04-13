package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.activity.WebPlayerActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.HotKeyAdapter;
import com.sumavision.talktv2.adapter.SearchResultAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.KeyWordData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnHotSearchListener;
import com.sumavision.talktv2.http.listener.OnSearchListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.StringUtils;
import com.sumavision.talktv2.widget.sticky.StickyListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 搜索页面
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, OnHotSearchListener, OnSearchListener {

	private EditText input;
	private StickyListView searchResultListView;
	private ArrayList<VodProgramData> searchList = new ArrayList<VodProgramData>();
	private SearchResultAdapter searchListAdapter;
	private Button searchBtn,shakeBtn;
	private View footer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getSupportActionBar().setTitle(R.string.search);
		initViews();
		setListeners();
		getHotSearchData();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SearchActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SearchActivity");
		super.onResume();
	}

	private void initViews() {
		input = (EditText) findViewById(R.id.ps_search_edit);
		input.clearFocus();
		searchBtn = (Button) findViewById(R.id.btn_search);
		gridSearchHotKey = (GridView) findViewById(R.id.ps_hot_key_grid);
		gridSearchHotKey.setOnItemClickListener(this);
		layoutSearchHistory = (LinearLayout) findViewById(R.id.ps_search_history);
		layoutSearchHotKey = (LinearLayout) findViewById(R.id.ps_hot_key);
		searchResultListView = (StickyListView) findViewById(R.id.listView);
		footer = LayoutInflater.from(this).inflate(R.layout.search_shake_footer,null);
		TextView footerTitle = (TextView) footer.findViewById(R.id.search_group_title_name);
		footerTitle.setText("摇佳片");
		ImageView footerIcon = (ImageView) footer.findViewById(R.id.search_group_left_pic);
		footerIcon.setImageResource(R.drawable.recommend_shake_icon);
		searchResultListView.addFooterView(footer);
		initLoadingLayout();
		searchListAdapter = new SearchResultAdapter(this, searchList);
		searchResultListView.setAdapter(searchListAdapter);
		hideSearchResltLayout();
		shakeBtn = (Button) findViewById(R.id.search_shake);
	}

	private void setListeners() {
		searchBtn.setOnClickListener(this);
		shakeBtn.setOnClickListener(this);
		footer.setOnClickListener(this);
		findViewById(R.id.ps_search_delete).setOnClickListener(this);
		searchResultListView.setOnItemClickListener(onItemClickListener);
		input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_SEARCH
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					startSearch();
					return true;
				}
				return false;
			}
		});
	}

	private String mKeyWord;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			hideSoftPad();
			close();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startSearch() {
		String inputStr = input.getText().toString();
		if (StringUtils.isNotEmpty(inputStr)) {
			hideSoftPad();
			mKeyWord = inputStr;
			search(0, LIST_COUNT, inputStr);
		} else {
			DialogUtil.alertToast(getApplicationContext(), "请先输入关键字");
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ps_search_delete) {
			input.setText("");
		} else if (v.getId() == R.id.btn_search) {
			startSearch();
		} else if (v.getId() == R.id.search_shake){
			startActivity(new Intent(this,ShakeActivity.class));
		} else if (v.getId() == R.id.search_shake_footer_layout){
			startActivity(new Intent(this,ShakeActivity.class));
		}

	}

	// 搜索历史网格
	private LinearLayout layoutSearchHistory;
	// 热搜词网格
	private LinearLayout layoutSearchHotKey;
	private GridView gridSearchHotKey;

	private void getHotSearchData() {
		VolleyProgramRequest.hotSearch(this, this);
	}

	private ArrayList<KeyWordData> keywords = new ArrayList<KeyWordData>();

	private void updateHotPlayGrid() {
		ArrayList<KeyWordData> temp = keywords;
		keywords = new ArrayList<KeyWordData>();
		for (KeyWordData item : temp) {
			if (item.name.endsWith("午夜佳片"))
				continue;
			keywords.add(item);
		}

		if (keywords != null) {
			gridSearchHotKey.setAdapter(new HotKeyAdapter(this, keywords));
			int height = (keywords.size() / 2 + 1)
					* CommonUtils.dip2px(this, 30);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, height);
			gridSearchHotKey.setLayoutParams(params);
			gridSearchHotKey.setNumColumns(2);
			gridSearchHotKey.setColumnWidth(CommonUtils.dip2px(this, 80));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		input.setText(keywords.get(position).name);
		startSearch();

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

	private void search(int first, int count, String keyWord) {
		if (first == 0) {
			searchList.clear();
			showLoadingLayout();
			shakeBtn.setVisibility(View.GONE);
		}
		VolleyProgramRequest.search(this, this, keyWord, first, count);
	}

	private void updateSearchList(ArrayList<VodProgramData> programList) {
		if (programList != null) {
			ArrayList<VodProgramData> temp = programList;
			programList = new ArrayList<VodProgramData>();
			for (VodProgramData item : temp) {
				if (item.name.endsWith("午夜佳片"))
					continue;
				if (item.name.contains("点击查看") && temp.size() < LIST_COUNT) {
					continue;
				}
				programList.add(item);
			}
			searchList.addAll(programList);
			if (searchList.size() == 0) {
				showEmptyLayout("暂无结果");
				searchResultListView.setVisibility(View.GONE);
				shakeBtn.setVisibility(View.VISIBLE);
			}
			searchListAdapter.notifyDataSetChanged();
		}
	}

	private void hideSearchResltLayout() {
		layoutSearchHistory.setVisibility(View.GONE);
		layoutSearchHotKey.setVisibility(View.VISIBLE);
		searchResultListView.setVisibility(View.GONE);
	}

	private void showSearchResultLayout() {
		layoutSearchHistory.setVisibility(View.GONE);
		layoutSearchHotKey.setVisibility(View.GONE);
		searchResultListView.setVisibility(View.VISIBLE);
	}

	private void hideSoftPad() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			hideSoftPad();
			close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void close() {
		finish();
	}

	private static final int LIST_COUNT = 10;
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int position = (int) arg3;
			VodProgramData programData = searchList.get(position);
			if (programData.subSearchType == VodProgramData.SEARCH_TYPE_PROGRAM) {
				if (TextUtils.isEmpty(programData.id)) {
					openMoreActivity(programData, "节目影片");
				} else {
					openProgramDetailActivity(programData.id,
							programData.topicId,programData.ptype);
				}
			} else {
				if (TextUtils.isEmpty(programData.subId)) {
					openMoreActivity(programData, "综合视频");
				} else {
					openPlayActivity(programData);
				}
			}
		}
	};

	private void openMoreActivity(VodProgramData programData, String typeName) {
		Intent intent = new Intent();
		intent.setClass(SearchActivity.this, SearchMoreActivity.class);
		intent.putExtra("searchtxt", mKeyWord);
		intent.putExtra("typeName", typeName);
		intent.putExtra("searchType", programData.subSearchType);
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
		intent.putExtra("playType", PlayerActivity.VOD_PLAY);
		if (!TextUtils.isEmpty(vpd.subHighPath)) {
			intent.putExtra(PlayerActivity.TAG_INTENT_HIGHURL, vpd.subHighPath);
		}
		if (!TextUtils.isEmpty(vpd.subSuperPath)) {
			intent.putExtra(PlayerActivity.TAG_INTENT_SUPERURL,
					vpd.subSuperPath);
		}
		long topicId = 0;
		if (!TextUtils.isEmpty(vpd.topicId)) {
			topicId = Long.parseLong(vpd.topicId);
		}
		intent.putExtra("topicId", topicId);
		intent.putExtra("title", vpd.subName);
		intent.putExtra("id", Integer.parseInt(vpd.subProgramId));
		intent.putExtra(PlayerActivity.TAG_INTENT_SUBID, Integer.parseInt(vpd.subId));
		if (!TextUtils.isEmpty(vpd.subVideoPath)) {// 不破解
			intent.setClass(this, WebAvoidPicActivity.class);
			intent.putExtra("path", vpd.subVideoPath);
			intent.putExtra("url", vpd.subVideoPath);
			intent.putExtra("where",2);
			startActivity(intent);
		} else if (!TextUtils.isEmpty(vpd.subUrl)) {// 破解
			intent.setClass(this, WebAvoidActivity.class);
			intent.putExtra("url", vpd.subUrl);
			intent.putExtra("where",2);
			startActivity(intent);
		}

	}

	@Override
	public void getHotKeyList(int errCode, ArrayList<KeyWordData> keyList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			keywords = keyList;
			updateHotPlayGrid();
		}

	}

	@Override
	public void getSearchProgramList(int errCode, int totalCount,
			ArrayList<VodProgramData> programList) {
		hideLoadingLayout();
		showSearchResultLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			searchList.clear();
			updateSearchList(programList);
		} else {
			showErrorLayout();
			DialogUtil.alertToast(getApplicationContext(), "搜索失败");
		}
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.hotSearch);
		VolleyHelper.cancelRequest(Constants.searchProgram);
	}

}
