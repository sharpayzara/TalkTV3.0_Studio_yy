package com.sumavision.talktv2.components;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 自定义布局：在该区域滑动时不释放焦点事件
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RecommendLayout extends RelativeLayout {

	public RecommendLayout(Context context) {
		super(context);
	}

	public RecommendLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RecommendLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean onFoucsTouch(MotionEvent ev) {
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		Rect rect = new Rect();
		this.getHitRect(rect);
		if (rect.contains(x, y)) {
			return true;
		}
		// if (getChildCount() > 1) {
		// View view = getChildAt(0);
		// rect.left = view.getLeft();
		// rect.right = view.getRight();
		// rect.top = view.getTop();
		// rect.bottom = view.getBottom();
		// if (rect.contains(x, y)) {
		// return true;
		// }
		// }
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (onFoucsTouch(ev))
			requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (onFoucsTouch(ev))
			requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}
}
