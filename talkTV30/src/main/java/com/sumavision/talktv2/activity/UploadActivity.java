package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.fragment.UploadFragment;

public class UploadActivity extends BaseActivity {
	
	private TextActionProvider editAction;
	private UploadFragment uploadFragment;
	
	private boolean editMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_detail);
		
		initView();
	}
	
	private void initView() { 
		getSupportActionBar().setTitle("上传");
		addFragment();
	}
	
	private void addFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		uploadFragment = UploadFragment.newInstance();
		transaction.replace(R.id.activity_detail_layout, uploadFragment, "content").commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_detail, menu);
		editAction = (TextActionProvider) menu.findItem(R.id.action_badge).getActionProvider();
		if (editAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		editAction.setShowText(R.string.navigator_channel_edit);
		editAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (uploadFragment != null) {
					editMode = !editMode;
					uploadFragment.setEditMode(editMode);
					if (editMode) {
						editAction.setShowText(R.string.navigator_channel_complete);
					} else {
						editAction.setShowText(R.string.navigator_channel_edit);
					}
				}
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	public void onEvent(VodProgramData data){
		if (uploadFragment!=null){
			uploadFragment.getUploadProgram(0, 10);
		}
	}
}
