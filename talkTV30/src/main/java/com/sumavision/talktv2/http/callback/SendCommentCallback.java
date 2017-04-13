package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.SendCommentRequest;
import com.sumavision.talktv2.http.json.SendReplyRequestNew;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSendCommentListener;

import de.greenrobot.event.EventBus;

/**
 * 发表评论回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendCommentCallback extends BaseCallback {

	private String topicId;
	private OnSendCommentListener listener;

	public SendCommentCallback(OnHttpErrorListener errorListener,
			String topicId, OnSendCommentListener listener) {
		super(errorListener);
		this.topicId = topicId;
		this.listener = listener;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
//			if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
//				if (parser.userInfo.point > 0) {
//					UserNow.current().setTotalPoint(parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//				}
//			}
			listener.sendCommentResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new SendCommentRequest(topicId).make();
	}

}
