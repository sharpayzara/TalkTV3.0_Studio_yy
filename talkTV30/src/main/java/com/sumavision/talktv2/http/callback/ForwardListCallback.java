package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ForwardListParser;
import com.sumavision.talktv2.http.json.ForwardListRequest;
import com.sumavision.talktv2.http.listener.OnForwardListListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 转发列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ForwardListCallback extends BaseCallback {
	private OnForwardListListener listener;
	private int first;
	private int count;

	public ForwardListCallback(int first, int count,
			OnHttpErrorListener errorListener, OnForwardListListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	ForwardListParser parser = new ForwardListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.forwardListResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ForwardListRequest(first, count).make();
	}

}
