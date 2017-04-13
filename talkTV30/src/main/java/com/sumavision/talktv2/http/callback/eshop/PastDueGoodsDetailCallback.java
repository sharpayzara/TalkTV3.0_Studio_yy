package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.PastDueGoodsDetailParser;
import com.sumavision.talktv2.http.json.eshop.PastDueGoodsDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnPastDueGoodsDetailListener;

/**
 * 过期物品详情回调
 * 
 * @author suma-hpb
 * 
 */
public class PastDueGoodsDetailCallback extends BaseCallback {

	private long userGoodsId;
	private OnPastDueGoodsDetailListener mListener;

	/**
	 * @param errorListener
	 * @param userGoodsId
	 * @param mListener
	 */
	public PastDueGoodsDetailCallback(OnHttpErrorListener errorListener,
			long userGoodsId, OnPastDueGoodsDetailListener mListener) {
		super(errorListener);
		this.userGoodsId = userGoodsId;
		this.mListener = mListener;
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetPastDueGoodsDetail(mParser.errCode, mParser.good);
		}

	}

	PastDueGoodsDetailParser mParser = new PastDueGoodsDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new PastDueGoodsDetailRequest(userGoodsId).make();
	}

}
