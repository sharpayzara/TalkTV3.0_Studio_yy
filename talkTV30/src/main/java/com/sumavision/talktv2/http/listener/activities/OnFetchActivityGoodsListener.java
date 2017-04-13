package com.sumavision.talktv2.http.listener.activities;

import com.sumavision.talktv2.bean.ExchangeGood;

/**
 * 活动奖品悄悄领取ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnFetchActivityGoodsListener {

	/**
	 * 活动奖品悄悄领取结果
	 * 
	 * @param errCode
	 * @param errMsg
	 * @param exchangeGood
	 */
	public void onFetchActivityGoods(int errCode, String errMsg,
			ExchangeGood exchangeGood);
}
