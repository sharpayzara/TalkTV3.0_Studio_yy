/**
 * 
 */
package com.sumavision.talktv2.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * scrollView嵌套显示不全解决
 */
public class StaticListView extends ListView {

	public StaticListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public StaticListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StaticListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int dividerHeight = getDividerHeight();
		int children = getChildCount();
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		expandSpec += (children - 1) * dividerHeight;
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
