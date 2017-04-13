package com.sumavision.talktv2.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {

	/**
	 * 设置沉浸式状态栏
	 * 
	 * @param activity
	 * @param resId
	 *            状态栏背景
	 */
	public static void setImmerseTheme(Activity activity, int resId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = activity.getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(resId);
	}

	/**
	 * miui6设置全透明状态栏
	 * 
	 * @param activity
	 */
	public static void setImmerseThemeOnMiui(Activity activity) {
		if (!isMiuiV6()) {
			return;
		}
		Window window = activity.getWindow();
		Class<?> clazz = window.getClass();
		try {
			int tranceFlag = 0;
			int darkModeFlag = 0;
			Class<?> layoutParams = Class
					.forName("android.view.MiuiWindowManager$LayoutParams");

			Field field = layoutParams
					.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
			tranceFlag = field.getInt(layoutParams);

			field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
			darkModeFlag = field.getInt(layoutParams);

			Method extraFlagField = clazz.getMethod("setExtraFlags", int.class,
					int.class);
			// 只需要状态栏透明
			extraFlagField.invoke(window, tranceFlag, tranceFlag);
			// 状态栏透明且黑色字体
			extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag
					| darkModeFlag);
			// 清除黑色字体
			// extraFlagField.invoke(window, 0, darkModeFlag);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static boolean isMiuiV6() {
		String value = "";
		try {
			Class<?> systemProp = Class.forName("android.os.SystemProperties");
			Method method = systemProp.getMethod("get", String.class);
			Object v = method.invoke(systemProp, "ro.miui.ui.version.name");
			if (v != null) {
				value = v.toString();
			}
			if (!TextUtils.isEmpty(value) && value.equals("V6")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
