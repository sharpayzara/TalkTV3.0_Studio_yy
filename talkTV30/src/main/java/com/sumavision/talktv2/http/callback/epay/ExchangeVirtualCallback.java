package com.sumavision.talktv2.http.callback.epay;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.callback.BaseStringCallback;
import com.sumavision.talktv2.http.json.epay.ExchangeVirtualParser;
import com.sumavision.talktv2.http.json.epay.ExchangeVirtualRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.epay.OnExchangeVirtualListener;

/**
 * 兑换虚拟货币回调
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeVirtualCallback extends BaseStringCallback {

	private int type;
	private int diamondCount;
	private String key;
	private String comm;
	private OnExchangeVirtualListener mListener;

	/**
	 * @param errorListener
	 * @param type
	 * @param diamondCount
	 * @param key
	 * @param mListener
	 */
	public ExchangeVirtualCallback(OnHttpErrorListener errorListener, int type,
			int diamondCount, String key, String comm,
			OnExchangeVirtualListener mListener) {
		super(errorListener);
		this.type = type;
		this.diamondCount = diamondCount;
		this.key = key;
		this.comm = comm;
		this.mListener = mListener;
		setSecretKey(key);
		addRequestHeader("encrypt", "1");
		addRequestHeader("userId", String.valueOf(UserNow.current().userID));
	}

	@Override
	public String makeRequest() {
		return new ExchangeVirtualRequest(type, diamondCount, key, comm).make();
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.OnExchangeVirtualMoney(mParser.errCode,
					mParser.exchangeData);
		}

	}

	ExchangeVirtualParser mParser = new ExchangeVirtualParser();

	@Override
	public void parseNetworkRespose(String str) {
		try {
			mParser.parse(new JSONObject(str));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
