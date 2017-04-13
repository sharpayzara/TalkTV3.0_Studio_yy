package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.RecommendActivityActivity;
import com.sumavision.talktv2.activity.ShoppingHomeActivity;
import com.sumavision.talktv2.adapter.FoundAdapter;
import com.sumavision.talktv2.bean.DiscoveryData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.listener.OnDiscoveryDetailListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现页
 * 
 * @author yanzhidan
 * 
 */
public class FoundFragment extends BaseFragment implements OnClickListener, OnDiscoveryDetailListener, OnRefreshListener<ListView> {

	private PullToRefreshListView foundListView;
	private FoundAdapter adapter;
	private boolean needLoadData = true;

	public static FoundFragment newInstance() {
		FoundFragment fragment = new FoundFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_found);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		needLoadData = true;

		foundListView = (PullToRefreshListView) view.findViewById(R.id.list_found);
		foundListView.setMode(Mode.PULL_FROM_START);
		foundListView.setPullToRefreshOverScrollEnabled(false);
		foundListView.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.transparent_background));
		foundListView.getRefreshableView().setSelector(getResources().getDrawable(R.drawable.transparent_background));
		foundListView.setOnRefreshListener(this);
		
		getDiscoveryDetail();
	}

	private void getDiscoveryDetail() {
		if (needLoadData) {
			showLoadingLayout();
			VolleyRequest.getDiscoveryDetail(this, this);
		}
	}

	private void initUi(ArrayList<DiscoveryData> datas) {
		adapter = new FoundAdapter(getActivity(), datas);
		foundListView.setAdapter(adapter);
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		showLoadingLayout();
		getDiscoveryDetail();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_shop:
			MobclickAgent.onEvent(getActivity(), "Fxlipin");
			mActivity.startActivity(new Intent(mActivity, ShoppingHomeActivity.class));
			break;
		case R.id.img_badge:
			MobclickAgent.onEvent(getActivity(), "Fxchoujiang");
			mActivity.startActivity(new Intent(mActivity, RecommendActivityActivity.class));
			break;

		default:
			break;
		}

	}

	@Override
	public void onDiscoveryDetail(int errCode, ArrayList<DiscoveryData> datas) {
		if (getActivity() != null && !getActivity().isFinishing()) {
			if (foundListView != null) {
				foundListView.onRefreshComplete();
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK && datas != null) {
				needLoadData = false;
				hideLoadingLayout();
				initUi(datas);
			} else {
				showErrorLayout();
			}
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		needLoadData = true;
		getDiscoveryDetail();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyRequest.cancelRequest(Constants.discoveryDetail);
	}

}
