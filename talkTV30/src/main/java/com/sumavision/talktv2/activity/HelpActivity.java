package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.AwesomeAdapter;
import com.sumavision.talktv2.utils.WebpUtils;
import com.sumavision.talktv2.widget.JazzyViewPager;
import com.sumavision.talktv2.widget.JazzyViewPager.TransitionEffect;
import com.umeng.analytics.MobclickAgent;

/**
 * @author jianghao
 * @version 2.2
 * @description 帮助动界面
 * @createTime 2012-1-5
 * @changeLog
 */
public class HelpActivity extends Activity implements OnPageChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		fromSplash = getIntent().getBooleanExtra("fromSplash", false);
		initViews();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("HelpActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("HelpActivity");
		super.onPause();
	}

	private boolean fromSplash = false;

	private JazzyViewPager viewPager;
	private Button mainBtn;

	private void initViews() {
		viewPager = (JazzyViewPager) findViewById(R.id.viewPager);
		mainBtn = (Button) findViewById(R.id.btn_main);
		mainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				SaveUserData();
				if (fromSplash) {
					openMainPage();
				}
				finish();

			}
		});
		LayoutInflater inflater = LayoutInflater.from(this);
		View view1 = inflater.inflate(R.layout.help_pic_frame, null);
		((ImageView) view1.findViewById(R.id.imageView))
				.setImageBitmap(WebpUtils.getAssetBitmap(this,
						"webp/help_one.webp"));
		View view2 = inflater.inflate(R.layout.help_pic_frame, null);
		((ImageView) view2.findViewById(R.id.imageView))
				.setImageBitmap(WebpUtils.getAssetBitmap(this,
						"webp/help_two.webp"));
		View view3 = inflater.inflate(R.layout.help_pic_frame, null);
		((ImageView) view3.findViewById(R.id.imageView))
				.setImageBitmap(WebpUtils.getAssetBitmap(this,
						"webp/help_three.webp"));
		view3.findViewById(R.id.imageView).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						setResult(RESULT_OK);
						SaveUserData();
						if (fromSplash) {
							openMainPage();
						}
						finish();

					}
				});
		ArrayList<View> listViews = new ArrayList<View>();
		listViews.add(view1);
		listViews.add(view2);
		listViews.add(view3);
		AwesomeAdapter pageAdapter = new AwesomeAdapter(listViews);
		pageAdapter.setViewPage(viewPager);
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setTransitionEffect(TransitionEffect.CubeOut);
		viewPager.setPageMargin(20);
	}

	private void SaveUserData() {
		SharedPreferences sp = getSharedPreferences("otherInfo", MODE_PRIVATE);
		Editor spEd = sp.edit();
		spEd.putBoolean("isShowHelp", false);
		spEd.commit();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		// if (arg0 == 2 && fromSplash) {
		// mainBtn.setVisibility(View.VISIBLE);
		// } else {
		// mainBtn.setVisibility(View.GONE);
		// }
	}

	private void openMainPage() {
		Intent intent = new Intent(this, SlidingMainActivity.class);
		startActivity(intent);
	}

}
