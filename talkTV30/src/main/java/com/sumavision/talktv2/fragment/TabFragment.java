package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MyPagerAdapter;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;

/**
 * 可自定义高度tab
 * 
 * @author suma-hpb
 * 
 */
public abstract class TabFragment extends BaseFragment implements
		OnPageChangeListener {
	private PagerSlidingTabStrip tabs;
	private DisplayMetrics dm;
	ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = getResources().getDisplayMetrics();
	}

	/**
	 * 初始化view
	 * 
	 * @param pager
	 * @param tabs
	 */
	public void initViewpageAndTabs(ViewPager pager, PagerSlidingTabStrip tabs) {
		this.pager = pager;
		this.tabs = tabs;
	}

	/**
	 * 更新tab及内容
	 * 
	 * @param titleList
	 * @param fragments
	 * @param redPosition
	 *            -1表示无红点
	 */
	public void updateTabs(ArrayList<String> titleList,
			ArrayList<Fragment> fragments, int redPosition) {
		pager.setAdapter(new MyPagerAdapter(getChildFragmentManager(),
				titleList, fragments));
		tabs.setOnPageChangeListener(this);
		tabs.setViewPager(pager, redPosition);
		setTabsValue();
	}

	private void setTabsValue() {
		tabs.setShouldExpand(true);
		tabs.setTabPaddingLeftRight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, dm));
		tabs.setDividerColor(Color.TRANSPARENT);
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP,
				getResources().getInteger(R.integer.tab_text_size), dm));
		tabs.setTextColorResource(R.color.light_black);
		tabs.setIndicatorColorResource(R.color.navigator_bg_color);
		tabs.setSelectedTextColorResource(R.color.navigator_bg_color);
		tabs.setTabBackground(0);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

}
