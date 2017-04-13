package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.RecommendDetailParser;
import com.sumavision.talktv2.http.json.RecommendDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRecommendDetailListener;

/**
 * 推荐数据回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RecommendDetailCallback extends BaseCallback {

	private OnRecommendDetailListener listener;

	public RecommendDetailCallback(OnHttpErrorListener errorListener,
			OnRecommendDetailListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	RecommendDetailParser parser = new RecommendDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getRecommendDetail(parser.errCode, parser.recommendData);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new RecommendDetailRequest().make();
	}

}
