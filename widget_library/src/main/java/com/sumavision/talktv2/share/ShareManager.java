package com.sumavision.talktv2.share;

import android.app.Activity;

import com.sumavision.talktv2.bean.WeiXinData;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 分享管理
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ShareManager {
	private Activity mActivity;
	private UMSocialService umSocialService;
	private static ShareManager instance;

	public static ShareManager getInstance(Activity mActivity) {
		if (null == instance) {
			instance = new ShareManager(mActivity);
		}
		return instance;
	}

	private ShareManager(Activity mActivity) {
		umSocialService = AuthManager.getInstance(mActivity).umService;
		this.mActivity = mActivity;

	}

	public UMSsoHandler getSsoHandler(int requestCode) {
		return AuthManager.getInstance(mActivity).umService.getConfig()
				.getSsoHandler(requestCode);
	}

	// public void sinaShare(String title, String content, UMImage image,
	// OnUMShareListener listener) {
	// SinaShareContent shareContent = new SinaShareContent();
	// shareContent.setShareContent(content);
	// if (image != null) {
	// shareContent.setShareImage(image);
	// umSocialService.setShareImage(image);
	// }
	// shareContent.setTitle(title);
	// umSocialService.setShareContent(content);
	// umSocialService.setShareMedia(shareContent);
	// umSocialService.directShare(mActivity, SHARE_MEDIA.SINA,
	// new ShareCallback(listener));
	// }

	public void qqShare(String title, String content, UMImage image,
			String contentUrl, OnUMShareListener listener) {
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(content);
		qqShareContent.setTitle(title);
		if (image != null) {
			qqShareContent.setShareImage(image);
		}
		qqShareContent.setTargetUrl(contentUrl);
		umSocialService.setShareMedia(qqShareContent);
		umSocialService.directShare(mActivity, SHARE_MEDIA.QQ,
				new ShareCallback(listener));
	}

	public void cleanListener() {
		umSocialService.getConfig().cleanListeners();
	}

	public void qzoneShare(String title, String content, UMImage image,
			String contentUrl, OnUMShareListener listener) {
		QZoneShareContent qzoneShareContent = new QZoneShareContent();
		qzoneShareContent.setShareContent(content);
		qzoneShareContent.setTitle(title);
		if (image != null) {
			qzoneShareContent.setShareImage(image);
		}
		qzoneShareContent.setTargetUrl(contentUrl);
		umSocialService.setShareMedia(qzoneShareContent);
		umSocialService.directShare(mActivity, SHARE_MEDIA.QZONE,
				new ShareCallback(listener));
	}

	/**
	 * 微信或朋友圈分享
	 * 
	 * @param shareWX
	 *            true-微信分享，false-朋友圈分享
	 * @param contentUrl
	 * @param content
	 * @param image
	 * @param listener
	 */
	public void weixinShare(boolean shareWX, String contentUrl, String title,
			String content, UMImage image, OnUMShareListener listener) {
		SHARE_MEDIA plat = shareWX ? SHARE_MEDIA.WEIXIN
				: SHARE_MEDIA.WEIXIN_CIRCLE;
		UMWXHandler wxHandler = new UMWXHandler(mActivity, WeiXinData.APP_ID,
				WeiXinData.APP_SERCRET);
		if (!shareWX) {
			wxHandler.setToCircle(true);
		}
		wxHandler.setTargetUrl(contentUrl);
		wxHandler.addToSocialSDK();
		wxHandler.setTitle(title);
		ShareCallback callback = new ShareCallback(listener);
		umSocialService.setShareContent(content);
		if (image != null) {
			umSocialService.setShareMedia(image);
		}
		umSocialService.directShare(mActivity, plat, callback);
	}

}
