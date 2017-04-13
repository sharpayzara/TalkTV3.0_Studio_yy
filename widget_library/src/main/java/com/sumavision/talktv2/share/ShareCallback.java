package com.sumavision.talktv2.share;

import android.os.Handler;
import android.os.Message;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

public class ShareCallback implements SnsPostListener {
	private OnUMShareListener listener;

	public ShareCallback(OnUMShareListener listener) {
		super();
		this.listener = listener;
	}

	private String platform;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (listener != null) {
				int code = (Integer) msg.obj;
				listener.umShareResult(platform,
						code == StatusCode.ST_CODE_SUCCESSED,
						code == StatusCode.ST_CODE_ERROR_CANCEL);
			}
		};
	};

	@Override
	public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
		Message msg = handler.obtainMessage();
		msg.obj = arg1;
		platform = arg0.name();
		handler.sendMessage(msg);
	}

	@Override
	public void onStart() {

	}

}
