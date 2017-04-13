package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.EntityGoodDetailParser;
import com.sumavision.talktv2.http.json.eshop.EntityGoodDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnEntityGoodDetailListener;

/**
 * 实体物品详情回调
 * 
 * @author suma-hpb
 * 
 */
public class EntityGoodDetailCallback extends BaseCallback {

	private ExchangeGood goods;
	private OnEntityGoodDetailListener mListener;

	/**
	 * @param errorListener
	 * @param goods
	 * @param mListener
	 */
	public EntityGoodDetailCallback(OnHttpErrorListener errorListener,
			ExchangeGood goods, OnEntityGoodDetailListener mListener) {
		super(errorListener);
		this.goods = goods;
		this.mListener = mListener;
		mParser = new EntityGoodDetailParser(goods);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.OnEntityGoodDetail(mParser.errCode, mParser.info);
		}

	}

	EntityGoodDetailParser mParser;

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new EntityGoodDetailRequest(goods.userGoodsId).make();
	}

}
