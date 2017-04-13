package com.sumavision.talktv2.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * listview中嵌套gridview时显示冲突解决:默认只显示一行
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StaticGridView extends GridView {

	public StaticGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StaticGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StaticGridView(Context context) {
		super(context);

	}

	/** * 设置不滚动 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}