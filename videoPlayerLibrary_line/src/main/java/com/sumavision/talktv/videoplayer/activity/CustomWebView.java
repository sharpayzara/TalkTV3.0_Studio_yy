package com.sumavision.talktv.videoplayer.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CustomWebView extends WebView {

	// private Context mContext;

	private int mProgress = 100;

	private boolean mIsLoading = false;
	private String mLoadedUrl;
	private static boolean mBoMethodsLoaded = false;

	private static Method mOnPauseMethod = null;
	private static Method mOnResumeMethod = null;
	private static Method mSetFindIsUp = null;
	private static Method mNotifyFindDialogDismissed = null;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 */
	public CustomWebView(Context context) {
		super(context);

		// mContext = context;

		initializeOptions();
		loadMethods();
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 * @param attrs
	 *            The attribute set.
	 */
	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// mContext = context;

		initializeOptions();
		loadMethods();
	}

	/**
	 * Initialize the WebView with the options set by the user through
	 * preferences.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void initializeOptions() {
		WebSettings settings = getSettings();
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);

		CookieManager.getInstance().setAcceptCookie(false);

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();

		// Enable / disable zoom support in case of multiple pointer, e.g.
		// enable zoom when we have two down pointers, disable with one pointer
		// or when pointer up.
		// We do this to prevent the display of zoom controls, which are not
		// useful and override over the right bubble.
		if ((action == MotionEvent.ACTION_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_1_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_2_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_3_DOWN)) {
			if (ev.getPointerCount() > 1) {
				this.getSettings().setBuiltInZoomControls(true);
				this.getSettings().setSupportZoom(true);
			} else {
				this.getSettings().setBuiltInZoomControls(false);
				this.getSettings().setSupportZoom(false);

				// CNTV
				// if (mLoadedUrl.contains("cntv")) {
				// openLiveActivity(1, mPlayUrl, mTitle);
				// }
			}
		} else if ((action == MotionEvent.ACTION_UP)
				|| (action == MotionEvent.ACTION_POINTER_UP)
				|| (action == MotionEvent.ACTION_POINTER_1_UP)
				|| (action == MotionEvent.ACTION_POINTER_2_UP)
				|| (action == MotionEvent.ACTION_POINTER_3_UP)) {
			this.getSettings().setBuiltInZoomControls(false);
			this.getSettings().setSupportZoom(false);
		}

		return super.onTouchEvent(ev);
	}

	@Override
	public void loadUrl(String url) {
		mLoadedUrl = url;
		super.loadUrl(url);
	}

	/**
	 * Inject the AdSweep javascript.
	 */
	public void loadAdSweep() {
		// super.loadUrl(ApplicationUtils.getAdSweepString(mContext));
	}

	/**
	 * Set the current loading progress of this view.
	 * 
	 * @param progress
	 *            The current loading progress.
	 */
	public void setProgress(int progress) {
		mProgress = progress;
	}

	/**
	 * Get the current loading progress of the view.
	 * 
	 * @return The current loading progress of the view.
	 */
	@Override
	public int getProgress() {
		return mProgress;
	}

	/**
	 * Triggered when a new page loading is requested.
	 */
	public void notifyPageStarted() {
		mIsLoading = true;
	}

	/**
	 * Triggered when the page has finished loading.
	 */
	public void notifyPageFinished() {
		mProgress = 100;
		mIsLoading = false;
	}

	/**
	 * Check if the view is currently loading.
	 * 
	 * @return True if the view is currently loading.
	 */
	public boolean isLoading() {
		return mIsLoading;
	}

	/**
	 * Get the loaded url, e.g. the one asked by the user, without redirections.
	 * 
	 * @return The loaded url.
	 */
	public String getLoadedUrl() {
		return mLoadedUrl;
	}

	/**
	 * Reset the loaded url.
	 */
	public void resetLoadedUrl() {
		mLoadedUrl = null;
	}

	public boolean isSameUrl(String url) {
		if (url != null) {
			return url.equalsIgnoreCase(this.getUrl());
		}

		return false;
	}

	/**
	 * Perform an 'onPause' on this WebView through reflexion.
	 */
	public void doOnPause() {
		if (mOnPauseMethod != null) {
			try {

				mOnPauseMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			}
		}
	}

	/**
	 * Perform an 'onResume' on this WebView through reflexion.
	 */
	public void doOnResume() {
		if (mOnResumeMethod != null) {
			try {

				mOnResumeMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			}
		}
	}

	public void doSetFindIsUp(boolean value) {
		if (mSetFindIsUp != null) {
			try {

				mSetFindIsUp.invoke(this, value);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			}
		}
	}

	public void doNotifyFindDialogDismissed() {
		if (mNotifyFindDialogDismissed != null) {
			try {

				mNotifyFindDialogDismissed.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			}
		}
	}

	/**
	 * Load static reflected methods.
	 */
	private void loadMethods() {

		if (!mBoMethodsLoaded) {

			try {

				mOnPauseMethod = WebView.class.getMethod("onPause");
				mOnResumeMethod = WebView.class.getMethod("onResume");

			} catch (SecurityException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			} catch (NoSuchMethodException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			}

			try {

				mSetFindIsUp = WebView.class.getMethod("setFindIsUp",
						Boolean.TYPE);
				mNotifyFindDialogDismissed = WebView.class
						.getMethod("notifyFindDialogDismissed");

			} catch (SecurityException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mSetFindIsUp = null;
				mNotifyFindDialogDismissed = null;
			} catch (NoSuchMethodException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mSetFindIsUp = null;
				mNotifyFindDialogDismissed = null;
			}

			mBoMethodsLoaded = true;
		}
	}

	public static final String IWEIKAN_USERAGENT = "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/18 Mobile Safari/534.30";
	public static final String IWEIKAN_USERAGENT_1 = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
}
