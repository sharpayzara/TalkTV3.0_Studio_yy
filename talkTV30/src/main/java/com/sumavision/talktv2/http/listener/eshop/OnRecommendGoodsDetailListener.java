package com.sumavision.talktv2.http.listener.eshop;

import com.sumavision.talktv2.bean.ExchangeGood;

/**
 * 兑换物品详情ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnRecommendGoodsDetailListener {

	/**
	 * 兑换物品详情请求结果
	 * 
	 * @param errCode
	 * @param good
	 */
	public void onExchangeGoodsDetail(int errCode, ExchangeGood good);
}
