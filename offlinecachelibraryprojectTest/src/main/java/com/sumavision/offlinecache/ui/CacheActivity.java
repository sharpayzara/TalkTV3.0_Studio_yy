package com.sumavision.offlinecache.ui;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sumavision.offlinecachelibrary.R;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;

/**
 * 缓存中心
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public abstract class CacheActivity extends SherlockFragmentActivity implements
		OnPageChangeListener, OnClickListener, OnSharedPreferenceChangeListener {

	public LinearLayout menuLayout;
	private Button selectAll;
	private Button delete;
	private int from = 0;

	private final int CACHE_SDCARD = 0;
	private final int CACHE_PHONE = 1;
	private boolean hasSdCard = false;

	public abstract CachedFragment getCachedFragment();

	public abstract void onEditClickExtend(boolean state);

	public abstract CachingFragment getCachingFragment();

	/**
	 * 删除缓存播放历史
	 * 
	 * @param delList
	 */
	public abstract void deleteCachePlayHistory(
			CopyOnWriteArrayList<DownloadInfo> delList);

	/**
	 * 设置tab样式
	 * 
	 * @return
	 */
	public abstract CacheTabStyle getTabStyle();

	public abstract int getBottomBackground();

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("缓存中心");
		setContentView(R.layout.activity_offline_cache);
		sp = getSharedPreferences("notifyId", 0);
		sp.registerOnSharedPreferenceChangeListener(this);
		initViews();
		if (savedInstanceState != null) {
			curTabPosition = savedInstanceState.getInt("curTabPosition");
		}
		if (curTabPosition == 1) {
			viewPager.setCurrentItem(1);
		}

		if (getIntent().hasExtra("from")) {
			from = getIntent().getExtras().getInt("from");
			if (from == 1)
				updataHandler.sendEmptyMessageDelayed(REFRESH_ME, 100);

		}
	};

	/**
	 * 操作栏事件
	 */
	public void onActionClick() {
		switch (nowPage) {
		case 0:
			onEditClick(!cachingFragment.editState);
			cachingFragment.setState();
			break;
		case 1:
			onEditClick(!cachedFragment.editState);
			cachedFragment.setState();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updataHandler.sendEmptyMessageDelayed(REFRESH_CACHING, 100);
	}

	private int curTabPosition;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curTabPosition", curTabPosition);
	}

	ViewPager viewPager;
	protected CachingFragment cachingFragment;
	protected CachedFragment cachedFragment;
	TextView txtSpace;

	PagerSlidingTabStrip pageTabs;

	@SuppressWarnings("deprecation")
	private void initViews() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		pageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		cachingFragment = getCachingFragment();
		fragments.add(cachingFragment);
		cachedFragment = getCachedFragment();
		fragments.add(cachedFragment);
		ArrayList<String> titles = new ArrayList<String>();
		titles.add(getString(R.string.cache_center_caching));
		titles.add(getString(R.string.cache_center_cached));
		MyPagerAdapter adapter = new MyPagerAdapter(
				getSupportFragmentManager(), titles, fragments);
		viewPager.setAdapter(adapter);
		pageTabs.setOnPageChangeListener(this);
		pageTabs.setViewPager(viewPager, -1);
		setTabsValue();

		int resId = getBottomBackground();
		if (resId > 0) {
			findViewById(R.id.bottom_layout).setBackgroundResource(resId);
		}
		menuLayout = (LinearLayout) findViewById(R.id.viewPager_bottom_menu);
		selectAll = (Button) findViewById(R.id.viewPager_bottom_menu_all);
		selectAll.setOnClickListener(this);
		delete = (Button) findViewById(R.id.viewPager_bottom_menu_delete);
		delete.setOnClickListener(this);

		txtSpace = (TextView) findViewById(R.id.spaceSize);
		txtSpace.setOnClickListener(this);
		int type = getCachePath();
		File path = null;
		String space = "";
		try {
			ArrayList<String> dirs = CommonUtils.getStoragePath(this);
			if (dirs.size() == 0) {
				space = String.format("SD卡  剩余：%1$sG / 总量：%2$sG", "0", "0");
				txtSpace.setText(space);
				return;
			}
			boolean isSdCard = false;
			if (type == 0) {
				if (dirs.size() > 1) {
					path = new File(dirs.get(1));
					isSdCard = true;
					hasSdCard = true;
				} else if (dirs.size() == 1
						&& Environment.isExternalStorageRemovable()) {
					path = new File(dirs.get(0));
					hasSdCard = true;
					isSdCard = true;
				} else {
					// space = String.format("SD卡  剩余：%1$sG / 总量：%2$sG", "0",
					// "0");
					// txtSpace.setText(space);
					// return;
					// 只有一个，并且为内置存储
					PreferencesUtils.putInt(this, null, "cache_path_type", 1);
					path = new File(dirs.get(0));
				}
			} else {
				if (dirs.size() > 1) {
					path = new File(dirs.get(0));
					isSdCard = false;
					hasSdCard = true;
				} else if (dirs.size() == 1
						&& !Environment.isExternalStorageRemovable()) {
					path = new File(dirs.get(0));
					isSdCard = false;
				} else {
					// space = String
					// .format("手机内存  剩余：%1$sG / 总量：%2$sG", "0", "0");
					// txtSpace.setText(space);
					// return;

					// 只有一个，并且为sd卡
					hasSdCard = true;
					PreferencesUtils.putInt(this, null, "cache_path_type", 0);
					path = new File(dirs.get(0));
				}
			}
			StatFs localStatFs = new StatFs(path.getPath());
			long l1 = localStatFs.getBlockCount();
			long l2 = localStatFs.getAvailableBlocks();
			long l3 = localStatFs.getBlockSize();
			long nSDTotalSize = (l1 * l3);
			long nSDFreeSize = (l2 * l3);
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			String allSize = decimalFormat.format(nSDTotalSize
					/ (1024 * 1024 * 1024.0));
			String freeSize = decimalFormat.format(nSDFreeSize
					/ (1024 * 1024 * 1024.0));

			if (isSdCard) {
				space = String.format("SD卡  剩余：%1$sG / 总量：%2$sG", freeSize,
						allSize);
			} else {
				space = String.format("手机内存  剩余：%1$sG / 总量：%2$sG", freeSize,
						allSize);
			}
			txtSpace.setText(space);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			txtSpace.setVisibility(View.GONE);
		}
	}

	private void setTabsValue() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		CacheTabStyle style = getTabStyle();
		pageTabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, style.textSize, dm));
		pageTabs.setTextColorResource(style.textColorRes);
		pageTabs.setIndicatorColorResource(style.selectedTextColorRes);
		pageTabs.setSelectedTextColorResource(style.selectedTextColorRes);
		pageTabs.setTabBackground(0);
		pageTabs.setBackgroundResource(style.backgroundRes > 0 ? style.backgroundRes
				: R.color.white);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	public void onEditClick(boolean state) {
		if (state) {
			menuLayout.setVisibility(View.VISIBLE);
		} else {
			menuLayout.setVisibility(View.GONE);
			selectedAll = false;
			selectAll.setText("全选");
		}
		onEditClickExtend(state);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPageSelected(int arg0) {
		nowPage = arg0;
		switch (arg0) {
		case 0:
			if (menuLayout != null
					&& menuLayout.getVisibility() == View.VISIBLE) {
				menuLayout.setVisibility(View.GONE);
				txtSpace.setVisibility(View.VISIBLE);
				selectedAll = false;
				selectAll.setText(R.string.cache_center_all);
			}
			if (cachingFragment.editState) {
				onEditClick(!cachingFragment.editState);
				cachingFragment.setState();
			}
			invalidateOptionsMenu();
			break;
		default:
			if (menuLayout != null
					&& menuLayout.getVisibility() == View.VISIBLE) {
				menuLayout.setVisibility(View.GONE);
				txtSpace.setVisibility(View.VISIBLE);
				selectedAll = false;
				selectAll.setText(R.string.cache_center_all);
			}
			if (cachedFragment.editState) {
				onEditClick(!cachedFragment.editState);
				cachedFragment.setState();
			}
			invalidateOptionsMenu();
			break;
		}

	}

	public int nowPage = 0;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.spaceSize) {
			showCachePathDialog();
		}
		if (v.getId() == R.id.viewPager_bottom_menu_delete) {
			Log.e("delete", "@onHere_" + nowPage);
			switch (nowPage) {
			case 0:
				cachingFragment.deleteSelectedItem();
				break;
			case 1:
				cachedFragment.deleteSelectedItem();
				deleteCachePlayHistory(cachedFragment.downloadInfosWaitDelete);
				break;
			default:
				break;
			}
			if (selectedAll) {
				selectedAll = false;
				selectAll.setText("全选");
			}
		} else if (v.getId() == R.id.viewPager_bottom_menu_all) {
			Log.e("all", "@onHere_" + nowPage);
			selectedAll = !selectedAll;
			if (selectedAll) {
				selectAll.setText("取消全选");
				switch (nowPage) {
				case 0:
					cachingFragment.selectAllItem();
					break;
				case 1:
					cachedFragment.selectAllItem();
					break;
				default:
					break;
				}

			} else {
				selectAll.setText("全选");
				switch (nowPage) {
				case 0:
					cachingFragment.disSelectAllItem();
					break;
				case 1:
					cachedFragment.disSelectAllItem();
					break;
				default:
					break;
				}
			}

		}
	}

	public boolean selectedAll = false;

	private final int REFRESH_ME = 1;
	private final int REFRESH_CACHING = 2;
	private Handler updataHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case REFRESH_ME:
				viewPager.setCurrentItem(1);
				break;
			case REFRESH_CACHING:
				try {
					cachingFragment.refreshData();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		};
	};

	public void setSelectText(boolean isAll) {
		if (isAll) {
			selectedAll = true;
			selectAll.setText("取消全选");
		} else {
			selectedAll = false;
			selectAll.setText("全选");
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		cachedFragment.refreshReceiver();
		cachingFragment.refreshReceiver();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (sp != null) {
			sp.unregisterOnSharedPreferenceChangeListener(this);
		}
	}

	private void showCachePathDialog() {
		ArrayList<String> dirs = CommonUtils.getStoragePath(this);
		// 只有一个也无需设置
		if (dirs.size() == 0 || dirs.size() == 1) {
			Toast.makeText(this, "手机只有一种存储空间，无需设置", Toast.LENGTH_SHORT).show();
			return;
		}
		AccessDownload accessDownload = AccessDownload.getInstance(this);
		ArrayList<DownloadInfo> downloadInfos = accessDownload
				.queryDownloadInfo();
		if (downloadInfos != null && downloadInfos.size() > 0) {
			Toast.makeText(this, "当前有正在缓存的任务，无法设置", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		boolean[] checkedItem = new boolean[] { false, false };
		checkedItem[getCachePath()] = true;
		final String[] items = new String[] {
				getStorageRoom(new File(dirs.get(1)), 0),
				getStorageRoom(new File(dirs.get(0)), 1) };
		builder.setSingleChoiceItems(items, getCachePath(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setCachePath(which);
						if (which == 0) {
							txtSpace.setText(items[0].replace("\n", " "));
						} else {
							txtSpace.setText(items[1].replace("\n", " "));
						}
						dialog.dismiss();
					}
				});
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private int getCachePath() {
		return PreferencesUtils.getInt(this, null, "cache_path_type");
	}

	private void setCachePath(int value) {
		if (value == CACHE_SDCARD || value == CACHE_PHONE) {
			PreferencesUtils.putInt(this, null, "cache_path_type", value);
		}
	}

	private String getStorageRoom(File path, int type) {
		StatFs localStatFs = new StatFs(path.getPath());
		long l1 = localStatFs.getBlockCount();
		long l2 = localStatFs.getAvailableBlocks();
		long l3 = localStatFs.getBlockSize();
		long nSDTotalSize = (l1 * l3);
		long nSDFreeSize = (l2 * l3);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String allSize = decimalFormat.format(nSDTotalSize
				/ (1024 * 1024 * 1024.0));
		String freeSize = decimalFormat.format(nSDFreeSize
				/ (1024 * 1024 * 1024.0));
		String space = "";
		if (type == 0) {
			space = String
					.format("SD卡\n剩余：%1$sG / 总量：%2$sG", freeSize, allSize);
		} else {
			space = String.format("手机内存\n剩余：%1$sG / 总量：%2$sG", freeSize,
					allSize);
		}
		return space;
	}
}
