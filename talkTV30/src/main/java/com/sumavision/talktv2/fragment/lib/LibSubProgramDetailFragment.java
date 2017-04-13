package com.sumavision.talktv2.fragment.lib;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.SubProgramAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.fragment.BaseFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.LibNormalDetailParser;
import com.sumavision.talktv2.http.json.LibNormalDetailRequest;
import com.sumavision.talktv2.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 子节目某一分类
 * 
 * @author suma-hpb
 * 
 */
public class LibSubProgramDetailFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnItemClickListener, OnScrollListener {
	public static LibSubProgramDetailFragment newInstance(int columnId, int programId, int contentType) {
		LibSubProgramDetailFragment fragment = new LibSubProgramDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_lib_sub_detail);
		bundle.putInt("columnId", columnId);
		bundle.putInt("programId", programId);
		bundle.putInt("contentType", contentType);
		fragment.setArguments(bundle);
		return fragment;
	}

	private PullToRefreshListView ptrListView;
	private boolean needLoadData = true;
	private int columnId;
	private int programId;
	private int contentType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		columnId = getArguments().getInt("columnId");
		programId = getArguments().getInt("programId");
		contentType = getArguments().getInt("contentType");
	}

	SubProgramAdapter subAdapter;
	List<VodProgramData> programList = new ArrayList<VodProgramData>();

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		ptrListView = (PullToRefreshListView) view.findViewById(R.id.list_sub_program);
		ptrListView.setMode(Mode.PULL_FROM_START);
		ptrListView.setOnScrollListener(this);
		ptrListView.setOnItemClickListener(this);
		ptrListView.setOnRefreshListener(this);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		subAdapter = new SubProgramAdapter(mActivity, programList);
		ptrListView.setAdapter(subAdapter);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			doRequest();
		}
	}

	private void doRequest() {
		if (needLoadData) {
			request(0, 20, true);
		}
	}

	LibNormalDetailParser libParser = new LibNormalDetailParser();

	private void request(int first, int count, final boolean refresh) {
		VolleyHelper.post(new LibNormalDetailRequest(columnId, programId, contentType, first, count).make(), new ParseListener(libParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					ptrListView.onRefreshComplete();
					if (libParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						if (refresh) {
							programList.clear();
						}
						programList.addAll(libParser.listSub);
						if (libParser.listSub.size() < 20) {
							// ptrListView.setMode(Mode.PULL_FROM_START);
							overloading = true;
						} else {
							overloading = false;
						}
						if (programList != null && programList.size() > 0) {
							update();
						} else {
							showEmptyLayout("暂无数据");
						}
					} else {
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	private void update() {
		hideLoadingLayout();
		subAdapter.notifyDataSetChanged();
		loading = false;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		request(0, 20, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		request(0, 20, true);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		VodProgramData data = programList.get((int) id);
		if (TextUtils.isEmpty(data.subVideoPath) && TextUtils.isEmpty(data.playUrl)) {
			Toast.makeText(mActivity, "播放路径为空", Toast.LENGTH_SHORT).show();
			return;
		}
		//311 改为全屏播放
		Intent intent = new Intent(getActivity(), PlayerActivity.class);
		intent.putExtra("id", Integer.parseInt(data.programId));
		intent.putExtra("subid", Integer.parseInt(data.id));
		intent.putExtra("playType", 2);
		intent.putExtra("isHalf", true);
		intent.putExtra("where",2);
		startActivity(intent);
//		Intent intent = new Intent();
//		if (!TextUtils.isEmpty(data.subVideoPath)) {
//			intent.setClass(mActivity, WebAvoidPicActivity.class);
//			intent.putExtra("path", data.subVideoPath);
//			intent.putExtra("url", data.subVideoPath);
//			intent.putExtra("where",2);
//		} else {
//			intent.setClass(mActivity, WebAvoidActivity.class);
//			intent.putExtra("url", data.playUrl);
//			intent.putExtra("where",2);
//		}
//		long topicId = 0;
//		if (!TextUtils.isEmpty(data.topicId)) {
//			topicId = Long.parseLong(data.topicId);
//		}
//		intent.putExtra("topicId", topicId);
//		intent.putExtra("hideFav", true);
//		intent.putExtra("id", Integer.parseInt(data.id));
//		intent.putExtra("playType", PlayerActivity.VOD_PLAY);
//		intent.putExtra("title", data.name);
//		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.vaultColumnsContentTagDetail);
	}

	private boolean loading = false;
	private boolean overloading = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1 && overloading) {
				ToastHelper.showToast(getActivity(), "已经滑动到底部");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view.getLastVisiblePosition() > (totalItemCount - 3) && !loading && !overloading && totalItemCount > 0) {
			loading = true;
			if (programList != null && programList.size() > 0) {
				request(programList.size(), 20, false);
			}
		}
	}

}
