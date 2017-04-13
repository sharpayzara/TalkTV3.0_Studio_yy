package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.ExchangeKeyParser;
import com.sumavision.talktv2.http.json.eshop.ExchangeKeyRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeKeyListener;

/**
 * 获取兑换物品秘钥回调
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeKeyCallback extends BaseCallback {

	public ExchangeKeyCallback(OnHttpErrorListener errorListener,
			OnExchangeKeyListener listener) {
		super(errorListener);
		this.mListener = listener;
	}

	private OnExchangeKeyListener mListener;

	public JSONObject makeRequest() {
		return new ExchangeKeyRequest().make();
	};

	ExchangeKeyParser mParser = new ExchangeKeyParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetExchangeKey(mParser.errCode, mParser.data);
		}

	}
}
