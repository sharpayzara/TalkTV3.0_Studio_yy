package com.sumavision.talktv2.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * 自定义viewpager:<br>
 * 在页面滑动切换时不释放焦点,正常切换.在最左边进行
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CustomViewPager extends ViewPager {
	private float xDistance, yDistance, xLast, yLast;

	 @SuppressWarnings("unused")
	private GestureDetector mGestureDetector;

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		 mGestureDetector = new GestureDetector(context, new
		 XScrollDetector());
	}

	public CustomViewPager(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context, new XScrollDetector());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;
			if (yDistance > xDistance) {
				return false;
			}
			break;
		case MotionEvent.ACTION_UP:
			requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		return super.dispatchTouchEvent(ev);
	}

	class XScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return (Math.abs(distanceX) > Math.abs(distanceY));
		}
	}
}
