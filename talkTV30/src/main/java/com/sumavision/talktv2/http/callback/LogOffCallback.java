package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.LogoffParser;
import com.sumavision.talktv2.http.json.LogoffRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnLogOffListener;

/**
 * 电视粉账号注销回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class LogOffCallback extends BaseCallback {
	private OnLogOffListener listener;

	public LogOffCallback(OnHttpErrorListener errorListener,
			OnLogOffListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	LogoffParser parser = new LogoffParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.logOffResult(parser.errCode);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new LogoffRequest().make();
	}

}
