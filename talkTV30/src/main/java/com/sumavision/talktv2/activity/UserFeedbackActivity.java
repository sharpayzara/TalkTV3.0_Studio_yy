package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.fragment.FeedbackFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * @author hpb
 * @createTime
 * @description 用户反馈
 * @changeLog
 */
public class UserFeedbackActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_new);
		getSupportActionBar().setTitle(R.string.navigator_feedback);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.feedback_layout, FeedbackFragment.newInstance()).commit();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UserFeedbackActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("UserFeedbackActivity");
		super.onResume();
	}

}
