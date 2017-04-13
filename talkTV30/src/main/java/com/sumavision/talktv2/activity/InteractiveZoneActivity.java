package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.Interactive;
import com.sumavision.talktv2.fragment.GuessingCyclopediaFragment;
import com.sumavision.talktv2.fragment.GuessingNewsFragment;
import com.sumavision.talktv2.fragment.InteractiveActivityListFragment;

/**
 * 竞猜专区
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveZoneActivity extends BaseActivity implements
		OnPageChangeListener, TabListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guessing_zone);
		getSupportActionBar().setLogo(R.drawable.ic_action_back);
		String title = Interactive.getInstance().name;
		if (TextUtils.isEmpty(title)) {
			title = "互动专区";
		}
		getSupportActionBar().setTitle(title);
		initViewPager();
		initTab();
	}

	private ViewPager viewPager;
	InteractiveActivityListFragment mGuessingFragment;
	GuessingNewsFragment mGuessingNewsFragment;
	GuessingCyclopediaFragment mGuessingBaikeFragment;

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		mGuessingFragment = InteractiveActivityListFragment.newInstance();
		mGuessingNewsFragment = GuessingNewsFragment.newInstance();
		mGuessingBaikeFragment = GuessingCyclopediaFragment.newInstance();
		fragments.add(mGuessingFragment);
		fragments.add(mGuessingNewsFragment);
		fragments.add(mGuessingBaikeFragment);
		viewPager.setOnPageChangeListener(this);
		viewPager.setAdapter(new CustomFragmentAdapter(
				getSupportFragmentManager(), fragments));

	}

	private void initTab() {
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab guessTab = mActionBar.newTab();
		guessTab.setText(R.string.interaction);
		guessTab.setTabListener(this);
		mActionBar.addTab(guessTab);
		ActionBar.Tab newsTab = mActionBar.newTab();
		newsTab.setText(R.string.guessing_news);
		newsTab.setTabListener(this);
		mActionBar.addTab(newsTab);
		ActionBar.Tab baikeTab = mActionBar.newTab();
		baikeTab.setText(R.string.guessing_baike);
		baikeTab.setTabListener(this);
		mActionBar.addTab(baikeTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.guessing, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.action_help) {
			startActivity(new Intent(this, InteractiveHelpActivity.class));
			return true;
		} else if (item.getItemId() == R.id.action_history) {
			if (UserNow.current().userID > 0) {
				startActivity(new Intent(this, MyGuessingActivity.class));
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		getSupportActionBar().selectTab(getSupportActionBar().getTabAt(arg0));
		if (arg0 == 1) {
			mGuessingNewsFragment.getZoneData();
		} else if (arg0 == 2) {
			mGuessingBaikeFragment.getData(0, 10);
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	class CustomFragmentAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> fragments;

		public CustomFragmentAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}
}
