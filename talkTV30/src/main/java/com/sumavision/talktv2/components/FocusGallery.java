package com.sumavision.talktv2.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.BaseAdapter;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class FocusGallery extends Gallery {

	public FocusGallery(Context context) {
		super(context);
	}


	public FocusGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
			// if (onItemSelectionChangeListener != null) {
			// if (currentPosition != 0) {
			// currentPosition = currentPosition - 1;
			// }
			// onItemSelectionChangeListener.onMoveLeft(currentPosition);
			// }
		} else {
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
			// if (onItemSelectionChangeListener != null) {
			// if (currentPosition != maxPosition) {
			// currentPosition = currentPosition + 1;
			// }
			// onItemSelectionChangeListener.onMoveRight(currentPosition);
			// }
		}
		super.onKeyDown(kEvent, null);
		return false;

	}

	public void setOnItemSelectionChangeListener(
			OnItemSelectionChangeListener onItemSelectionChangeListener) {
	}

	public interface OnItemSelectionChangeListener {
		public void onMoveLeft(int position);

		public void onMoveRight(int position);
	}

	public void setAdapter(BaseAdapter adapter) {
		super.setAdapter(adapter);
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// return true;
	// }
}
