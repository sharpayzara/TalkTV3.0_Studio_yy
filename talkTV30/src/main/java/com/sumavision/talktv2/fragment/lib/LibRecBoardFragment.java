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
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.LibBoardNormalAdapter;
import com.sumavision.talktv2.adapter.LibBoardVerticalAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.RecommendModeData;
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
import java.util.List;

/**
 * 电视剧等类型下的分类的精选页
 * 
 * @author suma-hpb
 * 
 */
public class LibRecBoardFragment extends FocusLayoutFragment implements OnRefreshListener2<ListView>, OnScrollListener {

	public static LibRecBoardFragment newInstance(int columnId, int programType, int contentType, int programId) {
		LibRecBoardFragment fragment = new LibRecBoardFragment();
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
    private ArrayList<ArrayList<VodProgramData>> datas = new ArrayList<ArrayList<VodProgramData>>();
    private ArrayList<RecommendModeData> boardDatas = new ArrayList<RecommendModeData>();

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
			verticalAdapter = new LibBoardVerticalAdapter(mActivity,programType,datas);
			ptrListView.setAdapter(verticalAdapter);
		}else {
			normalAdapter = new LibBoardNormalAdapter(mActivity, programType, datas);
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
			request(0, 5, true);
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
								libDetailParser.recLabs,
								libDetailParser.recommendDatas);
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
										libParser.recLabs,
										libParser.listRecommend);
							}
						}
					}, this);
		}
	}
	
	private void update(boolean refresh, int errorCode, ArrayList<RecommendModeData> libProgram,
			ArrayList<RecommendData> libRecommend) {
		ptrListView.onRefreshComplete();
		if (errorCode == JSONMessageType.SERVER_CODE_OK) {
			hideLoadingLayout();
			needLoadData = false;
			boolean hasData = false;
			if (refresh) {
				datas.clear();
                boardDatas.clear();
				recommendList.clear();
				if (libRecommend != null && libRecommend.size() > 0) {
					recommendList.addAll(libRecommend);
				}
			}
			if (libProgram != null && libProgram.size()>0){
//				List<List<VodProgramData>> programDatas = new ArrayList<List<VodProgramData>>();
                boardDatas.addAll(libProgram);
				for (int i=0; i<libProgram.size(); i++){
					ArrayList<VodProgramData> item = new ArrayList<VodProgramData>();
					item.add(libProgram.get(i).exchangeToVod());
					datas.add(item);
					List<VodProgramData> tempList = libProgram.get(i).hotLabelPrograms;
					if (tempList != null && tempList.size()>0){
						int size = tempList.size();
						for (int j=0; j<size; j+=columnNum){
							ArrayList<VodProgramData> list = new ArrayList<VodProgramData>();
							list.add(tempList.get(j));
							if (j+1<size){
								list.add(tempList.get(j+1));
							}
							if (isVertical(programType)){
								if (j+2<size){
									list.add(tempList.get(j+2));
								}
                            }
                            datas.add(list);
                        }
                    }
				}
				if (boardDatas.size() < 5) {
					overloading = true;
				} else {
					overloading = false;
				}
			} else {
				overloading = true;
			}

			if (datas != null && datas.size() > 0) {
				updateListView();
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

	private LibBoardNormalAdapter normalAdapter;
	private LibBoardVerticalAdapter verticalAdapter;
	ArrayList<RecommendData> recommendList = new ArrayList<RecommendData>();

	// FIXME
	private void updateListView() {
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
		request(0, 20, true);
	}

	private void onRefresh() {
		if (boardDatas != null && boardDatas.size() > 0) {
			request(boardDatas.size(), 5, false);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		request(0, 5, true);
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
