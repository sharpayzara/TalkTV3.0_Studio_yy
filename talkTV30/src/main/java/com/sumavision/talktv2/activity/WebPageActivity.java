package com.sumavision.talktv2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.LoginFilter;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.badpx.webp.support.WebpDecoder;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.activity.CompatBaseActivity;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.UserDetailParser;
import com.sumavision.talktv2.http.json.UserDetailRequest;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.ImeiUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;

import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;

public class WebPageActivity extends CompatBaseActivity {
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
		web.addJavascriptInterface(new AwardMessage(),"jsInterface");
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
		web.setWebChromeClient(new WebChromeClient(){

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
		web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		web.getSettings().setUseWideViewPort(true);
//		web.getSettings().setUserAgentString("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)");
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
	public void updateUserInfo(){
		VolleyHelper.post(new UserDetailRequest().make(), new ParseListener(new UserDetailParser()) {
			@Override
			public void onParse(BaseJsonParser parser) {
				EventBus.getDefault().post(new UserInfoEvent());
			}
		},null);
	}

	public class AwardMessage{
		@JavascriptInterface
		public String callUserId(){
			return UserNow.current().userID+"";
		}

		@JavascriptInterface
		public String callImei(){
			return ImeiUtil.getInstance(getApplication()).getUniqueId();
		}

		@JavascriptInterface
		public void toMyGoodsList(){
			startActivity(new Intent(WebPageActivity.this,MyGiftActivity.class));
		}
		@JavascriptInterface
		public String userPoint(){
			return UserNow.current().totalPoint+"";
		}
		@JavascriptInterface
		public void isLottery(){
			PreferencesUtils.putString(WebPageActivity.this ,null,"curDate_found_award_date", CommonUtils.getDateString());
			Set<String> set = PreferencesUtils.getStringSet(WebPageActivity.this,null,"curDate_found_award_ids",new HashSet<String>());
			set.add(UserNow.current().userID+"");
			PreferencesUtils.putStringSet(WebPageActivity.this, null, "curDate_found_award_ids", set);
		}
		@JavascriptInterface
		public void hasAward(int type){
			switch (type){
				case 1:
					handler.sendEmptyMessageDelayed(1,2000);
					break;
				case 2:
					handler.sendEmptyMessageDelayed(1,2000);
					break;
			}
		}
	}
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					updateUserInfo();
					break;
			}
		}
	};

	@Override
	public void finish() {
		super.finish();
		if (getIntent().getBooleanExtra("notice",false)){
			Intent intent = new Intent(this,SlidingMainActivity.class);
			startActivity(intent);
		}
	}
}
