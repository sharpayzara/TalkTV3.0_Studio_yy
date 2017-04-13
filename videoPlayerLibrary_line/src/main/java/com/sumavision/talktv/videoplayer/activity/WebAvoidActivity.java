package com.sumavision.talktv.videoplayer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.cachingwhileplaying.CachingWhilePlayingService;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.NetPlayData;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * @author李伟
 * @version
 * @createTime
 * @description web浏览器版权规避界面：含真实url
 * @changeLog
 */
public class WebAvoidActivity extends CompatBaseActivity {

	private String url = "";
	private String title = "节目播放";
	private TextView urlold,tipText;
	private ImageView urlbtn;
	int playType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webavoid);
		
		EventBus.getDefault().removeAllStickyEvents();
		int subid = getIntent().getIntExtra("subid", 0);
		int programId = getIntent().getIntExtra("id", 0);
		String url = getIntent().getStringExtra("url");
		playType = getIntent().getIntExtra("playType", 0);
		Log.i("PlayerActivity", "webavoid playtype:"+playType);
		boolean isHalf = getIntent().getBooleanExtra("isHalf", false);
//		if (playType == 2 ) {
//			startPreLoading(url, programId, subid);
//		}
//		if (playType == 5) {
//			startPreLoading(url, programId, subid);
//		}
//		getIntent().putExtra(PlayerActivity.INTENT_NEEDAVOID, true);
		if (!getIntent()
				.getBooleanExtra(PlayerActivity.INTENT_NEEDAVOID, true)) {
            if (playType == PlayerActivity.LIVE_PLAY && getIntent().getBooleanExtra("toWeb",false)){

            }else {
                loadPlayerStatusForLocalPlay();
                return;
            }
		}
		getExtra();
		initView();
		showOrHideActionBar();
		if (!TextUtils.isEmpty(title)) {
			String subTitle = title;
			if (title.indexOf(":") > 0) {
				subTitle = title.substring(title.indexOf(":") + 1);
			} else if (title.indexOf("：") > 0) {
				subTitle = title.substring(title.indexOf("：") + 1);
			}
			if (getSupportActionBar() != null
					&& getSupportActionBar().isShowing()) {
				getSupportActionBar().setTitle(subTitle);
			} else {
				titleTextView.setText(subTitle);
			}
		}
	}

	private PlayCountDown countDown;

	private void getExtra() {
		if (getIntent().getStringExtra("url") != null) {
			url = getIntent().getStringExtra("url");
		}
		String name = getIntent().getStringExtra("programName");
		if (TextUtils.isEmpty(name)) {
			title = getIntent().getStringExtra("title");
		} else {
			title = name + " " + getIntent().getStringExtra("title");
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

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		countDown = new PlayCountDown(3000, 1000);
		countDown.start();

		urlold = (TextView) findViewById(R.id.urlold);
		urlbtn = (ImageView) findViewById(R.id.urlbtn);
		urlbtn.setOnClickListener(this);
		tipText = (TextView) findViewById(R.id.web_tip_text);

		web = (WebView) findViewById(R.id.wbb);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		web.invokeZoomPicker();
		web.getSettings().setSupportZoom(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setUserAgentString("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)");
		if (playType == PlayerActivity.LIVE_PLAY){
			urlbtn.setImageResource(R.drawable.webplaybtn1);
			countDown.cancel();
		} else {
			urlbtn.setImageResource(R.drawable.webplaybtn);
		}

		boolean b = isBigDisplay();
		if (b) {
			web.setInitialScale(50);
		} else {
			web.setInitialScale(32);
		}
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setAllowFileAccess(true);
		web.setDownloadListener(new FileDownLoadListener());
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (playType == PlayerActivity.LIVE_PLAY){
					tipText.setVisibility(View.GONE);
					web.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (playType == PlayerActivity.LIVE_PLAY){
					tipText.setVisibility(View.VISIBLE);
					web.setVisibility(View.GONE);
				}
			}
		});
		web.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				countDown.cancel();
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
		if (getIntent().getIntExtra("playType", 0) == PlayerActivity.LIVE_PLAY
				&& getIntent().hasExtra("NetPlayData")){
			ArrayList<NetPlayData> temp = (ArrayList<NetPlayData>) getIntent().getSerializableExtra("NetPlayData");
			if (temp != null && temp.size()>0){
				int pos = getIntent().getIntExtra("livepos",0);
				if (!TextUtils.isEmpty(temp.get(pos<temp.size()?pos:0).showUrl)){
					url = temp.get(pos<temp.size()?pos:0).showUrl;
				} else {
					url = temp.get(pos<temp.size()?pos:0).url;
				}

			}
		}
		if (url != null)
			web.loadUrl(url);
		else {
			Toast.makeText(getApplicationContext(), "页面加载失败!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		urlold.setText(url);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		if (getSupportActionBar() != null) {
			getSupportMenuInflater().inflate(R.menu.web_avoid, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			web.reload();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			stopPreLoading();
			if (getIntent().getBooleanExtra("isHalf", false)){
				loadPlayerStatusForLocalPlay();
			}
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.urlbtn) {
			countDown.cancel();
			countDown.onFinish();
		}
		super.onClick(v);
	}

	public void loadPlayerStatusForLocalPlay() {
		Intent intent = getIntent();
		boolean isHalf = intent.getBooleanExtra("isHalf", false);
		int playType = intent.getIntExtra("playType", 0);
		if (playType == PlayerActivity.LIVE_PLAY
				|| (!isHalf && !getIntent().getBooleanExtra("fromSource",false))) {
			intent.putExtras(getIntent().getExtras());
			if (playType == 2) {
				intent.putExtra("needVParse", true);
			}
			intent.setClass(this, PlayerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (!getIntent().getBooleanExtra("fromSource",false)){
				//解决多全屏问题
				startActivity(intent);
			}
		}
//		if ((playType ==2 || playType==5) && isHalf){
//			setResult(RESULT_OK);
//			EventBus.getDefault().post(new JiShuData());
//		}
		if (getIntent().getBooleanExtra("fromSource", false)){
			EventBus.getDefault().post(new JiShuData());
		}
		finish();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		web.onResume();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		super.onPause();
		web.onPause();
	}

	@Override
	public void finish() {
		super.finish();
		if (countDown != null) {
			countDown.cancel();
		}
		if (web != null) {
			web.stopLoading();
		}
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

	class PlayCountDown extends CountDownTimer {

		public PlayCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			loadPlayerStatusForLocalPlay();
		}

	}
	
	private void startPreLoading(String url, int programId, int subId) {
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_START);
		intent.putExtra("url", url);
		intent.putExtra("programId", programId);
		intent.putExtra("subId", subId);
		startService(intent);
	}
	
	private void stopPreLoading() {
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_STOP);
		startService(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.ACTION_DOWN) {
			stopPreLoading();
		}
		return super.onKeyDown(keyCode, event);
	}
}
