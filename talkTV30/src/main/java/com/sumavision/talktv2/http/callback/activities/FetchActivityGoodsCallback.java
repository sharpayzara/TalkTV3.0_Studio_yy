package com.sumavision.talktv2.http.callback.activities;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsParser;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.activities.OnFetchActivityGoodsListener;

import org.json.JSONObject;

/**
 * 活动奖品悄悄领取回调
 * 
 * @author suma-hpb
 * 
 */
public class FetchActivityGoodsCallback extends BaseCallback {

	private long goodsId;
	private long activityId;
	private OnFetchActivityGoodsListener mListener;
	private String imei;

	/**
	 * @param errorListener
	 * @param goodsId
	 * @param activityId
	 * @param mListener
	 */
	public FetchActivityGoodsCallback(OnHttpErrorListener errorListener,
			long goodsId, long activityId, String imei,
			OnFetchActivityGoodsListener mListener) {
		super(errorListener);
		this.goodsId = goodsId;
		this.activityId = activityId;
		this.mListener = mListener;
		this.imei = imei;
	}

	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onFetchActivityGoods(mParser.errCode, mParser.errMsg,
					mParser.exchangeGood);
		}
	};

	FetchActivityGoodsParser mParser = new FetchActivityGoodsParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new FetchActivityGoodsRequest(activityId, goodsId, imei).make();
	}

}
