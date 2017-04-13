package com.sumavision.talktv.videoplayer.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.utils.SmartBarUtils;
import com.sumavision.talktv2.utils.StatusBarUtils;

public class CompatBaseActivity extends SherlockFragmentActivity implements
		OnClickListener {
	protected RelativeLayout titleLayout;
	protected WebView web;
	protected TextView titleTextView;
	protected ImageView back, refreshBtn;

	public void showOrHideActionBar() {
		titleLayout = (RelativeLayout) findViewById(R.id.navigator_layout);
		back = (ImageView) findViewById(R.id.back);
		refreshBtn = (ImageView) findViewById(R.id.refreshweb);
		back.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
		titleTextView = (TextView) findViewById(R.id.title);
		Configuration config = getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (getSupportActionBar() != null) {
				getSupportActionBar().hide();
			}
			titleLayout.setVisibility(View.VISIBLE);
		} else {
			if (getSupportActionBar() != null){
				if (SmartBarUtils.hasSmartBar()) {
					SmartBarUtils.setBackIcon(getActionBar(), getResources()
							.getDrawable(R.drawable.ic_action_back));
					SmartBarUtils.setActionBarViewCollapsable(getActionBar(), false);
					getSupportActionBar().setDisplayUseLogoEnabled(false);
					getSupportActionBar().setDisplayShowHomeEnabled(false);
					getSupportActionBar().setDisplayHomeAsUpEnabled(false);
				} else {
					getSupportActionBar().setDisplayUseLogoEnabled(true);
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					getSupportActionBar().setLogo(R.drawable.ic_action_back);
					getSupportActionBar().setBackgroundDrawable(
							getResources().getDrawable(R.drawable.navigator_bg));
				}
			}
		}
		StatusBarUtils.setImmerseTheme(this, R.drawable.navigator_bg);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (SmartBarUtils.hasSmartBar()) {
			getWindow().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back) {
			finish();
		} else if (v.getId() == R.id.refreshweb) {
			web.reload();
		}

	}
}
