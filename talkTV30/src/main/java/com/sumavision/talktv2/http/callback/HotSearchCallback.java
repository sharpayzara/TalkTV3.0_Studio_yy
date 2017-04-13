package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.HotSearchParser;
import com.sumavision.talktv2.http.json.HotSearchRequest;
import com.sumavision.talktv2.http.listener.OnHotSearchListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 热门搜索回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class HotSearchCallback extends BaseCallback {

	private OnHotSearchListener listener;

	public HotSearchCallback(OnHttpErrorListener errorListener,
			OnHotSearchListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	HotSearchParser parser = new HotSearchParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}
	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getHotKeyList(parser.errCode, parser.keyWordList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new HotSearchRequest().make();
	}

}
