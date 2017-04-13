package com.sumavision.talktv2.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

/**
 * 统一prefer数据存储
 * 
 * @author suma-hpb
 * 
 */
public class PreferencesUtils {

	public static String PREFERENCE_NAME = "tvfan";// 默认sp地址

	public static SharedPreferences getSharedPreferences(Context context,
			String preName) {
		SharedPreferences settings = null;
		if (null == preName || preName.equals("")) {
			settings = context.getSharedPreferences(PREFERENCE_NAME,
					Context.MODE_PRIVATE);
		} else {
			settings = context.getSharedPreferences(preName,
					Context.MODE_PRIVATE);
		}
		return settings;
	}

	public static void clearAll(Context context, String preName) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}

	public static void remove(Context context, String preName, String key) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * put string preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to modify
	 * @param value
	 *            The new value for the preference
	 * @return True if the new values were successfully written to persistent
	 *         storage.
	 */
	public static boolean putString(Context context, String preName,
			String key, String value) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	@SuppressLint("NewApi")
	public static boolean putStringSet(Context context, String preName,
									String key, Set<String> value) {
		if (Build.VERSION.SDK_INT>=11){
			SharedPreferences settings = getSharedPreferences(context, preName);
			SharedPreferences.Editor editor = settings.edit();
			editor.putStringSet(key, value);
			return editor.commit();
		} else {
			return false;
		}
	}

	@SuppressLint("NewApi")
	public static Set<String> getStringSet(Context context, String preName, String key,
								   Set<String> defaultValue) {
		if (Build.VERSION.SDK_INT>=11){
			SharedPreferences settings = getSharedPreferences(context, preName);
			Set<String> result = new HashSet<>();
			return settings.getStringSet(key, result);
		} else {
			return new HashSet<String>();
		}
	}

	/**
	 * get string preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @return The preference value if it exists, or null. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a string
	 * @see #getString(Context, String, String)
	 */
	public static String getString(Context context, String preName, String key) {
		return getString(context, preName, key, "");
	}

	/**
	 * get string preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @param defaultValue
	 *            Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a string
	 */
	public static String getString(Context context, String preName, String key,
			String defaultValue) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		return settings.getString(key, defaultValue);
	}

	/**
	 * put int preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to modify
	 * @param value
	 *            The new value for the preference
	 * @return True if the new values were successfully written to persistent
	 *         storage.
	 */
	public static boolean putInt(Context context, String preName, String key,
			int value) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * get int preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @return The preference value if it exists, or 0. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a int
	 * @see #getInt(Context, String, int)
	 */
	public static int getInt(Context context, String preName, String key) {
		return getInt(context, preName, key, 0);
	}

	/**
	 * get int preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @param defaultValue
	 *            Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a int
	 */
	public static int getInt(Context context, String preName, String key,
			int defaultValue) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		return settings.getInt(key, defaultValue);
	}

	/**
	 * put long preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to modify
	 * @param value
	 *            The new value for the preference
	 * @return True if the new values were successfully written to persistent
	 *         storage.
	 */
	public static boolean putLong(Context context, String preName, String key,
			long value) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	/**
	 * get long preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @return The preference value if it exists, or -1. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a long
	 * @see #getLong(Context, String, long)
	 */
	public static long getLong(Context context, String preName, String key) {
		return getLong(context, preName, key, -1);
	}

	/**
	 * get long preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @param defaultValue
	 *            Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a long
	 */
	public static long getLong(Context context, String preName, String key,
			long defaultValue) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		return settings.getLong(key, defaultValue);
	}

	/**
	 * put float preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to modify
	 * @param value
	 *            The new value for the preference
	 * @return True if the new values were successfully written to persistent
	 *         storage.
	 */
	public static boolean putFloat(Context context, String preName, String key,
			float value) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}

	/**
	 * get float preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @return The preference value if it exists, or -1. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a float
	 * @see #getFloat(Context, String, float)
	 */
	public static float getFloat(Context context, String preName, String key) {
		return getFloat(context, preName, key, -1);
	}

	/**
	 * get float preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @param defaultValue
	 *            Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a float
	 */
	public static float getFloat(Context context, String preName, String key,
			float defaultValue) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		return settings.getFloat(key, defaultValue);
	}

	/**
	 * put boolean preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to modify
	 * @param value
	 *            The new value for the preference
	 * @return True if the new values were successfully written to persistent
	 *         storage.
	 */
	public static boolean putBoolean(Context context, String preName,
			String key, boolean value) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	/**
	 * get boolean preferences, default is false
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @return The preference value if it exists, or false. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean
	 * @see #getBoolean(Context, String, boolean)
	 */
	public static boolean getBoolean(Context context, String preName, String key) {
		return getBoolean(context, preName, key, false);
	}

	/**
	 * get boolean preferences
	 * 
	 * @param context
	 * @param preName
	 * @param key
	 *            The name of the preference to retrieve
	 * @param defaultValue
	 *            Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean
	 */
	public static boolean getBoolean(Context context, String preName,
			String key, boolean defaultValue) {
		SharedPreferences settings = getSharedPreferences(context, preName);
		return settings.getBoolean(key, defaultValue);
	}
}
