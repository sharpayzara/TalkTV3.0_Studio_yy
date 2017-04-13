package com.sumavision.talktv.videoplayer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.sumavision.talktv.videoplayer.R;

import java.lang.reflect.InvocationTargetException;

/**
 * 使用WebView播放视频时需要注意的地方：
 * 1、加网络访问权限（及其他所需要的权限）； 
 * 2、WebViewClient中方法shouldOverrideUrlLoading可用来实现点击webView页面的链接；
 * 3、WebView中播放视频需要添加webView.setWebChromeClient(new WebChromeClient())；
 * 4、视频竖屏时，点击全屏，想要切换到横屏全屏的状态，那么必须在Manifest.xml配置文件该Activity的
 * 	配置文件中添加android:configChanges="orientation|screenSize"语句。
 * 5、如果视频不能播放，或者播放比较卡，可以采用硬件加速，即在Application，或所在的Activity的配置文件中添加
 * 	android:hardwareAccelerated="true"即可。
 * @author zhongyao
 */
public class WebPlayerActivity extends Activity {
	private WebView webView;
	private FrameLayout video_fullView;// 全屏时视频加载view
	private View xCustomView;
	private ProgressDialog waitdialog = null;
	private CustomViewCallback xCustomViewCallback;
	private myWebChromeClient xwebchromeclient;
	private String url = "http://www.wasu.cn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉应用标题
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_webplayer);

		waitdialog = new ProgressDialog(this);
		waitdialog.setTitle("提示");
		waitdialog.setMessage("视频页面加载中...");
		waitdialog.setIndeterminate(true);
		waitdialog.setCancelable(true);
		waitdialog.show();

		webView = (WebView) findViewById(R.id.webView);
		video_fullView = (FrameLayout) findViewById(R.id.video_fullView);

		WebSettings ws = webView.getSettings();
		ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
		 ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);// 排版适应屏幕

		ws.setUseWideViewPort(true);// 可任意比例缩放
		ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。

		ws.setSavePassword(true);
		ws.setSaveFormData(true);// 保存表单数据
		ws.setJavaScriptEnabled(true);
//		ws.setGeolocationEnabled(true);// 启用地理定位
//		ws.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");// 设置定位的数据库路径
		ws.setDomStorageEnabled(true);
		ws.setSupportMultipleWindows(true);// 新加
		xwebchromeclient = new myWebChromeClient();
		webView.setWebChromeClient(xwebchromeclient);
		webView.setWebViewClient(new myWebViewClient());
		String temp = getIntent().getStringExtra("url");
		if (!TextUtils.isEmpty(temp)){
			url = getIntent().getStringExtra("url");
		}
		webView.loadUrl(url);
	}

	public class myWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			waitdialog.dismiss();
		}
	}

	public class myWebChromeClient extends WebChromeClient {
		private View xprogressvideo;

		// 播放网络视频时全屏会被调用的方法
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			webView.setVisibility(View.INVISIBLE);
			// 如果一个视图已经存在，那么立刻终止并新建一个
			if (xCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			changeStatuBar(true);
			video_fullView.addView(view);
			xCustomView = view;
			xCustomViewCallback = callback;
			video_fullView.setVisibility(View.VISIBLE);
		}

		// 视频播放退出全屏会被调用的
		@Override
		public void onHideCustomView() {
			if (xCustomView == null)// 不是全屏播放状态
				return;

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			changeStatuBar(false);
			xCustomView.setVisibility(View.GONE);
			video_fullView.removeView(xCustomView);
			xCustomView = null;
			video_fullView.setVisibility(View.GONE);
			xCustomViewCallback.onCustomViewHidden();
			webView.setVisibility(View.VISIBLE);
		}

		// 视频加载时进程loading
		@Override
		public View getVideoLoadingProgressView() {
			if (xprogressvideo == null) {
				LayoutInflater inflater = LayoutInflater
						.from(WebPlayerActivity.this);
				xprogressvideo = inflater.inflate(
						R.layout.web_video_loading_progress, null);
			}
			return xprogressvideo;
		}
	}

	/**
	 * 判断是否是全屏
	 *
	 * @return
	 */
	public boolean inCustomView() {
		return (xCustomView != null);
	}

	/**
	 * 全屏时按返加键执行退出全屏方法
	 */
	public void hideCustomView() {
		xwebchromeclient.onHideCustomView();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		webView.onResume();
		try {
			webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		webView.resumeTimers();

		/**
		 * 设置为横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		webView.onPause();
		try {
			webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		webView.pauseTimers();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		super.onDestroy();
		video_fullView.removeAllViews();
		webView.loadUrl("about:blank");
		webView.stopLoading();
		webView.setWebChromeClient(null);
		webView.setWebViewClient(null);
		webView.destroy();
		webView = null;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inCustomView()) {
				// webViewDetails.loadUrl("about:blank");
				hideCustomView();
				return true;
			} else {
				webView.loadUrl("about:blank");
				WebPlayerActivity.this.finish();
			}
		}
		return false;
	}
	public void changeStatuBar(boolean isFull){
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if(isFull){
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}else{
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
}
