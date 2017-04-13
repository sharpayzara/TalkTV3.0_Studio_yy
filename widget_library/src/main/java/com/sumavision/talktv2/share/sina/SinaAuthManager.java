package com.sumavision.talktv2.share.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sumavision.talktv2.bean.SinaData;
import com.sumavision.talktv2.share.AccessTokenKeeper;

/**
 * 新浪微博授权：注意回调onActivityResult
 * 
 * @author suma-hpb
 * 
 */
public class SinaAuthManager {
	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;

	public void auth(final Activity activity,
			final OnSinaBindLoginListener listener) {
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(activity);
		if (mAccessToken.isSessionValid()) {
			setSinaInfo(activity, listener, mAccessToken);
			return;
		}
		mWeiboAuth = new WeiboAuth(activity, SinaData.CUSTOMER_KEY,
				SinaData.REDIRECT_URL, SinaData.SCOPE);
		mSsoHandler = new SsoHandler(activity, mWeiboAuth);
		mSsoHandler.authorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				// Toast.makeText(activity, "授权异常 : ",
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onComplete(Bundle arg0) {
				Oauth2AccessToken mAccessToken = Oauth2AccessToken
						.parseAccessToken(arg0);
				if (mAccessToken.isSessionValid()) {
					setSinaInfo(activity, listener, mAccessToken);
				} else {
					// String code = arg0.getString("code");
					// String message = "微博授权失败";
					// if (!TextUtils.isEmpty(code)) {
					// // message = message + "\nObtained the code: " + code;
					// Log.e("微博授权", message + "\nObtained the code: " + code);
					// }
					// Toast.makeText(activity, message,
					// Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onCancel() {
				// Toast.makeText(activity, "取消", Toast.LENGTH_LONG)
				// .show();

			}
		});
	}

	/**
	 * 解绑、取消授权
	 */
	public void logout(final Activity activity) {
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(activity);
		LogoutAPI logout = new LogoutAPI(mAccessToken);
		logout.logout(new RequestListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				// Toast.makeText(activity,
				// "cancel Auth exception : " + arg0.getMessage(),
				// Toast.LENGTH_LONG).show();

			}

			@Override
			public void onComplete(String arg0) {
				AccessTokenKeeper.clear(activity);

			}
		});
	}

	private void setSinaInfo(final Activity activity,
			final OnSinaBindLoginListener listener,
			Oauth2AccessToken mAccessToken) {
		SinaData.id = mAccessToken.getUid();
		SinaData.accessToken = mAccessToken.getToken();
		SinaData.expires_in = String.valueOf(mAccessToken.getExpiresTime());
		AccessTokenKeeper.writeAccessToken(activity, mAccessToken);

		if (listener != null) {
			listener.OnSinaBindSucceed();
		}
	}

	/**
	 * activity授权、分享回调
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public void getUserInfo(final Context context,
			final OnSinaUserInfoListener listener) {
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(context);
		UsersAPI mUsersAPI = new UsersAPI(token);
		mUsersAPI.show(Long.parseLong(token.getUid()), new RequestListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				Log.e("sina-userinfo",
						"getUserinfo exception : " + arg0.getMessage());
				if (listener != null) {
					listener.OnGetSinaUserInfo(false, "", "");
				}
			}

			@Override
			public void onComplete(String arg0) {
				if (!TextUtils.isEmpty(arg0)) {
					User user = User.parse(arg0);
					SinaData.name = user.name;
					SinaData.description = user.description;
					if (user.gender.equals("m")) {
						SinaData.gender = 1;
					} else if (user.gender.equals("f")) {
						SinaData.gender = 2;
					}
					SinaData.icon = user.profile_image_url;
					if (listener != null) {
						listener.OnGetSinaUserInfo(true, user.name,
								user.profile_image_url);
					}
				}

			}
		});
	}

	public interface OnSinaBindLoginListener {
		public void OnSinaBindSucceed();
	}

	public interface OnSinaUserInfoListener {
		public void OnGetSinaUserInfo(boolean getInfoSucc, String name,
				String iconUrl);
	}

}
