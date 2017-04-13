package com.sumavision.talktv2.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author chengbaodong 2013-9-15 上午10:34:12
 */
public class AppUtil {
	/**
	 * wifi情况下mac地址获取
	 * 
	 * @param context
	 * @return
	 */
	public static String getMac(Context context) {
		String mac = "";
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			WifiInfo info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
		}
		return mac;
	}

	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).toString();
		else
			return null;
	}

	public static void clearOldLogo() {
		try {
			Runtime.getRuntime().exec(
					"rm -r " + Constants.SDCARD_FLASH_FOLDER);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param context
	 * @param name
	 *            MetaData name
	 * @return
	 */
	public static String getMetaData(Context context, String name) {
		if (context != null) {
			ApplicationInfo info;
			try {
				info = context.getPackageManager().getApplicationInfo(
						context.getPackageName(), PackageManager.GET_META_DATA);
				return info.metaData.get(name).toString();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static String getImei(Context context) {
		String result;
		if(context == null){
			result = "111222333444555";
			return result;
		}
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		result = manager.getDeviceId();
		if (TextUtils.isEmpty(result)) {
			result = PreferencesUtils.getString(context, null, Constants.virtualImei, "");
			if (TextUtils.isEmpty(result)){
				result = getVirtualImei();
				PreferencesUtils.putString(context,null,Constants.virtualImei,result);
			}
		}
		return result;
	}
	private static String getVirtualImei(){
		StringBuilder result = new StringBuilder("1234567");
		for(int i=0; i<8; i++){
			result.append((int)(Math.random()*10));
		}
		return result.toString();
	}

	/**
	 * 判断是否为平板
	 * 
	 * @return
	 */
	public static boolean isPad(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		double x = Math.pow(dm.widthPixels, 2);
		double y = Math.pow(dm.heightPixels, 2);
		// 屏幕尺寸
		double screenInches = Math.sqrt(x + y);
		double screenSize = screenInches / (160 * dm.density);
		// 大于6尺寸则为Pad
		if (screenSize >= 6.0) {
			return true;
		}
		return false;
	}

	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static boolean getNetStatus(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;
	}

	public static String getAppVersionId(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			return null;
		}

	}

	/**
	 * 服务是否正在运行
	 * 
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceInfo = activityManager
				.getRunningServices(100);
		for (RunningServiceInfo info : serviceInfo) {
			String name = info.service.getClassName();
			if (name.equals(serviceName)) {
				return true;
			}
		}
		return false;

	}

	public static String getPackageName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			return packInfo.packageName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	public static boolean hasPlugin(Activity c, String packageName) {
		PackageInfo pi;
		try {
			pi = c.getPackageManager().getPackageInfo(packageName, 0);
			// pi =
			// c.getPackageManager().getPackageInfo("com.sumavision.talktv2",
			// 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pManager = c.getPackageManager();
			List<ResolveInfo> apps = pManager.queryIntentActivities(
					resolveIntent, 0);

			Log.e("AppUtils", "step - 1" + packageName);

			ResolveInfo ri = null;
			try {
				Log.e("AppUtils", "step - 2" + packageName);
				ri = apps.iterator().next();
			} catch (NoSuchElementException e) {
				Log.e("AppUtils", "step - 3" + packageName);
				return false;
			}
			if (ri != null) {
				Log.e("AppUtils", "step - 4" + packageName);
				return true;
			} else {
				Log.e("AppUtils", "step - 5" + packageName);
				return false;
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Log.e("AppUtils", "step - 6");
			return false;
		}
	}

	public static boolean isEmail(String strEmail) {
		String strPattern = "^([a-z0-9A-Z]+[-|\\._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	public static boolean isUserName(String userName) {
		String strPattern = "^([u4e00-u9fa5]|[ufe30-uffa0]|[a-zA-Z0-9_]){3,12}$";

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(userName);
		return m.matches();
	}

	public static void clearDir(String path) {
		try {
			Runtime.getRuntime().exec("rm -r " + path);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("clearDir", path);
	}

	// android的versionCode获取
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void hideKeyoard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void showKeyoard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInputFromInputMethod(activity
				.getCurrentFocus().getWindowToken(),
				InputMethodManager.SHOW_FORCED);
	}

	public static boolean canReadSim(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
		boolean readsim = false;
		switch (tm.getSimState()) { // getSimState()取得sim的状态 有下面6中状态
		case TelephonyManager.SIM_STATE_ABSENT:
			readsim = false;
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			readsim = false;
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			break;
		case TelephonyManager.SIM_STATE_READY:
			readsim = true;
			break;
		}
		return readsim;
	}
}
