package com.sumavision.talktv2.components;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 自定义gallery
 * 
 * 
 * @author:zhaohongru
 * 
 * @time:2013-12-5 下午2:33:38
 */
@SuppressWarnings("deprecation")
@SuppressLint("HandlerLeak")
public class GuideGallery extends Gallery {
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(timerAnimation);
		}
	};

	public GuideGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		timer.schedule(task, 5000, 8000);
	}

	private static final int timerAnimation = 1;
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case timerAnimation:
				int position = getSelectedItemPosition();
				if (position >= (getCount() - 1)) {
					onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				} else {
					onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// requestFocus();
			break;
		case MotionEvent.ACTION_MOVE:
			requestDisallowInterceptTouchEvent(true);
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	};

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int keyCode;
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;

		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return false;

	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {

		return e2.getX() > e1.getX();

	}

	public void stopGallery() {
		timer.cancel();
		task.cancel();
	}

	public void startGallery() {
		if (timer != null) {
			task.cancel();
			timer.cancel();
			Timer timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
					mHandler.sendEmptyMessage(timerAnimation);
				}
			};
			timer.schedule(task, 5000, 8000);
		}
	}
}
