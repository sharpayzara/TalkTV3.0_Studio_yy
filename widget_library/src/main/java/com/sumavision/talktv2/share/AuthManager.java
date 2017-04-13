package com.sumavision.talktv2.share;

import android.app.Activity;
import android.content.Intent;

import com.sumavision.talktv2.bean.UmLoginInfo;
import com.sumavision.talktv2.bean.UmPlatformData;
import com.sumavision.talktv2.bean.WeiXinData;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;

public class AuthManager {
	private Activity mActivity;
	protected UMSocialService umService;
	private static AuthManager instance;

	public static AuthManager getInstance(Activity mActivity) {
		if (null == instance) {
			instance = new AuthManager(mActivity);
		}
		return instance;
	}

	private AuthManager(Activity mActivity) {
		umService = UMServiceFactory.getUMSocialService("com.umeng.login");
		this.mActivity = mActivity;
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity,
				UmPlatformData.QQ_APP_ID, UmPlatformData.QQ_APP_SERCRET);
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity,
				UmPlatformData.QQ_APP_ID, UmPlatformData.QQ_APP_SERCRET);
		// SinaSsoHandler mSinaSsoHandler = new SinaSsoHandler();
		UMWXHandler mWxHandler = new UMWXHandler(mActivity, WeiXinData.APP_ID,
				WeiXinData.APP_SERCRET);
		mWxHandler.setRefreshTokenAvailable(false);
		// mSinaSsoHandler.addToSocialSDK();
		qqSsoHandler.addToSocialSDK();
		qZoneSsoHandler.addToSocialSDK();
		// umService.getConfig().setSsoHandler(mSinaSsoHandler);
		umService.getConfig().setSsoHandler(qqSsoHandler);
		umService.getConfig().setSsoHandler(qZoneSsoHandler);
		umService.getConfig().setSsoHandler(mWxHandler);
		umService.getConfig().closeToast();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		UMSsoHandler ssoHandler = umService.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public void auth(final SHARE_MEDIA plat, final OnUMAuthListener listener) {
		// boolean isAuth = OauthHelper.isAuthenticatedAndTokenNotExpired(
		// mActivity, plat);
		// if (!isAuth) {
		umService.doOauthVerify(mActivity, plat, new AuthCallback(mActivity,
				plat, listener));
		// } else {
		// if (listener != null) {
		// Bundle bundle = new Bundle(6);
		// if(plat==SHARE_MEDIA.QQ){
		// bundle.putString("openid",
		// AccessTokenKeeper.readQQOpenId(mActivity));
		// bundle.putString("access_token",
		// AccessTokenKeeper.readQQAccessToken(mActivity));
		// }
		// listener.umAuthResult(plat, true, bundle);
		// }
		// }
	}

	/**
	 * 登录
	 * 
	 * @param plat
	 * @param listener
	 */
	public void login(final SHARE_MEDIA plat, final OnUMLoginListener listener) {
		// if (OauthHelper.isAuthenticatedAndTokenNotExpired(mActivity, plat)) {
		// getPlatformInfo(plat, listener);
		// } else {
		umService.doOauthVerify(mActivity, plat, new AuthCallback(mActivity,
				plat, new OnUMAuthListener() {

					@Override
					public void umAuthResult(SHARE_MEDIA platform,
							boolean authSucc, String oid, String token) {
						if (authSucc) {
							UmLoginInfo loginInfo = new UmLoginInfo();
							loginInfo.accessToken = token;
							loginInfo.openid = oid;
							getPlatformInfo(plat,loginInfo, listener);
						}
					}
				}));
		// }
	}

	public void logOut(final SHARE_MEDIA plat) {
		boolean isAuth = OauthHelper.isAuthenticatedAndTokenNotExpired(
				mActivity, plat);
		if (isAuth) {
			umService.deleteOauth(mActivity, plat,
					new SocializeClientListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(int status,
								SocializeEntity entity) {
							// if (status == 200) {
							// OauthHelper.removeTokenExpiresIn(mActivity,
							// plat);
							// Toast.makeText(mActivity, "注销成功",
							// Toast.LENGTH_SHORT).show();
							// } else {
							// Toast.makeText(mActivity, "注销失败",
							// Toast.LENGTH_SHORT).show();
							// }
						}
					});
		}
	}

	public void getPlatformInfo(final SHARE_MEDIA plat,final UmLoginInfo loginInfo,
			final OnUMLoginListener listener) {
		umService.getPlatformInfo(mActivity, plat, new UMDataListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				if (listener != null) {
					if (status == 200) {
						loginInfo.userName = info.get("screen_name").toString();
						loginInfo.gender = info.get("gender").toString();
						loginInfo.userIconUrl = info.get("profile_image_url")
								.toString();
					}
					listener.onUmLogin(plat, status == 200, loginInfo);
				}
			}
		});
	}
}
