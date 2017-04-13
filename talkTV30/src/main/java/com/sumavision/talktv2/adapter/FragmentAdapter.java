package com.sumavision.talktv2.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
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
