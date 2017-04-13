package com.sumavision.talktv2.share.sina;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sumavision.talktv2.bean.SinaData;
import com.sumavision.talktv2.share.AccessTokenKeeper;

public class SinaShareManager implements IWeiboHandler.Response {
	public interface OnSinaShareListener {
		public void onSinaShare(boolean succeed);
	}

	private IWeiboShareAPI mWeiboShareAPI = null;
	Activity mActivity;
	boolean weiboAppInstalled;
	private StatusesAPI mStatusesAPI;
	OnSinaShareListener listener;

	public SinaShareManager(Activity mActivity, OnSinaShareListener listener) {
		this.mActivity = mActivity;
		this.listener = listener;
	}

	/**
	 * oncreate 调用
	 * 
	 * @param savedInstanceState
	 */
	public void init(Bundle savedInstanceState) {
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity,
				SinaData.CUSTOMER_KEY);
		// mWeiboShareAPI.registerApp();
		weiboAppInstalled = mWeiboShareAPI.isWeiboAppInstalled();
		if (weiboAppInstalled && savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(mActivity.getIntent(), this);
		}
	}

	/**
	 * onNewIntent 调用
	 * 
	 * @param intent
	 */
	public void onNewIntent(Intent intent) {
		if (weiboAppInstalled) {
			mWeiboShareAPI.handleWeiboResponse(intent, this);
		}
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(mActivity, "微博分享成功", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(mActivity, "取消微博分享", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(mActivity, "微博分享失败", Toast.LENGTH_LONG).show();
			break;
		}
	}

	/**
	 * 分享
	 * 
	 * @param content
	 * @param bitmap
	 */
	public void share(final String content, final Bitmap bitmap) {
		// if (weiboAppInstalled && mWeiboShareAPI.isWeiboAppSupportAPI()) {
		// int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
		// if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
		// sendMessage(true, content, bitmap);
		// } else {
		// sendMessage(false, content, bitmap);
		// }
		// } else {
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(mActivity);
		noWeiboAppShare(content, bitmap, mAccessToken);
		// }
	}

	private void noWeiboAppShare(String content, Bitmap bitmap,
			Oauth2AccessToken token) {
		mStatusesAPI = new StatusesAPI(token);
		ShareListener shareLis = new ShareListener();
		if (bitmap == null) {
			mStatusesAPI.update(content, "", "", shareLis);
		} else {
			mStatusesAPI.upload(content, bitmap, "", "", shareLis);
		}
	}

	@SuppressWarnings("unused")
	private void sendMessage(boolean multi, String content, Bitmap bitmap) {
		TextObject textObject = new TextObject();
		textObject.text = content;

		ImageObject imageObject = null;
		if (bitmap != null) {
			imageObject = new ImageObject();
			imageObject.setImageObject(bitmap);
		}
		if (multi) {
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			weiboMessage.textObject = textObject;
			if (imageObject != null) {
				weiboMessage.imageObject = imageObject;
			}
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;
			mWeiboShareAPI.sendRequest(request);
		} else {
			WeiboMessage weiboMessage = new WeiboMessage();
			weiboMessage.mediaObject = textObject;
			if (imageObject != null) {
				weiboMessage.mediaObject = imageObject;
			}
			SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.message = weiboMessage;
			mWeiboShareAPI.sendRequest(request);
		}

	}

	class ShareListener implements RequestListener {
		@Override
		public void onWeiboException(WeiboException arg0) {
			// Toast.makeText(mActivity, "share exception : " +
			// arg0.getMessage(),
			// Toast.LENGTH_LONG).show();
			if (listener != null) {
				listener.onSinaShare(false);
			}
		}

		@Override
		public void onComplete(String arg0) {
			if (listener != null) {
				listener.onSinaShare(true);
			}
//			Toast.makeText(mActivity, "新浪微博分享成功", Toast.LENGTH_SHORT).show();

		}
	}
}
