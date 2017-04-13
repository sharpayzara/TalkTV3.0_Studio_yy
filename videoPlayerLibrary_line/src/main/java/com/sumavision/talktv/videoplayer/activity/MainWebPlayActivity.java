package com.sumavision.talktv.videoplayer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv.videoplayer.R;

/**
 * web页面播放
 * 
 * @author suma-hpb
 * 
 */
public class MainWebPlayActivity extends CompatBaseActivity implements
		OnTouchListener {

	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	protected LayoutInflater mInflater = null;
	private ProgressBar mProgressBar;
	private WebView mCurrentWebView;
	private boolean mFindDialogVisible = false;

	private GestureDetector mGestureDetector;
	private View mCustomView;
	private Bitmap mDefaultVideoPoster = null;
	private View mVideoProgressView = null;

	private FrameLayout mFullscreenContainer;
	private final int CLOSE_ME_DELAY = 1500;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private RelativeLayout mErrLayout;
	private TextView mErrTv;

	/**
	 * 加载初始化--需包含loading_layout
	 */
	protected void initLoadingLayout() {
		mErrLayout = (RelativeLayout) findViewById(R.id.errLayout);
		mErrTv = (TextView) findViewById(R.id.err_text);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mErrTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadData();
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setProgressBarVisibility(true);
		setContentView(R.layout.activity_main_webplay);
		getExtra();
		initLoadingLayout();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCurrentWebView = (CustomWebView) findViewById(R.id.webview);
		mGestureDetector = new GestureDetector(this, new GestureListener());
		mProgressBar = (ProgressBar) findViewById(R.id.WebViewProgress);
		mProgressBar.setMax(100);
		initView();
		showOrHideActionBar();
		if (title != null) {
			if (getSupportActionBar() != null) {
				getSupportActionBar().setTitle(title);
			} else {
				titleTextView.setText(title);
			}
		}
	}

	private String url;
	private String title;

	private void getExtra() {
		Intent intent = getIntent();
		if (intent.hasExtra("url"))
			url = intent.getStringExtra("url");
		if (intent.hasExtra("title"))
			title = intent.getStringExtra("title");
	}

	private void initView() {
		if (mFindDialogVisible) {
			closeFindDialog();
		}
		initializeCurrentWebView();
		updateUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		processQuit();
		handler.sendEmptyMessageDelayed(DELAY_FINISH, 1500);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getData() != null) {
			initView();
		}
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initializeCurrentWebView() {
		mCurrentWebView.getSettings().setJavaScriptEnabled(true);
		mCurrentWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);
		mCurrentWebView.invokeZoomPicker();
		mCurrentWebView.getSettings().setSupportZoom(true);
		mCurrentWebView.getSettings().setBuiltInZoomControls(true);

		mCurrentWebView.getSettings().setLoadWithOverviewMode(true);
		mCurrentWebView.getSettings().setAllowFileAccess(true);
		mCurrentWebView.setOnTouchListener(this);
		mCurrentWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(url);
				intent.setData(content_url);
				startActivity(intent);
			}

		});
		mCurrentWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!url.contains("redirect")) {// redirect 不让跳转
					view.loadUrl(url);
				}
				return true;
			}
		});

		if (url != null)
			mCurrentWebView.loadUrl(url);
		else {
			Toast.makeText(getApplicationContext(), "页面加载失败!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		mCurrentWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public Bitmap getDefaultVideoPoster() {
				if (mDefaultVideoPoster == null) {
					mDefaultVideoPoster = BitmapFactory.decodeResource(
							getResources(), R.drawable.netlive_icon);
				}

				return mDefaultVideoPoster;
			}

			@Override
			public View getVideoLoadingProgressView() {
				if (mVideoProgressView == null) {
					mVideoProgressView = mInflater.inflate(
							R.layout.video_loading_progress, null);
				}
				return mVideoProgressView;
			}

			@Override
			public void onShowCustomView(View view,
					WebChromeClient.CustomViewCallback callback) {
				showCustomView(view, callback);

			}

			@Override
			public void onHideCustomView() {
				hideCustomView();
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				try {
					((CustomWebView) view).setProgress(newProgress);
					mProgressBar.setProgress(mCurrentWebView.getProgress());
					if (newProgress == 100)
						handler.sendEmptyMessage(LOAD_OVER);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

		});
	}

	private void closeFindDialog() {
		setFindBarVisibility(false);
	}

	private void setFindBarVisibility(boolean visible) {
		if (visible) {
			mFindDialogVisible = true;
		} else {
			mFindDialogVisible = false;
		}
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			processQuit();
			handler.sendEmptyMessageDelayed(DELAY_FINISH, CLOSE_ME_DELAY);
			return true;
		default:
			return super.onKeyLongPress(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			return true;
		case KeyEvent.KEYCODE_BACK:
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mCurrentWebView.canGoBack()) {
				mCurrentWebView.goBack();
				return true;
			} else {
				processQuit();
				handler.sendEmptyMessageDelayed(DELAY_FINISH, CLOSE_ME_DELAY);
				return true;
			}
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	private void updateUI() {
		try {
			mProgressBar.setProgress(mCurrentWebView.getProgress());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (resultCode == RESULT_OK) {
			finish();
		} else
			super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	protected void onStop() {
		super.onPause();
		processQuit();
		finish();
	}

	private void showCustomView(View view,
			WebChromeClient.CustomViewCallback callback) {
		if (mCustomView != null) {
			callback.onCustomViewHidden();
			return;
		}
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		mFullscreenContainer = new FullscreenHolder(this);
		mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		mCustomView = view;
		mCustomViewCallback = callback;
	}

	private void hideCustomView() {
		if (mCustomView == null)
			return;
		try {
			FrameLayout decor = (FrameLayout) getWindow().getDecorView();
			decor.removeView(mFullscreenContainer);
			mFullscreenContainer = null;
			mCustomView = null;
			mCustomViewCallback.onCustomViewHidden();
		} catch (Exception e) {
			finish();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mCurrentWebView.zoomIn();
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	}

	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			processQuit();
			handler.sendEmptyMessageDelayed(DELAY_FINISH, 1500);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void hideLoadingLayout() {
		mErrLayout.setVisibility(View.GONE);
	}

	/**
	 * 加载失败
	 */
	protected void showErrorLayout() {
		mErrLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mErrTv.setVisibility(View.VISIBLE);
	}

	/**
	 * 加载数据为空
	 * 
	 * @param emptyTip
	 */
	protected void showEmptyLayout(String emptyTip) {
		mErrLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mErrTv.setVisibility(View.VISIBLE);
		mErrTv.setClickable(false);
		mErrTv.setCompoundDrawables(null, null, null, null);
		mErrTv.setText(emptyTip);
	}

	private static final int LOAD_OVER = 1;
	private static final int LOAD_ERROR = 2;
	private static final int BACKING = 3;
	private static final int DELAY_FINISH = 4;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BACKING:
				showEmptyLayout("正在返回，请稍候...");
				break;
			case LOAD_OVER:
				hideLoadingLayout();
				break;
			case LOAD_ERROR:
				showErrorLayout();
				break;
			case DELAY_FINISH:
				finish();
			default:
				break;
			}
		};
	};

	/**
	 * 加载等待
	 */
	public void showLoadingLayout() {
		mErrLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		mErrTv.setVisibility(View.GONE);
	}

	private void loadData() {
		if (url != null) {
			showLoadingLayout();
			mCurrentWebView.loadUrl(url);
		} else {
			showErrorLayout();
		}
	}

	private void processQuit() {
		mCurrentWebView.loadUrl("file:///android_asset/white_for_web.jpg");
		handler.sendEmptyMessageDelayed(BACKING, 100);
	}

}
