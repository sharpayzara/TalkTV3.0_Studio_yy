package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.InteractiveDetailActivity;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.adapter.InteractiveActivityAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveActivityListListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.widget.StickyHeadersRefreshListView;
import com.sumavision.talktv2.widget.StickyHeadersRefreshListView.OnStickyRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 互动列表页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveActivityListFragment extends BaseFragment implements
		OnItemClickListener, OnStickyRefreshListener, OnClickListener,
		OnInteractiveActivityListListener {

	public static InteractiveActivityListFragment newInstance() {
		InteractiveActivityListFragment fragment = new InteractiveActivityListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_guessing);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("InteractiveActivityListFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("InteractiveActivityListFragment");
	}
	private StickyHeadersRefreshListView listView;

	private RelativeLayout errLayout;

	private ProgressBar loadingProgress;

	private TextView errTxt;

	private RelativeLayout emptyView;

	private ArrayList<InteractiveActivity> guessList = new ArrayList<InteractiveActivity>();

	private InteractiveActivityAdapter guessingAdapter;

	@Override
	protected void initViews(View view) {
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		loadingProgress = (ProgressBar) rootView.findViewById(R.id.progress);
		errTxt = (TextView) rootView.findViewById(R.id.err_text);
		errTxt.setOnClickListener(this);
		emptyView = (RelativeLayout) rootView.findViewById(R.id.empty_tip);
		listView = (StickyHeadersRefreshListView) rootView
				.findViewById(R.id.guessing_list);
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(this);

	}

	private boolean isLoadMore;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	private boolean firstLoad = true;

	public void loadData() {
		if (firstLoad) {
			getGuessingData(0, 20);
		}
	}

	private void getGuessingData(int start, int count) {
		if (guessList.size() == 0) {
			showLoadingLayout();
		}
		if (start == 0) {
			isLoadMore = false;
		} else {
			isLoadMore = true;
		}
		VolleyInteractionRequest.interactiveActivityList(this, start, count,
				this);
	}

	protected void showLoadingLayout() {
		errLayout.setVisibility(View.VISIBLE);
		loadingProgress.setVisibility(View.VISIBLE);
		errTxt.setVisibility(View.GONE);
	}

	protected void showErrLayout() {
		errLayout.setVisibility(View.VISIBLE);
		loadingProgress.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		errTxt.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (UserNow.current().userID > 0) {
			InteractiveActivity data = (InteractiveActivity) parent
					.getItemAtPosition(position);
			if (data.interactStatus != InteractiveActivity.INTERACTION_STATUS_NO) {
				Intent intent = new Intent(getActivity(),
						InteractiveDetailActivity.class);
				intent.putExtra("interactive", data);
				startActivity(intent);
			}
		} else {
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}

	}

	private void updateList(
			ArrayList<InteractiveActivity> mInteractiveActivityList) {
		listView.setVisibility(View.VISIBLE);
		if (getActivity() != null) {
			if (isLoadMore) {
				guessList.addAll(mInteractiveActivityList);
				guessingAdapter.notifyDataSetChanged();
				listView.onLoadMoreOver();
				if (mInteractiveActivityList.size() < 20) {
					listView.setCanLoadMore(false);
				} else {
					listView.setCanLoadMore(true);
				}
			} else {
				guessList.clear();
				guessList.addAll(mInteractiveActivityList);
				if (guessList.size() == 0) {
					emptyView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
				} else {
					guessingAdapter = new InteractiveActivityAdapter(
							getActivity(), guessList);
					listView.setAdapter(guessingAdapter);
					if (mInteractiveActivityList.size() < 20) {
						listView.setCanLoadMore(false);
					} else {
						listView.setCanLoadMore(true);
					}
				}
			}
		}

	}

	@Override
	public void onRefresh() {
		getGuessingData(0, 20);

	}

	@Override
	public void onLoadingMore() {
		isLoadMore = true;
		getGuessingData(guessList.size(), 20);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.err_text) {
			getGuessingData(0, 20);
		}

	}

	@Override
	public void OnInteractiveActivityListResult(int errCode,
			ArrayList<InteractiveActivity> mInteractiveActivityList) {
		errLayout.setVisibility(View.GONE);
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			showErrLayout();
			break;
		case JSONMessageType.SERVER_CODE_OK:
			firstLoad = false;
			updateList(mInteractiveActivityList);
			break;
		default:
			break;
		}

	}

	@Override
	public void reloadData() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.interactiveActivityList);
	}

}
