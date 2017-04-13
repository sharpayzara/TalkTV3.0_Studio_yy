package com.sumavision.talktv2.activity;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.fragment.FriendAllFragment;

/**
 * 动态页大厅<br>
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DynamicActivity extends BaseActivity {

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.dynamichall);
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, FriendAllFragment.newInstance())
				.commit();
	};

}