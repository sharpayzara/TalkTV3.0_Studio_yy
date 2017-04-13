package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.MailListParser;
import com.sumavision.talktv2.http.json.MailListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnMailListListener;
/**
 * 
 * @author suma-hpb
 * @version 
 * @description
 */
public class MailCallback extends BaseCallback {

	private int userId;
	private int first;
	private int count;
	private OnMailListListener listener;

	public MailCallback(OnHttpErrorListener errorListener, int userId,
			int first, int count, OnMailListListener listener) {
		super(errorListener);
		this.userId = userId;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	MailListParser parser = new MailListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}
	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getMail(parser.errCode, parser.mailCount, parser.mailList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new MailListRequest(userId, first, count).make();
	}

}
