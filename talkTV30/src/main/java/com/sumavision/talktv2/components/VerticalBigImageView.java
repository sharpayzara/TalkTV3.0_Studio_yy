package com.sumavision.talktv2.components;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.CommonUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 首页推荐竖图
 * 
 * @author suma-hpb
 * 
 */
public class VerticalBigImageView extends ImageView {
	int extendHeight;

	public VerticalBigImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public VerticalBigImageView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.VerticalBigImageView);
		extendHeight = mTypedArray.getInteger(
				R.styleable.VerticalBigImageView_extendHeight, 50);
		mTypedArray.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();
		if (d == null) {
			super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		float imageRatio = 16f / 9f;
		int height = (int) (widthSize / imageRatio);
		height = height + height
				+ CommonUtils.dip2px(getContext(), extendHeight);
		setMeasuredDimension(widthSize, height);
	}
}
