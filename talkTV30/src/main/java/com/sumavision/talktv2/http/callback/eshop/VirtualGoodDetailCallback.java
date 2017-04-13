package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.VirtualGoodDetailParser;
import com.sumavision.talktv2.http.json.eshop.VirtualGoodDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnVirtualGoodDetailListener;

/**
 * 虚拟物品详情回调
 * 
 * @author suma-hpb
 * 
 */
public class VirtualGoodDetailCallback extends BaseCallback {

	private ExchangeGood goods;
	private OnVirtualGoodDetailListener mListener;

	/**
	 * @param errorListener
	 * @param goods
	 * @param mListener
	 */
	public VirtualGoodDetailCallback(OnHttpErrorListener errorListener,
			ExchangeGood goods, OnVirtualGoodDetailListener mListener) {
		super(errorListener);
		this.goods = goods;
		this.mListener = mListener;
		mParser = new VirtualGoodDetailParser(goods);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onVirtualGoodDetail(mParser.errCode);
		}

	}

	VirtualGoodDetailParser mParser;

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new VirtualGoodDetailRequest(goods.userGoodsId, goods.ticket).make();
	}

}
