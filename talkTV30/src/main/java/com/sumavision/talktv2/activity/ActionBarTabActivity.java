package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.sumavision.talktv2.R;

/**
 * viewpager+tabbar
 * 
 * @author suma-hpb
 * 
 */
public class ActionBarTabActivity extends BaseActivity implements
		OnPageChangeListener, TabListener {
	protected ViewPager mViewPager;

	/**
	 * viewpager
	 * 
	 * @param position
	 */
	public void onViewPageSelected(int position)
	{

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initViewPagerAndTab(ArrayList<Fragment> fragments,
			String[] tabName) {
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		FragmentAdapter adapter = new FragmentAdapter(
				getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(this);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (String name : tabName) {
			ActionBar.Tab mTab = getSupportActionBar().newTab();
			mTab.setText(name);
			mTab.setTabListener(this);
			getSupportActionBar().addTab(mTab);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		getSupportActionBar().selectTab(
				getSupportActionBar().getTabAt(position));
		onViewPageSelected(position);

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	class FragmentAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> fragments;

		public FragmentAdapter(FragmentManager manager,
				ArrayList<Fragment> fragments) {
			super(manager);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			if (fragments == null)
				return 0;
			else
				return fragments.size();
		}
	}
}
