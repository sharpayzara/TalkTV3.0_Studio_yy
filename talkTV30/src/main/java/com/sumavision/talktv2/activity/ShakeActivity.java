package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.fragment.ShakeFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 摇一摇
 * 
 * @author cx
 * */
public class ShakeActivity extends BaseActivity {
	
	private TextActionProvider uploadAction;
	private ShakeFragment shakeFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_detail);
		
		initView();
	}
	
	private void initView() { 
		getSupportActionBar().setTitle("摇佳片");
		addFragment();
	}
	
	private void addFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		shakeFragment = ShakeFragment.newInstance();
		transaction.replace(R.id.activity_detail_layout, shakeFragment, "content").commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_detail, menu);
		uploadAction = (TextActionProvider) menu.findItem(R.id.action_badge).getActionProvider();
		if (uploadAction != null){
			uploadAction.setShowText(R.string.upload);
			uploadAction.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MobclickAgent.onEvent(ShakeActivity.this, "yjpshangchuan");
					if (UserNow.current().userID <= 0) {
						startActivityForResult(new Intent(ShakeActivity.this,LoginActivity.class),101);
					} else {
						startActivity(new Intent(ShakeActivity.this, UploadActivity.class));
					}
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101 && resultCode == RESULT_OK){
			startActivity(new Intent(ShakeActivity.this, UploadActivity.class));
		}
	}
}
