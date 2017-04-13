package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.StarDetailParser;
import com.sumavision.talktv2.http.json.StarDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnStarDetailListener;

/**
 * 明星信息回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StarDetailCallback extends BaseCallback {
	private OnStarDetailListener listener;
	int stagerId;

	public StarDetailCallback(OnHttpErrorListener errorListener, int stagerId,
			OnStarDetailListener listener) {
		super(errorListener);
		this.listener = listener;
		this.stagerId = stagerId;
	}

	StarDetailParser parser = new StarDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getStatDetail(parser.errCode, parser.starData);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new StarDetailRequest(stagerId).make();
	}

}
