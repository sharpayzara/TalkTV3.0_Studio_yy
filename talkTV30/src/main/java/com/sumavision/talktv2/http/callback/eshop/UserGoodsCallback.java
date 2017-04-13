package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.UserGoodsParser;
import com.sumavision.talktv2.http.json.eshop.UserGoodsRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnUserGoodsListener;

/**
 * 用户礼品列表回调
 * 
 * @author suma-hpb
 * 
 */
public class UserGoodsCallback extends BaseCallback {

	private OnUserGoodsListener mListener;

	public UserGoodsCallback(OnHttpErrorListener errorListener,
			OnUserGoodsListener mListener) {
		super(errorListener);
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new UserGoodsRequest().make();
	}

	UserGoodsParser mParser = new UserGoodsParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetUserGoodsList(mParser.errCode, mParser.goodsList);
		}

	}
}
