package com.sumavision.talktv2.activity;

import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.fragment.MyFellowingFragment;

/**
 * 关注
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyFellowingActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int userId = getIntent().getIntExtra("id", 0);
		if (userId == UserNow.current().userID) {
			getSupportActionBar().setTitle(R.string.navigator_myfellowing);
		} else {
			getSupportActionBar().setTitle(R.string.navigator_otherfellowing);
		}
		MyFellowingFragment myFellowingFragment = MyFellowingFragment
				.newInstance();
		myFellowingFragment.getArguments().putInt("id", userId);
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, myFellowingFragment).commit();
	}

}
