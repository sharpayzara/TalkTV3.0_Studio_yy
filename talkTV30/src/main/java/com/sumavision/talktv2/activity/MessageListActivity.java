package com.sumavision.talktv2.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.ClearActionProvider;
import com.sumavision.talktv2.fragment.CommonDialogFragment.OnCommonDialogListener;
import com.sumavision.talktv2.fragment.PrivateMsgFragment;
import com.sumavision.talktv2.service.TvBaiduPushMessageReceiver;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 消息列表
 * 
 * @author suma-hpb
 * 
 */
public class MessageListActivity extends BaseActivity implements
		OnCommonDialogListener {
	PrivateMsgFragment mPrivateMsgFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mPrivateMsgFragment = PrivateMsgFragment.newInstance();
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.pushMessage, Constants.key_privateMsg, false);
		getSupportActionBar().setTitle(getString(R.string.message_list));
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, mPrivateMsgFragment).commit();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(TvBaiduPushMessageReceiver.NOTIFICATION_ID);
	}

	ClearActionProvider clearActionProvider;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_clear, menu);
		clearActionProvider = (ClearActionProvider) menu.findItem(
				R.id.action_clear).getActionProvider();

		clearActionProvider.setDialogTitle("清空消息");
		clearActionProvider.setOnClickListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onPositiveButtonClick() {
		mPrivateMsgFragment.clearAll();

	}

	@Override
	public void onNeutralButtonClick() {

	}

	@Override
	public void onNegativeButtonClick() {

	}
}
