package com.sumavision.talktv2.fragment.lib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.kugou.fanxing.core.FanxingManager;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LibDetailActivity;
import com.sumavision.talktv2.activity.SlidingMainActivity;
import com.sumavision.talktv2.adapter.IBaseAdapter;
import com.sumavision.talktv2.bean.HotLibType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.fragment.FocusLayoutFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.HotLibParser;
import com.sumavision.talktv2.http.json.HotLibRequest;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ViewHolder;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 片库首页
 * **/
public class HotLibFragment extends FocusLayoutFragment implements
		OnItemClickListener, OnRefreshListener2<ScrollView> {

	private ArrayList<RecommendData> listRecommend = new ArrayList<RecommendData>();
	private ArrayList<HotLibType> listColumn = new ArrayList<HotLibType>();

	private StaticGridView libTypeGrid;
	private libTypeAdapter adapter;

	private PullToRefreshScrollView scrollLayout;
	private LinearLayout rcmdLayout;
	private int screenWidth;
	private boolean needLoadData = true;
	
	public static HotLibFragment newInstance() {
		HotLibFragment fragment = new HotLibFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_hot_lib);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		screenWidth = mDisplayMetrics.widthPixels;
	}
	
	boolean pageAnalytic;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			doRequest();
			pageAnalytic = true;
			MobclickAgent.onEvent(mActivity, "pianku");
			MobclickAgent.onPageStart("HotLibFragment");
		} else {
			if (pageAnalytic) {
				pageAnalytic = false;
				MobclickAgent.onPageEnd("HotLibFragment");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		if (pageAnalytic) {
			pageAnalytic = false;
			MobclickAgent.onPageEnd("HotLibFragment");
		}
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		initStarsLayout();
		hideStarsLayout();
		libTypeGrid = (StaticGridView) view.findViewById(R.id.grid_type);
		rcmdLayout = (LinearLayout) view.findViewById(R.id.rcmd_layout);
		libTypeGrid.setOnItemClickListener(this);
		libTypeGrid.setFocusable(false);
		libTypeGrid.setFocusableInTouchMode(false);
		scrollLayout = (PullToRefreshScrollView) view
				.findViewById(R.id.scroll_layout);
		scrollLayout.setMode(Mode.PULL_FROM_START);
		scrollLayout.setOnRefreshListener(this);
		scrollLayout.setVisibility(View.GONE);
		scrollLayout.setPullToRefreshOverScrollEnabled(false);

		adapter = new libTypeAdapter(mActivity, listColumn);
		libTypeGrid.setAdapter(adapter);
		rcmdLayout.addView(headerView);
	}

	public void doRequest() {
		if (needLoadData) {
			requestLib();
		}
	}

	HotLibParser hotLibParser = new HotLibParser();

	private void requestLib() {
		showLoadingLayout();
		VolleyHelper.post(new HotLibRequest().make(), new ParseListener(
				hotLibParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					scrollLayout.onRefreshComplete();
					if (hotLibParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						listColumn.clear();
						listRecommend.clear();
						listColumn.addAll(hotLibParser.listColumn);
						listRecommend.addAll(hotLibParser.listRecommend);
						onGetLibType(listColumn);
					} else {
						hideStarsLayout();
						scrollLayout.setVisibility(View.GONE);
						showErrorLayout();
					}
				}
			}
		}, this);
	}
	
	private void onGetLibType(List<HotLibType> typeList) {
		scrollLayout.setVisibility(View.VISIBLE);
		updateStarsLayout(listRecommend);
		showStarsLayout();
		adapter.notifyDataSetChanged();

		if (listRecommend == null || listRecommend.size() == 0) {
			hideStarsLayout();
			scrollLayout.setVisibility(View.GONE);
		}
		hideLoadingLayout();
	}

	class libTypeAdapter extends IBaseAdapter<HotLibType> {

		public libTypeAdapter(Context context, List<HotLibType> objects) {
			super(context, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_hot_lib, null);
			}
			ImageView iconView = ViewHolder.get(convertView,
					R.id.imagv_lib_icon);
			TextView typeTxt = ViewHolder.get(convertView, R.id.tv_lib_type);
			
			HotLibType libTpye = getItem(position);
			loadImageCacheDisk(iconView, libTpye.icon, R.drawable.aadefault);
			typeTxt.setText(libTpye.name);
			typeTxt.getLayoutParams().width = screenWidth / 3;
			iconView.getLayoutParams().width = screenWidth / 3;
			iconView.getLayoutParams().height = screenWidth / 3;
			return convertView;
		}
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		requestLib();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int arg2, long id) {
		if (parent == libTypeGrid) {
			int pos = (int) id;
			if (listColumn.get(pos).type != HotLibType.TYPE_USHOW) {
				MobclickAgent.onEvent(getActivity(), "pkjiemu",
						listColumn.get(pos).name);
				Intent intent = new Intent(getActivity(),
						LibDetailActivity.class);
				intent.putParcelableArrayListExtra("libType", listColumn);
				intent.putExtra("id", listColumn.get(pos).id);
				startActivity(intent);
			} else {
				if (getActivity() instanceof SlidingMainActivity) {
//					((SlidingMainActivity) getActivity()).uShowLaunchHall();
//					startActivity(new Intent(getActivity(),FanxingActivity.class));
					AdStatisticsUtil.adCount(getActivity(), Constants.vaultColumnAD);
					FanxingManager.goMainUi(getActivity());
				}
			}
		}
	}

	@Override
	public void onError(int code) {
		super.onError(code);
		hideStarsLayout();
		if (scrollLayout != null) {
			scrollLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		requestLib();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

	}

	public void moveToTop() {
		try {
			if (scrollLayout != null) {
				scrollLayout.getRefreshableView().scrollTo(0, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyRequest.cancelRequest(Constants.vaultRecommendDetail);
	}
}
