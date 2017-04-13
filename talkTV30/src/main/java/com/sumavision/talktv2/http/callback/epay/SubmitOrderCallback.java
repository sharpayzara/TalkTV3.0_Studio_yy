package com.sumavision.talktv2.http.callback.epay;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.epay.SubmitOrderParser;
import com.sumavision.talktv2.http.json.epay.SubmitOrderRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.epay.OnSubmitOrderListener;

/**
 * 提交订单回调
 * 
 * @author suma-hpb
 * 
 */
public class SubmitOrderCallback extends BaseCallback {

	private String mPayRuleId;
	private OnSubmitOrderListener mListener;

	/**
	 * @param errorListener
	 * @param mPayRuleId
	 * @param mListener
	 */
	public SubmitOrderCallback(OnHttpErrorListener errorListener,
			String mPayRuleId, OnSubmitOrderListener mListener) {
		super(errorListener);
		this.mPayRuleId = mPayRuleId;
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new SubmitOrderRequest(mPayRuleId).make();
	}

	SubmitOrderParser mParser = new SubmitOrderParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onSubmitOrder(mParser.errCode, mParser.product);
		}

	}

}
