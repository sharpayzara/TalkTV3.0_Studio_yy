package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.RecommendAppParser;
import com.sumavision.talktv2.http.json.RecommendAppRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRecommendAppListener;

/**
 * 应用推荐回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RecommendAppCallback extends BaseCallback {

	private int first;
	private int count;
	private OnRecommendAppListener listener;

	public RecommendAppCallback(OnHttpErrorListener errorListener, int first,
			int count, OnRecommendAppListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	RecommendAppParser parser = new RecommendAppParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getRecommendAppList(parser.errCode, parser.appList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new RecommendAppRequest(first, count).make();
	}

}
