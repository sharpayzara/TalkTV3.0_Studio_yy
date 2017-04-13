package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.BindDeleteRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnBindDeleteListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 第三方账号解绑回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindDeleteCallback extends BaseCallback {

	private OnBindDeleteListener listener;

	int id;

	public BindDeleteCallback(OnHttpErrorListener errorListener,
			OnBindDeleteListener listener, int id) {
		super(errorListener);
		this.listener = listener;
		this.id = id;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (listener != null) {
			listener.bindDeleteResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new BindDeleteRequest(id).make();
	}

}
