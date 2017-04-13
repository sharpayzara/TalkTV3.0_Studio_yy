package com.sumavision.talktv.videoplayer.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv2.utils.SmartBarUtils;
import com.sumavision.talktv2.utils.StatusBarUtils;

/**
 * dlna甩屏操作指南
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DLNAHelpActivity extends SherlockFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (SmartBarUtils.hasSmartBar()) {
			getWindow().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
			SmartBarUtils.setBackIcon(getActionBar(), getResources()
					.getDrawable(R.drawable.ic_action_back));
			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), false);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			getSupportActionBar().setDisplayUseLogoEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setLogo(R.drawable.ic_action_back);
			getSupportActionBar().setBackgroundDrawable(
					getResources().getDrawable(R.drawable.navigator_bg));
		}
		StatusBarUtils.setImmerseTheme(this, R.drawable.navigator_bg);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.help_list);
		setContentView(R.layout.activity_dlna_help);
		try {
			Class<?> webpUtilsClass = Class.forName(getPackageName()
					+ ".utils.WebpUtils");
			Method getBitmapMethod = webpUtilsClass.getMethod("getAssetBitmap",
					Context.class, String.class);
			Bitmap pic = (Bitmap) getBitmapMethod.invoke(webpUtilsClass, this,
					"webp/helplist.webp");
			int sdk = android.os.Build.VERSION.SDK_INT;
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				findViewById(R.id.helpimg).setBackgroundDrawable(
						new BitmapDrawable(getResources(), pic));
			} else {
				findViewById(R.id.helpimg).setBackground(
						new BitmapDrawable(getResources(), pic));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
