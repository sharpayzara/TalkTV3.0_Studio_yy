package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.PayHistoryAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PayHistoryData;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.epay.OnPayHistoryListener;
import com.sumavision.talktv2.http.request.VolleyEPayRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 充值记录页面
 * 
 * @author suma-hpb
 * 
 */
public class PayHistoryActivity extends BaseActivity implements
		OnPayHistoryListener {

	private ListView paylist;
	private TextView emptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_history);
		initLoadingLayout();
		getSupportActionBar().setTitle(getString(R.string.pay_history));
		paylist = (ListView) findViewById(R.id.lstView_history);
		emptyView = (TextView) findViewById(R.id.listview_history_empty);
		paylist.setEmptyView(emptyView);
		getPayHistroy();
	}

	PayHistoryAdapter historyAdapter;
	private ArrayList<PayHistoryData> historyDatas = new ArrayList<PayHistoryData>();

	public void getPayHistroy() {
		showLoadingLayout();
		VolleyEPayRequest.getPayHistory(this, this);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("PayHistoryActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("PayHistoryActivity");
		super.onPause();
	}

	@Override
	public void onUpdatePayHistory(int errCode,
			ArrayList<PayHistoryData> historyList) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			this.historyDatas = historyList;
			historyAdapter = new PayHistoryAdapter(this, historyDatas);
			paylist.setAdapter(historyAdapter);
		} else {
			showErrorLayout();
		}

	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.payRecordList);
	}

}
