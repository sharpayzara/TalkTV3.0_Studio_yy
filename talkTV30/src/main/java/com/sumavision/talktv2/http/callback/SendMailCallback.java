package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.SendMailParser;
import com.sumavision.talktv2.http.json.SendMailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSendMailListener;

/**
 * 发私信回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendMailCallback extends BaseCallback {

	private int userId;
	private OnSendMailListener listener;
	String content;
	String pic;

	public SendMailCallback(OnHttpErrorListener errorListener, int userId,
			OnSendMailListener listener, String content, String pic) {
		super(errorListener);
		this.userId = userId;
		this.listener = listener;
		this.content = content;
		this.pic = pic;
	}

	SendMailParser parser = new SendMailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.sendMailResult(parser.errCode, parser.mailCount,
					parser.mailList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new SendMailRequest(userId, content, pic).make();
	}

}
