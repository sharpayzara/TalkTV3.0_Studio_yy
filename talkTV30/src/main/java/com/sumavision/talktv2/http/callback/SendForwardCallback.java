package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.SendForwardParser;
import com.sumavision.talktv2.http.json.SendForwardRequest;
import com.sumavision.talktv2.http.json.SendForwardRequestNew;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSendFowardListener;

/**
 * 转发评论回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendForwardCallback extends BaseCallback {
	private OnSendFowardListener listener;

	public SendForwardCallback(OnHttpErrorListener errorListener,
			OnSendFowardListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	SendForwardParser parser = new SendForwardParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.sendForwardResult(parser.errCode);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new SendForwardRequestNew().make();
	}

}
