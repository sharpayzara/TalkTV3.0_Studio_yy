package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MyPagerAdapter;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.CustomViewPager;
import com.sumavision.talktv2.fragment.CommentFragment;
import com.sumavision.talktv2.fragment.ReplyFragment;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户评论页面<br>
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class UserCommentActivity extends BaseActivity implements
		OnPageChangeListener, OnSharedPreferenceChangeListener {

	int userId;

	SharedPreferences pushSp;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_comment);
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.pushMessage, Constants.key_user_comment, false);
		userId = getIntent().getIntExtra("userId", 0);
		if (userId == 0) {
			getSupportActionBar().setTitle("评论");
		} else if (userId == UserNow.current().userID) {
			getSupportActionBar().setTitle("我的评论");
			pushSp = getSharedPreferences(Constants.pushMessage, 0);
			pushSp.registerOnSharedPreferenceChangeListener(this);
		} else {
			getSupportActionBar().setTitle("他(她)的评论");
		}
		initViews();
	};

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("UserCommentActivity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UserCommentActivity");
		super.onPause();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (pushSp != null) {
			pushSp.unregisterOnSharedPreferenceChangeListener(this);
		}
	};

	PagerSlidingTabStrip pageTabs;
	CustomViewPager mViewPager;

	private void initViews() {
		pageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(CommentFragment.newInstance(0, userId, 0));
		fragments.add(ReplyFragment.newInstance(userId));
		ArrayList<String> titles = new ArrayList<String>();
		titles.add(getString(R.string.my_function_comment));
		titles.add(getString(R.string.my_function_replyme));
		MyPagerAdapter adapter = new MyPagerAdapter(
				getSupportFragmentManager(), titles, fragments);
		mViewPager.setAdapter(adapter);
		pageTabs.setOnPageChangeListener(this);
		pageTabs.setViewPager(mViewPager, -1);
		setTabsValue();
		if (pushSp != null && pushSp.getBoolean(Constants.key_reply, false)) {
			pageTabs.changeRedTip(1, true);
		}
	}

	private void setTabsValue() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		pageTabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP,
				getResources().getInteger(R.integer.tab_text_size), dm));
		pageTabs.setTextColorResource(R.color.black);
		pageTabs.setIndicatorColorResource(R.color.navigator_bg_color);
		pageTabs.setSelectedTextColorResource(R.color.navigator_bg_color);
		pageTabs.setTabBackground(0);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 1
				&& PreferencesUtils.getBoolean(this, Constants.pushMessage,
						Constants.key_reply)) {
			PreferencesUtils.putBoolean(this, Constants.pushMessage,
					Constants.key_reply, false);
			pageTabs.changeRedTip(arg0, false);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (Constants.key_reply.equals(key)) {
			if (pushSp.getBoolean(key, false)) {
				pageTabs.changeRedTip(2, true);
			}
		}

	}
}