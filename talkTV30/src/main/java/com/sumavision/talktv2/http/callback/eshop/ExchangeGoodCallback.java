package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.callback.BaseStringCallback;
import com.sumavision.talktv2.http.json.eshop.ExchangeGoodsParser;
import com.sumavision.talktv2.http.json.eshop.ExchangeGoodsRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeGoodsListener;

/**
 * 兑换物品回调
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeGoodCallback extends BaseStringCallback {

	private ExchangeGood mExchangeGood;
	private OnExchangeGoodsListener mListener;
	private String key;

	/**
	 * @param errorListener
	 * @param mExchangeGood
	 * @param mListener
	 * @param mParser
	 */
	public ExchangeGoodCallback(OnHttpErrorListener errorListener,
			ExchangeGood mExchangeGood, String key,
			OnExchangeGoodsListener mListener) {
		super(errorListener);
		this.mExchangeGood = mExchangeGood;
		this.mListener = mListener;
		this.key = key;
		mParser = new ExchangeGoodsParser(mExchangeGood);
		setSecretKey(key);
		addRequestHeader("encrypt", "1");
		addRequestHeader("userId", String.valueOf(UserNow.current().userID));
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onExchangeGood(mParser.errCode);
		}

	}

	ExchangeGoodsParser mParser;

	@Override
	public void parseNetworkRespose(String str) {
		try {
			mParser.parse(new JSONObject(str));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String makeRequest() {
		return new ExchangeGoodsRequest(mExchangeGood.hotGoodsId, key).make();
	}

}
