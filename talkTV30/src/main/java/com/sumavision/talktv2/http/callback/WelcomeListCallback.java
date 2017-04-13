package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.WelComeListParser;
import com.sumavision.talktv2.http.json.WelcomeListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnWelcomeListListener;

/**
 * 欢迎页数据回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class WelcomeListCallback extends BaseCallback {

	private int first;
	private int count;
	private OnWelcomeListListener listener;

	public WelcomeListCallback(OnHttpErrorListener errorListener, int first,
			int count, OnWelcomeListListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	WelComeListParser parser = new WelComeListParser();

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getWelcomeList(parser.errCode, parser.welcomeList);
		}
	}

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new WelcomeListRequest(first, count).make();
	}

}
