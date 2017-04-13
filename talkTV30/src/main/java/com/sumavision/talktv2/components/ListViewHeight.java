package com.sumavision.talktv2.components;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewHeight {
	// ScrollView中嵌套ListView
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			// listItem.measure(
			// MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
			// MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1) + 10);
		listView.setLayoutParams(params);
	}

	// ScrollView中嵌套ListView， 代码中的3，代表gridview 有3列
	public static void setGridViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		int hang = 0;
		if ((gridView.getCount() % 5) == 0) {

			hang = gridView.getCount() / 5;
		} else {
			hang = gridView.getCount() / 5 + 1;
		}
		for (int i = 0; i < hang; i++) {
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + 300;
		gridView.setLayoutParams(params);
	}
}
