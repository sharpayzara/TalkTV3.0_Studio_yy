package com.sumavision.talktv2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.fragment.AddProgramFragment;

public class AddProgramActivity extends BaseActivity {
	
	private AddProgramFragment addProgramFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_detail);
		
		initView();
	}
	
	private void initView() { 
		getSupportActionBar().setTitle("添加影片");
		addFragment();
	}
	
	private void addFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		addProgramFragment = AddProgramFragment.newInstance();
		transaction.replace(R.id.activity_detail_layout, addProgramFragment, "content").commit();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(addProgramFragment.isDetached()){
			return;
		}
		if (arg1 == Activity.RESULT_OK) {
			if (arg0 == 102) {
				Bundle bundle = arg2.getExtras();
				String result = bundle.getString("result");
				addProgramFragment.getScanResult(result);
			} else if (arg0 == 101) {
				finish();
			}
		}
	}
	public void onEvent(EventMessage msg){
		if (msg.name.equals("addProgram")){
			finish();
		}
	}
}
