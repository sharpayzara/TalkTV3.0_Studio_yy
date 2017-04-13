package com.sumavision.talktv2.activity;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.offlinecache.ui.CacheActivity;
import com.sumavision.offlinecache.ui.CacheTabStyle;
import com.sumavision.offlinecache.ui.CachedFragment;
import com.sumavision.offlinecache.ui.CachingFragment;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.talktv.videoplayer.dao.AccessProgram;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 缓存中心
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyCacheActivity extends CacheActivity {

	String scheme;
	int from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme_Light);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLogo(R.drawable.ic_action_back);
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.pushMessage, Constants.KEY_CACHE, false);
		if (getIntent().getData() != null) {
			scheme = getIntent().getData().getScheme();
		}
		from = getIntent().getIntExtra("from", 0);
	};

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("MyCacheActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyCacheActivity");
		super.onPause();
	}

	TextActionProvider editAction;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.cache_center, menu);
		editAction = (TextActionProvider) menu.findItem(R.id.action_edit)
				.getActionProvider();
		if (editAction != null){
			editAction.setShowText(R.string.action_edit);
			editAction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onActionClick();
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onEditClickExtend(boolean state) {
		if (state) {
			editAction.setShowText(R.string.cancel);
		} else {
			editAction.setShowText(R.string.action_edit);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if ((!TextUtils.isEmpty(scheme) || from == 1) && AppUtil.getNetStatus(this)) {
				Intent intent = new Intent(MyCacheActivity.this,
						SlidingMainActivity.class);
				startActivity(intent);
				finish();
			} else {
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((!TextUtils.isEmpty(scheme) || from == 1) && AppUtil.getNetStatus(this)) {
				Intent intent = new Intent(MyCacheActivity.this,
						SlidingMainActivity.class);
				startActivity(intent);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public CachedFragment getCachedFragment() {
		return CachedFragment.newInstance(PlayerActivity.class.getName());
	}

	@Override
	public CachingFragment getCachingFragment() {
		return CachingFragment.newInstance(getString(R.string.app_name),
				"tvfanphone");
	}

	@Override
	public void deleteCachePlayHistory(
			CopyOnWriteArrayList<DownloadInfo> delList) {
		for (DownloadInfo info : delList) {
			NetPlayData program = AccessProgram.getInstance(this)
					.findByProgramIdAndSubId(String.valueOf(info.programId),
							String.valueOf(info.subProgramId));
			if (program.id > 0) {
				AccessProgram.getInstance(this).delete(program);
			}
		}
	}

	@Override
	public CacheTabStyle getTabStyle() {
		CacheTabStyle ctStyle = new CacheTabStyle();
		ctStyle.selectedTextColorRes = R.color.navigator_bg_color;
		ctStyle.textColorRes = R.color.light_black;
		ctStyle.textSize = getResources().getInteger(R.integer.tab_text_size);
		return ctStyle;
	}

	@Override
	public int getBottomBackground() {
		return 0;
	}

}
