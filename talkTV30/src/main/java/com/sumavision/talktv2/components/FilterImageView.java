package com.sumavision.talktv2.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 自带按下效果(半透明灰色)ImageView
 * 
 * @author suma-hpb
 * 
 */
public class FilterImageView extends ImageView {

	public FilterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setColorFilter(Color.argb(0x7f, 0x88, 0x88, 0x88));
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			clearColorFilter();
			invalidate();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Drawable d = getDrawable();
//		if (d == null) {
//			super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//			return;
//		}
//		int dWith = d.getIntrinsicWidth();
//		int dHeight = d.getIntrinsicHeight();
//		float dRatio = dWith * 1.0f / dHeight;
//		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//		int height = (int) (widthSize / dRatio);
//		setMeasuredDimension(widthSize, height);
//	}
}
