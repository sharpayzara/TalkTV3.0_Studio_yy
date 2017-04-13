package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnProgramMicroVideoListener;

public class ProgramMicroVideoCallback extends BaseCallback {

	private OnProgramMicroVideoListener listener;

	public ProgramMicroVideoCallback(OnHttpErrorListener errorListener,
			OnProgramMicroVideoListener listener) {
		super(errorListener);
		this.listener = listener;
		addRequestHeader("Client-id", "2910002");
		addRequestHeader("Client-secret", "924c04a95b00e545");
	}

	String url = "";

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getMicroVideoUrl(url);
		}

	}

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		if (jsonObject.has("url")) {
			url = jsonObject.optString("url");
		}
	}

	@Override
	public JSONObject makeRequest() {
		return null;
	}

}
