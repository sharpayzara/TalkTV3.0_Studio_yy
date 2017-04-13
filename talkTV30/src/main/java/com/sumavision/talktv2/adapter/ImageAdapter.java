package com.sumavision.talktv2.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 循环显示
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ImageAdapter extends PagerAdapter {

	private ArrayList<ImageView> views;

	public ImageAdapter(ArrayList<ImageView> views) {
		super();
		this.views = views;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position % views.size()));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position % views.size()));
		return views.get(position % views.size());
	}
}
