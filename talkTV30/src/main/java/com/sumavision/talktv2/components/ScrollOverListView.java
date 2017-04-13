package com.sumavision.talktv2.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * 功能描述： 一个可以监听ListView是否滚动到最顶部或最底部的自定义控件
 * 只能监听由触摸产生的，如果是ListView本身Flying导致的，则不能监听 如果加以改进，可以实现监听scroll滚动的具体位置等
 * 
 * @author njzhufeifei
 * @date 2013-2-2 上午9:51:27
 */
public class ScrollOverListView extends ListView {

	private int mLastY;

	/** 下拉到倒数第几个触发事件 */
	private int mBottomPosition = 0;

	private OnScrollOverListener mOnScrollOverListener = null;

	public ScrollOverListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollOverListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollOverListView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();// 得到正在执行的事件
		final int y = (int) ev.getRawY();// 得到事件y坐标
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mLastY = y;
			final boolean isHandled = mOnScrollOverListener.onMotionDown(ev);
			if (isHandled) {
				mLastY = y;
				return isHandled;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			final int childCount = getChildCount();// Returns the number of
													// children in the group.
			if (childCount == 0) {
				return super.onTouchEvent(ev);
			}
			final int itemCount = getAdapter().getCount() - mBottomPosition;
			final int deltaY = y - mLastY;

			final int lastBottom = getChildAt(childCount - 1).getBottom();
			final int end = getHeight() - getPaddingBottom();
			final int firstVisiblePosition = getFirstVisiblePosition();
			final boolean isHandleMotionMove = mOnScrollOverListener
					.onMotionMove(ev, deltaY);
			if (isHandleMotionMove) {
				mLastY = y;
				return true;
			}
			if (firstVisiblePosition + childCount >= itemCount
					&& lastBottom <= end && deltaY < 0) {
				final boolean isHandleOnListViewBottomAndPullDown;
				isHandleOnListViewBottomAndPullDown = mOnScrollOverListener
						.onListViewBottomAndPullUp(deltaY);
				if (isHandleOnListViewBottomAndPullDown) {
					mLastY = y;
					return true;
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			final boolean isHandlerMotionUp = mOnScrollOverListener
					.onMotionUp(ev);
			if (isHandlerMotionUp) {
				mLastY = y;
				return true;
			}
			break;
		}
		}
		mLastY = y;
		return super.onTouchEvent(ev);
	}

	/**
	 * 设置这个Listener可以监听是否到达顶端，或者是否到达低端等事件</br>
	 * 
	 * @see OnScrollOverListener
	 */
	public void setOnScrollOverListener(
			OnScrollOverListener onScrollOverListener) {
		mOnScrollOverListener = onScrollOverListener;
	}

	/**
	 * 滚动监听接口</br>
	 * 
	 * @see ScrollOverListView#setOnScrollOverListener(OnScrollOverListener)
	 */
	public interface OnScrollOverListener {

		/**
		 * 到达最底部触发
		 * 
		 * @param delta
		 *            手指点击移动产生的偏移量
		 * @return
		 */
		boolean onListViewBottomAndPullUp(int delta);// 上拉

		/**
		 * 手指触摸按下触发，相当于{@link MotionEvent#ACTION_DOWN}
		 * 
		 * @return 返回true表示自己处理
		 * @see View#onTouchEvent(MotionEvent)
		 */
		boolean onMotionDown(MotionEvent ev);

		/**
		 * 手指触摸移动触发，相当于{@link MotionEvent#ACTION_MOVE}
		 * 
		 * @return 返回true表示自己处理
		 * @see View#onTouchEvent(MotionEvent)
		 */
		boolean onMotionMove(MotionEvent ev, int delta);

		/**
		 * 手指触摸后提起触发，相当于{@link MotionEvent#ACTION_UP}
		 * 
		 * @return 返回true表示自己处理
		 * @see View#onTouchEvent(MotionEvent)
		 */
		boolean onMotionUp(MotionEvent ev);
	}
}
