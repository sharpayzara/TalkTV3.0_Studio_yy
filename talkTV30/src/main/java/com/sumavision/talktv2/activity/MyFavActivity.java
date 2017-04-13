package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.ClearActionProvider;
import com.sumavision.talktv2.fragment.CommonDialogFragment.OnCommonDialogListener;
import com.sumavision.talktv2.fragment.MyFavFragment;
import com.sumavision.talktv2.service.LiveAlertService;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 收藏页
 * 
 * @author suma-hpb
 * 
 */
public class MyFavActivity extends BaseActivity implements
		OnCommonDialogListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav);
		getSupportActionBar().setTitle("收藏");
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.pushMessage, Constants.key_favourite, false);
		myFavFragment = MyFavFragment.newInstance();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, myFavFragment).commit();

	}

	MyFavFragment myFavFragment;
	ClearActionProvider mFavActionProvider;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		long pid = intent.getLongExtra("programId", 0);
		if (pid > 0) {
			myFavFragment.refreshVodFav();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			startAlertService();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startAlertService();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void startAlertService() {
		Intent intent = new Intent(this, LiveAlertService.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_clear, menu);
		mFavActionProvider = (ClearActionProvider) menu.findItem(
				R.id.action_clear).getActionProvider();

		mFavActionProvider.setOnClickListener(this);
		mFavActionProvider.setDialogTitle("清空收藏");
		return super.onCreateOptionsMenu(menu);
	}

	public void showAction() {
		if (mFavActionProvider != null) {
			mFavActionProvider.showOption();
		}
	}

	public void hideAction() {
		if (mFavActionProvider != null) {
			mFavActionProvider.hideOption();
		}
	}

	@Override
	public void onPositiveButtonClick() {
		myFavFragment.clearAll();
	}

	@Override
	public void onNeutralButtonClick() {

	}

	@Override
	public void onNegativeButtonClick() {

	}
}
