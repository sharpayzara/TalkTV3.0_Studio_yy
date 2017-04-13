package com.sumavision.talktv2.activity;

import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.fragment.FansFragment;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 粉丝页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FansActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.pushMessage, Constants.key_fans, false);
		int userId = getIntent().getIntExtra("id", 0);
		FansFragment fanFragment = FansFragment.newInstance();
		fanFragment.getArguments().putInt("id", userId);
		if (userId == UserNow.current().userID) {
			getSupportActionBar().setTitle(R.string.navigator_myfans);
		} else {
			getSupportActionBar().setTitle(R.string.navigator_otherfans);
		}
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, fanFragment).commit();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("FansActivity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("FansActivity");
		super.onPause();
	}
}
