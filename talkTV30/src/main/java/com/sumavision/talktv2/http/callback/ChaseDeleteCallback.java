package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ChaseDeleteRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnChaseDeleteListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 取消追剧回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChaseDeleteCallback extends BaseCallback {

	private OnChaseDeleteListener listener;
	private String pid;

	public ChaseDeleteCallback(String pid, OnHttpErrorListener errorListener,
			OnChaseDeleteListener listener) {
		super(errorListener);
		this.pid = pid;
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
			listener.chaseDeleteResult(parser.errCode);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new ChaseDeleteRequest(pid).make();
	}

}
