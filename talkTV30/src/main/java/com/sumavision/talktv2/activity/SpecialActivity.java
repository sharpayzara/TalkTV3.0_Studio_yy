package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.fragment.lib.SpecialDetailFragment;

/**
 * 专题页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SpecialActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Intent intent = getIntent();
		boolean isSub = intent.getBooleanExtra("isSub", false);
		int programId = intent.getIntExtra("programId", -1);
		int columnId = intent.getIntExtra("columnId", 0);
		String headPic = intent.getStringExtra("pic");

		getSupportActionBar().setTitle(R.string.menu_special);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						android.R.id.content,
						SpecialDetailFragment.newInstance(isSub, programId,
								columnId, headPic, null)).commit();
	}
}
