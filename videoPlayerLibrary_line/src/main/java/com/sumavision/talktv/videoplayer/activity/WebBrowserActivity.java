package com.sumavision.talktv.videoplayer.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.R;

/**
 * webview 通用页面<br>
 * 电视粉协议、首页推荐导航广告
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class WebBrowserActivity extends CompatBaseActivity {
	WebView web;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_webbrowser);
		showOrHideActionBar();
		String title = getIntent().getStringExtra("title");
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(title);
		} else {
			titleTextView.setText(title);
			refreshBtn.setVisibility(View.GONE);
		}
		web = (WebView) findViewById(R.id.wbb);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		web.invokeZoomPicker();
		web.getSettings().setSupportZoom(true);
		web.getSettings().setBuiltInZoomControls(true);

		boolean b = isBigDisplay();
		if (b) {
			web.setInitialScale(50);
		} else {
			web.setInitialScale(32);
		}
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setAllowFileAccess(true);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

		});
		int screenDensity = getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.FAR;
			break;
		}

		web.getSettings().setDefaultZoom(zoomDensity);
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;
		web.measure(width, height);
		if (getIntent().getIntExtra("where",0) == 5){
			web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
			web.getSettings().setUseWideViewPort(true);
		}
		String url = getIntent().getStringExtra("url");
		if (url != null)
			web.loadUrl(url);
		else {
			Toast.makeText(getApplicationContext(), "页面加载失败!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private boolean isBigDisplay() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		if (width > 320) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		super.onPause();
		web.onPause();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		web.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
