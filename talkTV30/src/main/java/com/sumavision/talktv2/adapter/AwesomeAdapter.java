package com.sumavision.talktv2.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumavision.talktv2.widget.JazzyViewPager;

public class AwesomeAdapter extends PagerAdapter {
	private List<View> listViews;
	private JazzyViewPager viewPage;
	private ArrayList<String> titleList;

	public void setTitleList(ArrayList<String> titleList) {
		this.titleList = titleList;
	}

	public void setViewPage(JazzyViewPager viewPage) {
		this.viewPage = viewPage;
	}

	public AwesomeAdapter(List<View> listViews) {
		this.listViews = listViews;
	}

	@Override
	public int getCount() {
		return listViews.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (titleList != null) {
			return titleList.get(position);
		}
		return super.getPageTitle(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		View view = listViews.get(position);
		((ViewPager) container).addView(view, 0);
		if (viewPage != null) {
			viewPage.setObjectForPosition(view, position);
		}
		return view;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView(listViews.get(position));

	}
}
