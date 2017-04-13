package com.sumavision.talktv.videoplayer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.utils.SmartBarUtils;

/**
 * @author hpb
 * @version 3.0
 * @description 机顶盒web
 * @changeLog
 */
public class HeZiWebActivity extends SherlockFragmentActivity {

	private WebView web;
	private ProgressBar pb;
	private final int CLSOE_PROGRESSBAR = 1;
	private final int OPEN_PROGRESSBAR = 2;
	private final int MSG_CLOSE_ACTIVITY = 3;
	private String url = null;
	private String title;

	private boolean isAgreement = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (SmartBarUtils.hasSmartBar()) {
			getWindow().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
			SmartBarUtils.setBackIcon(getActionBar(), getResources()
					.getDrawable(R.drawable.ic_action_back));
			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heziweb);
		if (getIntent().getStringExtra("url") != null) {
			url = getIntent().getStringExtra("url");
		}
		if (getIntent().getStringExtra("title") != null)
			title = getIntent().getStringExtra("title");
		getSupportActionBar().setTitle(title);
		initView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.heziweb, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent(this, DLNAHelpActivity.class);
			startActivity(intent);
			return true;
		}
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
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

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {

		web = (WebView) findViewById(R.id.wbb2);
		if (!isAgreement) {
			web.getSettings().setJavaScriptEnabled(true);
			web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			web.getSettings().setLoadWithOverviewMode(true);
			web.getSettings().setAllowFileAccess(true);
			web.invokeZoomPicker();
			web.getSettings().setSupportZoom(true);
			web.getSettings().setBuiltInZoomControls(true);
			boolean b = isBigDisplay();
			if (b) {
				web.setInitialScale(50);
			} else {
				web.setInitialScale(32);
			}
			web.setDownloadListener(new FileDownLoadListener());
			web.setWebViewClient(new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					setContentView(R.layout.hezierror);
					ImageView iv = (ImageView) findViewById(R.id.helpbtn);
					iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(HeZiWebActivity.this,
									DLNAHelpActivity.class);
							startActivity(intent);
						}
					});
					ProgressBar pb1 = (ProgressBar) findViewById(R.id.wbb_title_pb);
					pb1.setVisibility(View.GONE);

				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

			});

			web.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int progress) {
					serverHandler.sendEmptyMessage(OPEN_PROGRESSBAR);
					if (progress == 100)
						serverHandler.sendEmptyMessage(CLSOE_PROGRESSBAR);
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

		} else {
			web.getSettings().setBuiltInZoomControls(false);
			web.getSettings().setSupportZoom(false);
		}

		if (url != null)
			web.loadUrl(url);
		else {
			Toast.makeText(getApplicationContext(), "页面加载失败!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		pb = (ProgressBar) findViewById(R.id.wbb_title_pbnew);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			serverHandler.sendEmptyMessageDelayed(MSG_CLOSE_ACTIVITY, 400);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private Handler serverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_CLOSE_ACTIVITY:
				web.clearCache(true);
				finish();
				break;
			case OPEN_PROGRESSBAR:
				pb.setVisibility(View.VISIBLE);
				break;

			case CLSOE_PROGRESSBAR:
				pb.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();

	}

	private class FileDownLoadListener implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

	}

}
