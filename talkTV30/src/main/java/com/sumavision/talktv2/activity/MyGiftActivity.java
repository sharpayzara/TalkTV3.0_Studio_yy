package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sumavision.offlinecache.ui.CacheTabStyle;
import com.sumavision.offlinecache.ui.MyPagerAdapter;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MyGiftAdapter;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.fragment.MyGiftRecordFragment;
import com.sumavision.talktv2.fragment.MyPartsGoodsFragment;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.MyGiftEvent;
import com.sumavision.talktv2.http.listener.eshop.OnUserGoodsListener;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 用户礼品页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyGiftActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
	ViewPager mViewPager;
	PagerSlidingTabStrip tabs;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_mygift);
		getSupportActionBar().setTitle(R.string.my_gift);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		ArrayList<String> titles = new ArrayList<String>();
		titles.add("记录");
		titles.add("碎片");
		ArrayList<Fragment> fragments = new ArrayList<>();
		fragments.add(MyGiftRecordFragment.newInstance());
		fragments.add(MyPartsGoodsFragment.newInstance());
		MyPagerAdapter adapter = new MyPagerAdapter(
				getSupportFragmentManager(), titles, fragments);
		mViewPager.setAdapter(adapter);
		tabs.setOnPageChangeListener(this);
		tabs.setViewPager(mViewPager, -1);
		setTabsValue();
	}
	private void setTabsValue() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.tab_text_size), dm));
		tabs.setTextColorResource(R.color.light_black);
		tabs.setIndicatorColorResource(R.color.navigator_bg_color);
		tabs.setSelectedTextColorResource(R.color.navigator_bg_color);
		tabs.setTabBackground(0);
		tabs.setBackgroundResource(R.color.white);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("MyGiftActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyGiftActivity");
		super.onPause();
	}

	private void initView() {
	}

	private ArrayList<ExchangeGood> allList = new ArrayList<ExchangeGood>();


	private static final int REQUEST_CODE_RECEIVE = 1;

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
