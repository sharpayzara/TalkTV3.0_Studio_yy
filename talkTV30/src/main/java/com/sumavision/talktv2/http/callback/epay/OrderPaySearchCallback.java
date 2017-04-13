package com.sumavision.talktv2.http.callback.epay;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.epay.OrderPaySearchParser;
import com.sumavision.talktv2.http.json.epay.OrderPaySearchReqeust;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.epay.OnOrderPaySearchListener;

/**
 * 支付订单查询回调
 * 
 * @author suma-hpb
 * 
 */
public class OrderPaySearchCallback extends BaseCallback {

	private long orderId;
	private OnOrderPaySearchListener mListener;

	/**
	 * @param errorListener
	 * @param orderId
	 * @param mListener
	 */
	public OrderPaySearchCallback(OnHttpErrorListener errorListener,
			long orderId, OnOrderPaySearchListener mListener) {
		super(errorListener);
		this.orderId = orderId;
		this.mListener = mListener;
	}

	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onSearchOrderPay(mParser.errCode, mParser.orderResult);
		}
	};

	OrderPaySearchParser mParser = new OrderPaySearchParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new OrderPaySearchReqeust(orderId).make();
	}
}
