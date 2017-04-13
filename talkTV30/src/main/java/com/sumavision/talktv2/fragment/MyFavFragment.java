package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

public class MyFavFragment extends TabFragment {
	public static MyFavFragment newInstance() {
		MyFavFragment fragment = new MyFavFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_fav);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MyFavFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MyFavFragment");
	}

	public void clearAll() {
		if (mViewPager.getCurrentItem() == 0) {
			favDetailFragment.clear();
		} else {
			liveFavDetailFragment.clear();
		}
	}

	public void refreshVodFav() {
		favDetailFragment.reloadData();
	}

	@Override
	public void onPageSelected(int position) {
		((FavDetailFragment) fragments.get(position)).showOrHideAction();

	}

	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	FavDetailFragment favDetailFragment;
	FavDetailFragment liveFavDetailFragment;
	ViewPager mViewPager;

	@Override
	protected void initViews(View view) {
		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view
				.findViewById(R.id.tabs);
		initViewpageAndTabs(mViewPager, tabs);
		favDetailFragment = FavDetailFragment.newInstance(false);
		liveFavDetailFragment = FavDetailFragment.newInstance(true);
		fragments.add(favDetailFragment);
		boolean liveModule = PreferencesUtils.getBoolean(getActivity(), null, "liveModule", true);
		if (liveModule) {
			fragments.add(liveFavDetailFragment);
		}
		ArrayList<String> titles = new ArrayList<String>();
		String[] arr = getResources().getStringArray(R.array.fav_tab);
		for (String s : arr) {
			if (liveModule) {
				titles.add(s);
			} else {
				if (!s.equals("电视直播")) {
					titles.add(s);
				} else {
					continue;
				}
			}
		}
		if (titles.size() == 1) {
			tabs.setVisibility(View.GONE);
		}
		updateTabs(titles, fragments, -1);

	}

	@Override
	public void reloadData() {

	}

}
