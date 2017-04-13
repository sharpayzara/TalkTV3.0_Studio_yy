package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.fragment.FoundFragment;

public class ActivityDetailActivity extends BaseActivity {
	
	private TextActionProvider badgeAction;
	private FoundFragment foundFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_detail);
		
		initView();
	}
	
	private void initView() { 
		getSupportActionBar().setTitle("徽章活动");
		addFoundFragment();
	}
	
	private void addFoundFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		foundFragment = FoundFragment.newInstance();
		transaction.replace(R.id.activity_detail_layout, foundFragment, "content").commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_detail, menu);
		badgeAction = (TextActionProvider) menu.findItem(R.id.action_badge).getActionProvider();
		if (badgeAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		badgeAction.setShowText(R.string.badge_detail);
		badgeAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActivityDetailActivity.this, RecommendActivityActivity.class));
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
}
