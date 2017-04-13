package com.sumavision.talktv2.fragment.lib;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mediav.ads.sdk.adcore.Mvad;
import com.mediav.ads.sdk.interfaces.IMvNativeAd;
import com.mediav.ads.sdk.interfaces.IMvNativeAdListener;
import com.mediav.ads.sdk.interfaces.IMvNativeAdLoader;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.LibNormalAdapter;
import com.sumavision.talktv2.adapter.LibNormalVerticalAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.fragment.FocusLayoutFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.LibNormalDetailParser;
import com.sumavision.talktv2.http.json.LibNormalDetailRequest;
import com.sumavision.talktv2.http.json.LibNormalParser;
import com.sumavision.talktv2.http.json.LibNormalRequest;
import com.sumavision.talktv2.utils.Constants;

import java.util.ArrayList;

/**
 * 电视剧等类型下的分类的精选页
 * 
 * @author suma-hpb
 * 
 */
public class LibNormalSelectFragment extends FocusLayoutFragment implements OnRefreshListener2<ListView>, OnScrollListener {

	public static LibNormalSelectFragment newInstance(int columnId, int programType, int contentType, int programId) {
		LibNormalSelectFragment fragment = new LibNormalSelectFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_lib_normal_detail);
		bundle.putInt("contentType", contentType);
		bundle.putInt("columnId", columnId);
		bundle.putInt("programId", programId);
		bundle.putInt("programType", programType);
		fragment.setArguments(bundle);
		return fragment;
	}

	private PullToRefreshListView ptrListView;
	private boolean needLoadData = true;
	private int contentType;
	private int columnId;
	private int programId;
	private int programType;
	private int columnNum = 2;
	private int requestCount = 20;

	// 根据请求的code决定解析对应的数据
	public static final int REQUEST_CODE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentType = getArguments().getInt("contentType");
		columnId = getArguments().getInt("columnId");
		programId = getArguments().getInt("programId");
		programType = getArguments().getInt("programType");
        if (isVertical(programType)){
            columnNum = 3;
            requestCount = 21;
        }
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		initStarsLayout();
		hideStarsLayout();
		ptrListView = (PullToRefreshListView) view.findViewById(R.id.list_program);
		ptrListView.setMode(Mode.PULL_FROM_START);
		ptrListView.setOnRefreshListener(this);
		ptrListView.setOnScrollListener(this);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		ptrListView.getRefreshableView().addHeaderView(headerView);
		if (isVertical(programType)){
			verticalAdapter = new LibNormalVerticalAdapter(mActivity,programType,programs);
			ptrListView.setAdapter(verticalAdapter);
		}else {
			normalAdapter = new LibNormalAdapter(mActivity, programType, programs);
			ptrListView.setAdapter(normalAdapter);
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			doRequest();
		}
	}

	private boolean isVertical(int type){
		if (programType == ProgramData.TYPE_MOVIE || programType == ProgramData.TYPE_TV){
			return true;
		}
		return false;
	}
	private void doRequest() {
		if (needLoadData) {
			request(0, requestCount, true);
		}
	}

	LibNormalParser libParser = new LibNormalParser(REQUEST_CODE);
	LibNormalDetailParser libDetailParser = new LibNormalDetailParser();

	private void request(int first, int count, final boolean refresh) {
		if (contentType != 0 && columnId != 0 && programType != 0
				&& programId != 0) {
			VolleyHelper.post(new LibNormalDetailRequest(columnId, programId,
					contentType, first, count).make(), new ParseListener(
						libDetailParser) {
				@Override
				public void onParse(BaseJsonParser parser) {
					if (getActivity() != null && !getActivity().isFinishing()) {
						update(refresh, libDetailParser.errCode,
								libDetailParser.listProgram,
								null);
					}
				}
			}, this);
		} else {
			VolleyHelper.post(
					new LibNormalRequest(columnId, first, count).make(),
					new ParseListener(libParser) {
						@Override
						public void onParse(BaseJsonParser parser) {
							if (getActivity() != null
									&& !getActivity().isFinishing()) {
								update(refresh, libParser.errCode,
										libParser.listProgram,
										libParser.listRecommend);
							}
						}
					}, this);
		}
	}
	
	private void update(boolean refresh, int errorCode, ArrayList<VodProgramData> libProgram,
			ArrayList<RecommendData> libRecommend) {
		ptrListView.onRefreshComplete();
		if (errorCode == JSONMessageType.SERVER_CODE_OK) {
			hideLoadingLayout();
			needLoadData = false;
			boolean hasData = false;
			if (refresh) {
				programList.clear();
				programs.clear();
				recommendList.clear();
				if (libRecommend != null && libRecommend.size() > 0) {
					recommendList.addAll(libRecommend);
				}
			}
			programList.addAll(libProgram);
			if (libProgram.size() < requestCount) {
				overloading = true;
			} else {
				overloading = false;
			}
			if (programList != null && programList.size() > 0) {
				updateListView(libProgram);
				hasData = true;
			}
			if (recommendList != null && recommendList.size() > 0) {
				updateRecommend();
				hasData = true;
			} else {
				headerView.setVisibility(View.GONE);
				ptrListView.getRefreshableView().removeHeaderView(headerView);
			}
			if (!hasData) {
				showEmptyLayout("暂无数据");
			}
		} else {
			showErrorLayout();
		}
	}

	private void updateRecommend() {
		updateStarsLayout(recommendList);
		showStarsLayout();
	}

	private LibNormalAdapter normalAdapter;
	private LibNormalVerticalAdapter verticalAdapter;
	ArrayList<ArrayList<VodProgramData>> programs = new ArrayList<ArrayList<VodProgramData>>();
	ArrayList<VodProgramData> programList = new ArrayList<VodProgramData>();
	ArrayList<RecommendData> recommendList = new ArrayList<RecommendData>();

	// FIXME
	private void updateListView(ArrayList<VodProgramData> programList) {
		for (int i = 0; i < programList.size(); i += columnNum) {
			ArrayList<VodProgramData> list = new ArrayList<VodProgramData>();
			list.add(programList.get(i));
			if (i + 1 < programList.size()) {
				list.add(programList.get(i + 1));
			}
			if (isVertical(programType) && (i+2 <programList.size())){
				list.add(programList.get(i + 2));
			}
			programs.add(list);
		}
		if (isVertical(programType)){
			verticalAdapter.notifyDataSetChanged();
		} else {
			normalAdapter.notifyDataSetChanged();
		}
		loading = false;
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		request(0, requestCount, true);
	}

	private void onRefresh() {
		if (programList != null && programList.size() > 0) {
			request(programList.size(), requestCount, false);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		request(0, requestCount, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.vaultColumnsRecommendDetail);
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
			onRefresh();
		}
	}

}
