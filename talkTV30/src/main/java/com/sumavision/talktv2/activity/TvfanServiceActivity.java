package com.sumavision.talktv2.activity;

import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 充值反馈
 * 
 * @author suma-hpb
 * 
 */
public class TvfanServiceActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvfan_service);
		getSupportActionBar().setTitle("客服");
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("TvfanServiceActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("TvfanServiceActivity");
		super.onResume();
	}
}
