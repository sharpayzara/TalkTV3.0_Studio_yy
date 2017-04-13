package com.sumavision.talktv2.http.callback.epay;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.epay.PayHistoryParser;
import com.sumavision.talktv2.http.json.epay.PayHistoryRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.epay.OnPayHistoryListener;

/**
 * 充值记录回调
 * 
 * @author suma-hpb
 * 
 */
public class PayHistoryCallback extends BaseCallback {

	private OnPayHistoryListener mListener;

	public PayHistoryCallback(OnHttpErrorListener errorListener,
			OnPayHistoryListener mListener) {
		super(errorListener);
		this.mListener = mListener;
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onUpdatePayHistory(mParser.errCode, mParser.historyList);
		}

	}

	PayHistoryParser mParser = new PayHistoryParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new PayHistoryRequest().make();
	}

}
