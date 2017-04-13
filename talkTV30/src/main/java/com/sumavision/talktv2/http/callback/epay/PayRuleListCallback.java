package com.sumavision.talktv2.http.callback.epay;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.epay.PayRuleListParser;
import com.sumavision.talktv2.http.json.epay.PayRuleListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.epay.OnPayRuleListListener;

/**
 * 充值规则列表回调
 * 
 * @author suma-hpb
 * 
 */
public class PayRuleListCallback extends BaseCallback {

	private OnPayRuleListListener mListener;

	public PayRuleListCallback(OnHttpErrorListener errorListener,
			OnPayRuleListListener mListener) {
		super(errorListener);
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new PayRuleListRequest().make();
	}

	PayRuleListParser mParser = new PayRuleListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onUpdatePayRuleList(mParser.errCode, mParser.payList);
		}

	}
}
