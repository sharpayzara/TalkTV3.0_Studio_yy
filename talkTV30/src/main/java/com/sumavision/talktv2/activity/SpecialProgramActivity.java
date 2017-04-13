package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.fragment.lib.SpecialDetailFragment;

public class SpecialProgramActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Intent intent = getIntent();
		boolean isSub = intent.getBooleanExtra("isSub", false);
		int programId = intent.getIntExtra("programId", -1);
		int columnId = intent.getIntExtra("columnId", 0);
		String headPic = intent.getStringExtra("pic");
		String title = intent.getStringExtra("title");

		getSupportActionBar().setTitle(R.string.menu_special);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						android.R.id.content,
						SpecialDetailFragment.newInstance(isSub, programId,
								columnId, headPic, title)).commit();
	}
}
