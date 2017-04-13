package com.sumavision.talktv2.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 固定宽度16:9缩放图片
 * 
 * @author suma-hpb
 * 
 */
public class ResizableVercitalImageView extends ImageView {
	public ResizableVercitalImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizableVercitalImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();
		if (d == null) {
			super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		float imageRatio = 16f / 12f;
		int height = (int) (widthSize * imageRatio);
		setMeasuredDimension(widthSize, height);
	}
}
