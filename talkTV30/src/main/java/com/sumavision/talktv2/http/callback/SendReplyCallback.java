package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.SendReplyParser;
import com.sumavision.talktv2.http.json.SendReplyRequest;
import com.sumavision.talktv2.http.json.SendReplyRequestNew;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSendReplyListener;

/**
 * 发表回复回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendReplyCallback extends BaseCallback {
	private OnSendReplyListener listener;

	public SendReplyCallback(OnHttpErrorListener errorListener,
			OnSendReplyListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	SendReplyParser parser = new SendReplyParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
				if (parser.userInfo.point > 0) {
					UserNow.current().setTotalPoint(parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
				}
			}
			listener.sendReplyResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new SendReplyRequestNew().make();
	}

}
