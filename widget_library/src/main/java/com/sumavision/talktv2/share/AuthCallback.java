package com.sumavision.talktv2.share;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;

/**
 * 友盟授权回调
 * 
 * @author suma-hpb
 * 
 */
public class AuthCallback implements UMAuthListener {

	private Context context;
	private OnUMAuthListener listener;
	private SHARE_MEDIA platform;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (listener != null) {
				Bundle value = (Bundle) msg.obj;
				Boolean result = (value != null && !TextUtils.isEmpty(value
						.getString("uid")));
				String openid = "";
				String token = "";
				if (result) {
					try {
						openid = value.getString("openid");
						token = value.getString("access_token");
					} catch (Exception e) {
						openid = value.get("openid").toString();
						token = value.get("access_token").toString();
					}

				}
				listener.umAuthResult(platform, result, openid, token);
			}
		};
	};

	public AuthCallback(Context context, SHARE_MEDIA platform,
			OnUMAuthListener listener) {
		this.context = context;
		this.listener = listener;
		this.platform = platform;
	}

	@Override
	public void onCancel(SHARE_MEDIA arg0) {
		Toast.makeText(context, "授权取消", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onComplete(Bundle value, SHARE_MEDIA arg1) {
		Message msg = handler.obtainMessage();
		msg.what = 0;
		msg.obj = value;
		handler.sendMessage(msg);
	}

	@Override
	public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
		Toast.makeText(context, "授权异常", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(SHARE_MEDIA arg0) {
	}

}
