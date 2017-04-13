package com.sumavision.talktv2.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.EmergencyAdapter;
import com.sumavision.talktv2.bean.EmergencyFocusData;
import com.sumavision.talktv2.bean.EmergencyObjectData;
import com.sumavision.talktv2.bean.EmergencyZoneData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.Interactive;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnEmergencyZoneListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 竞猜资讯页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GuessingNewsFragment extends BaseFragment implements
		OnClickListener, OnEmergencyZoneListener {

	private PullToRefreshListView listView;
	private EmergencyAdapter adapter;
	private RelativeLayout errLayout;
	private ProgressBar loadingBar;
	private TextView errTxt;

	public static GuessingNewsFragment newInstance() {
		GuessingNewsFragment fragment = new GuessingNewsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_guessing_news);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("GuessingNewsFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("GuessingNewsFragment");
	}

	@Override
	protected void initViews(View view) {
		listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
		listView.setMode(Mode.DISABLED);listView.setPullToRefreshOverScrollEnabled(false);
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		loadingBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		errTxt = (TextView) rootView.findViewById(R.id.err_text);
		errTxt.setOnClickListener(this);

	}

	private void showLoadingayout() {
		errLayout.setVisibility(View.VISIBLE);
		loadingBar.setVisibility(View.VISIBLE);
		errTxt.setVisibility(View.GONE);
	}

	private void showErrayout() {
		errLayout.setVisibility(View.VISIBLE);
		loadingBar.setVisibility(View.GONE);
		errTxt.setVisibility(View.VISIBLE);
	}

	private EmergencyZoneData emergencyZoneData;
	private boolean needLoadData = true;

	public void getZoneData() {
		if (needLoadData) {
			showLoadingayout();
			VolleyProgramRequest.emergencyZone(this, this,
					Interactive.getInstance().zoneId);
		}

	}

	List<EmergencyObjectData> listobject = new ArrayList<EmergencyObjectData>();
	List<EmergencyFocusData> list;

	private void updateEmergencyList() {
		listobject = emergencyZoneData.emergencyObject;
		adapter = new EmergencyAdapter(getActivity(), listobject);
		listView.setVisibility(View.VISIBLE);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.err_text) {
			getZoneData();
		}

	}

	@Override
	public void getEmergencyZone(int errCode, EmergencyZoneData emergencyZone) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			emergencyZoneData = emergencyZone;
			errLayout.setVisibility(View.GONE);
			needLoadData = false;
			updateEmergencyList();
			UserNow.current().zoneId = Interactive.getInstance().zoneId;
		} else {
			showErrayout();
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
		VolleyHelper.cancelRequest(Constants.zoneDetail);
	}

}
