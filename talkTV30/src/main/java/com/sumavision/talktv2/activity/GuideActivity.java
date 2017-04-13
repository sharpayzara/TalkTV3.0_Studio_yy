package com.sumavision.talktv2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.PreferencesUtils;

public class GuideActivity extends Activity implements OnClickListener {
	public static final int GUIDE_MAIN = 1;
	public static final int GUIDE_LIVE = 2;
	public static final int GUIDE_LIB_DETAIL = 3;
	RelativeLayout guideLayout, liveGuideLayout, libGuideLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		int type = getIntent().getIntExtra("type", 0);
		guideLayout = (RelativeLayout) findViewById(R.id.rlayout_guide);
		guideLayout.setOnClickListener(this);
		liveGuideLayout = (RelativeLayout) findViewById(R.id.rlayout_live_guide);
		liveGuideLayout.setOnClickListener(this);
		libGuideLayout = (RelativeLayout) findViewById(R.id.rlayout_lib_guide);
		libGuideLayout.setOnClickListener(this);
		if (type == GUIDE_MAIN) {
			guideLayout.setVisibility(View.VISIBLE);
		} else if (type == GUIDE_LIVE) {
			liveGuideLayout.setVisibility(View.VISIBLE);
		} else {
			libGuideLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlayout_guide:
			PreferencesUtils.putBoolean(this, null, "mainGuide", true);
			finish();
			break;
		case R.id.rlayout_live_guide:
			PreferencesUtils.putBoolean(this, null, "liveGuide", true);
			finish();
			break;
		case R.id.rlayout_lib_guide:
			PreferencesUtils.putBoolean(this, null, "libGuide", true);
			finish();
			break;
		default:
			break;
		}

	}

}
