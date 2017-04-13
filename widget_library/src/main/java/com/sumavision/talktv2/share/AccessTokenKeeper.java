package com.sumavision.talktv2.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 该类定义了微博授权时所需要的参数。
 * 
 * @author SINA
 * @since 2013-10-07
 */
public class AccessTokenKeeper {
	private static final String PREFERENCES_NAME = "tvfan_sina_token";
	private static final String QQ_PREFERENCES_NAME = "tvfan_qq_token";

	private static final String KEY_UID = "uid";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_QQ_OPENID = "openId";
	private static final String KEY_QQ_ACCESS_TOKEN = "qq_access_token";
	private static final String KEY_QQ_EXPIRES_IN = "qq_expires_in";

	/**
	 * 保存 Token 对象到 SharedPreferences。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * @param token
	 *            Token 对象
	 */
	public static void writeAccessToken(Context context, Oauth2AccessToken token) {
		if (null == context || null == token) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(KEY_UID, token.getUid());
		editor.putString(KEY_ACCESS_TOKEN, token.getToken());
		editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
		editor.commit();
	}

	public static void writeQQInfo(Context context, String openId,
			String accessToken, long expiresIn) {

		SharedPreferences pref = context.getSharedPreferences(
				QQ_PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(KEY_QQ_OPENID, openId);
		editor.putString(KEY_QQ_ACCESS_TOKEN, accessToken);
		editor.putLong(KEY_QQ_EXPIRES_IN, expiresIn);
		editor.commit();
	}

	public static String readQQOpenId(Context context) {
		return PreferencesUtils.getString(context, QQ_PREFERENCES_NAME,
				KEY_QQ_OPENID);
	}

	public static void clearQQInfo(Context context) {
		PreferencesUtils.clearAll(context, QQ_PREFERENCES_NAME);
	}

	public static String readQQAccessToken(Context context) {
		return PreferencesUtils.getString(context, QQ_PREFERENCES_NAME,
				KEY_QQ_ACCESS_TOKEN);
	}

	public static long readQQExpiresIn(Context context) {
		return PreferencesUtils.getLong(context, QQ_PREFERENCES_NAME,
				KEY_QQ_EXPIRES_IN);
	}

	/**
	 * 从 SharedPreferences 读取 Token 信息。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * 
	 * @return 返回 Token 对象
	 */
	public static Oauth2AccessToken readAccessToken(Context context) {
		if (null == context) {
			return null;
		}

		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		token.setUid(pref.getString(KEY_UID, ""));
		token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
		token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));
		return token;
	}

	/**
	 * 清空 SharedPreferences 中sina Token信息。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 */
	public static void clear(Context context) {
		if (null == context) {
			return;
		}

		PreferencesUtils.clearAll(context, PREFERENCES_NAME);
	}
}
