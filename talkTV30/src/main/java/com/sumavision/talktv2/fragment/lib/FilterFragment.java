package com.sumavision.talktv2.fragment.lib;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.LibNormalAdapter;
import com.sumavision.talktv2.adapter.LibNormalVerticalAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.FlowLayout;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.fragment.BaseFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.FilterListParser;
import com.sumavision.talktv2.http.json.FilterListRequest;
import com.sumavision.talktv2.http.json.FilterParser;
import com.sumavision.talktv2.http.json.FilterRequest;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import java.util.ArrayList;

/**
 * 筛选页
 * 
 * @author suma-hpb
 * 
 */
public class FilterFragment extends BaseFragment implements
		OnRefreshListener2<ListView>, OnScrollListener {
	public static FilterFragment newInstance(int programType, int columnId) {
		FilterFragment fragment = new FilterFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_filter);
		bundle.putInt("programType", programType);
		bundle.putInt("columnId", columnId);
		fragment.setArguments(bundle);
		return fragment;
	}

	private PullToRefreshListView ptrListView;
	private LinearLayout filterLayout;
	private LinearLayout filterLayoutBtn;
	private RelativeLayout rlayoutFilterDesc;
	private ScrollView scrollView;
	private Button refilterBtn;
	private TextView filterTxt;

	private Button ensureBtn;

	private int screenWidth;
	private int programType;
	private int columnId;
	private boolean needLoadData = true;

	private String[] choosen = new String[] { "全部", "全部", "全部", "全部" };
	private int[] choosenPos = new int[4];

	private LibNormalAdapter programAdapter;
	private LibNormalVerticalAdapter verticalAdapter;
	private ArrayList<ArrayList<VodProgramData>> programs = new ArrayList<ArrayList<VodProgramData>>();

	private ArrayList<String> listType;
	private ArrayList<String> listAge;
	private ArrayList<String> listCountry;
	private ArrayList<String> listActor;

	private Resources mResources;
	private ColorStateList mColorState;
    private int columnNum = 2;
    private int requestCount = 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		programType = bundle.getInt("programType");
		columnId = bundle.getInt("columnId");

		screenWidth = AppUtil.getScreenWidth(getActivity());

		mResources = getActivity().getResources();
		mColorState = mResources.getColorStateList(R.color.color_filter_text);

	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		ptrListView = (PullToRefreshListView) view
				.findViewById(R.id.list_program);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		filterLayout = (LinearLayout) view
                .findViewById(R.id.rlayout_filter_choice);
		filterLayoutBtn = (LinearLayout) view
                .findViewById(R.id.layout_filter_btn);
		scrollView = (ScrollView) view.findViewById(R.id.filter_scrollview);
		rlayoutFilterDesc = (RelativeLayout) view
				.findViewById(R.id.rlayout_filter_desc);
		rlayoutFilterDesc.setVisibility(View.GONE);
		refilterBtn = (Button) view.findViewById(R.id.btn_refilter);
		refilterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reset();
			}
		});
		filterTxt = (TextView) view.findViewById(R.id.tv_filter_intro);
		filterTxt.setMaxWidth(screenWidth * 3 / 4 - 20);
		ensureBtn = (Button) view.findViewById(R.id.filter_ensure);
		ensureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLegal(choosen)) {
                    showLoadingLayout();
                    request(0, requestCount);
                } else {
                    Toast.makeText(mActivity, "请至少选择一项", Toast.LENGTH_SHORT)
                            .show();
				}
			}
		});

		if (isVertical(programType)){
			verticalAdapter = new LibNormalVerticalAdapter(mActivity,programType,programs);
			ptrListView.setAdapter(verticalAdapter);
            columnNum = 3;
            requestCount = 21;
		}else {
			programAdapter = new LibNormalAdapter(mActivity, programType, programs);
			ptrListView.setAdapter(programAdapter);
        }
        ptrListView.setMode(Mode.DISABLED);
		ptrListView.setOnScrollListener(this);
		// ptrListView.setOnRefreshListener(this);
		ptrListView.setVisibility(View.GONE);
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
			requestList();
		}
	}

	private View initFilterView(ArrayList<String> list, final String title) {
		if (list.size() <= 1) {
			return null;
		}
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.item_flowlayout, null);
		FlowLayout mFlowLayout = (FlowLayout) view
				.findViewById(R.id.flow_layout);
		LinearLayout parent = (LinearLayout) view
				.findViewById(R.id.parent_flow);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		TextView titleTxt = new TextView(getActivity());
		params.setMargins(0, 0, 0, 15);
		titleTxt.setLayoutParams(params);
		titleTxt.setText(title);
		titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		titleTxt.setTextColor(getResources().getColor(R.color.light_black));
		parent.addView(titleTxt, 0);

		params.setMargins(screenWidth / 40, screenWidth / 40, screenWidth / 40,
				screenWidth / 40);
		ArrayList<TextView> listTxt = new ArrayList<TextView>();

		for (int i = 0; i < list.size(); i++) {
			TextView text = new TextView(getActivity());
			text.setLayoutParams(params);
			text.setBackgroundResource(R.drawable.selector_filter_text);
			text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			text.setPadding(3, 3, 3, 3);
			text.setGravity(Gravity.CENTER);
			text.setTextColor(mColorState);
			text.setText(list.get(i));
			if (i == 0) {
				text.setSelected(true);
			}
			MyOnClickListener listener = new MyOnClickListener(i, title,
					listTxt);
			text.setOnClickListener(listener);
			listTxt.add(text);
			mFlowLayout.addView(text);
		}
		return view;
	}

	private class MyOnClickListener implements OnClickListener {
		int pos;
		String currTitle;
		ArrayList<TextView> listTxt;

		public MyOnClickListener(int pos, String title,
				ArrayList<TextView> listTxt) {
			this.pos = pos;
			this.currTitle = title;
			this.listTxt = listTxt;
		}

		private void setPosition(int typePos, String detail) {
			if (choosenPos[typePos] == pos) {
				return;
			}
			listTxt.get(choosenPos[typePos]).setSelected(false);
			choosen[typePos] = detail;
			choosenPos[typePos] = pos;
		}

		@Override
		public void onClick(View v) {
			v.setSelected(true);
			String detail = ((TextView) v).getText().toString();
			if (currTitle.equals(getString(R.string.filter_type))) {
				setPosition(0, detail);
			} else if (currTitle.equals(getString(R.string.filter_age))) {
				setPosition(1, detail);
			} else if (currTitle.equals(getString(R.string.filter_country))) {
				setPosition(2, detail);
			} else if (currTitle.equals(getString(R.string.filter_actor))) {
				setPosition(3, detail);
			}
		}
	}

	private int resultCount;
	FilterParser filterParser = new FilterParser();
	private ArrayList<VodProgramData> listProgram = new ArrayList<VodProgramData>();

	private void request(int first, int count) {
        VolleyHelper.post(
                new FilterRequest(programType, choosen, first, count).make(),
                new ParseListener(filterParser) {
                    @Override
                    public void onParse(BaseJsonParser parser) {
                        if (getActivity() != null
                                && !getActivity().isFinishing()) {
                            ptrListView.onRefreshComplete();
                            if (filterParser.errCode == JSONMessageType.SERVER_CODE_OK) {
                                resultCount = filterParser.count;
                                listProgram.addAll(filterParser.listProgram);
                                if (filterParser.listProgram.size() < requestCount) {
                                    // ptrListView.setMode(Mode.DISABLED);
                                    overloading = true;
                                } else {
                                    overloading = false;
                                }
                                update();
                            } else {
                                hideLoadingLayout();
                                Toast.makeText(getActivity(),
                                        filterParser.errMsg, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
					}
				}, this);
	}

	FilterListParser listParser = new FilterListParser();

	private void requestList() {
		showLoadingLayout();
        VolleyHelper.post(new FilterListRequest(columnId).make(),
                new ParseListener(listParser) {
                    @Override
                    public void onParse(BaseJsonParser parser) {
                        if (getActivity() != null
                                && !getActivity().isFinishing()) {
                            if (listParser.errCode == JSONMessageType.SERVER_CODE_OK) {
                                needLoadData = false;
                                listType = listParser.listType;
                                listAge = listParser.listAge;
                                listCountry = listParser.listCountry;
                                listActor = listParser.listActor;
                                updateList();
                            } else {
                                showErrorLayout();
                                ensureBtn.setVisibility(View.GONE);
                            }
                        }
					}
				}, this);
	}

	private void updateList() {
		String data = "全部";
		updateFilterView(listType, data, getString(R.string.filter_type));
		updateFilterView(listAge, data, getString(R.string.filter_age));
		updateFilterView(listCountry, data, getString(R.string.filter_country));
		updateFilterView(listActor, data, getString(R.string.filter_actor));
		ensureBtn.setVisibility(View.VISIBLE);
		hideLoadingLayout();
	}

	private void updateFilterView(ArrayList<String> strings, String data,
			String type) {
		if (strings != null) {
			strings.add(0, data);
			View v = initFilterView(strings, type);
			if (v != null) {
				filterLayout.addView(v);
			}
		}
	}

	private void reset() {
		// ptrListView.setMode(Mode.PULL_FROM_END);
		// ptrListView.requestFocusFromTouch();
		// ptrListView.getRefreshableView().setSelection(0);
		listProgram.clear();
		programs.clear();
		if (isVertical(programType)){
			verticalAdapter.notifyDataSetChanged();
		} else {
			programAdapter.notifyDataSetChanged();
		}
		ptrListView.setVisibility(View.GONE);
		rlayoutFilterDesc.setVisibility(View.GONE);
		scrollView.setVisibility(View.VISIBLE);
		filterLayoutBtn.setVisibility(View.VISIBLE);
		resultCount = 0;
	}

	private boolean isLegal(String[] strs) {
		for (String s : strs) {
			if (!s.equals("全部")) {
				return true;
			}
		}
		return false;
	}

	private void update() {
		hideLoadingLayout();
		rlayoutFilterDesc.setVisibility(View.VISIBLE);
		scrollView.setVisibility(View.GONE);
		filterLayoutBtn.setVisibility(View.GONE);
		ptrListView.setVisibility(View.VISIBLE);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < choosen.length; i++) {
			if (!choosen[i].equals("全部")) {
				builder.append(choosen[i]);
				builder.append("/");
			}
		}
		builder.deleteCharAt(builder.length() - 1);
		filterTxt.setText(builder + "(" + resultCount + "部)");
		updateListView(filterParser.listProgram);
	}

	private void updateListView(ArrayList<VodProgramData> programList) {
		for (int i = 0; i < programList.size(); i += columnNum) {
			ArrayList<VodProgramData> list = new ArrayList<VodProgramData>();
			list.add(programList.get(i));
			if (i + 1 < programList.size()) {
				list.add(programList.get(i + 1));
			}
            if (isVertical(programType) && i+2<programList.size()){
                list.add(programList.get(i+2));
            }
			programs.add(list);
		}
		if (isVertical(programType)){
			verticalAdapter.notifyDataSetChanged();
		} else {
			programAdapter.notifyDataSetChanged();
		}
		loading = false;
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		showLoadingLayout();
		requestList();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		request(listProgram.size(), requestCount);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyRequest.cancelRequest(Constants.vaultColumnsSearch);
		VolleyRequest.cancelRequest(Constants.filterList);
	}

	private boolean loading = false;
	private boolean overloading = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1
					&& overloading) {
				ToastHelper.showToast(getActivity(), "已经滑动到底部");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view.getLastVisiblePosition() > (totalItemCount - 3) && !loading
				&& !overloading && totalItemCount > 0) {
			loading = true;
			request(listProgram.size(), requestCount);
		}
	}
}
