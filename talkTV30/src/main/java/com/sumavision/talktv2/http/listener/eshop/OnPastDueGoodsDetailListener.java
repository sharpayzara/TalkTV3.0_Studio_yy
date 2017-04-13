package com.sumavision.talktv2.http.listener.eshop;

import com.sumavision.talktv2.bean.ExchangeGood;

/**
 * 过期物品详情ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnPastDueGoodsDetailListener {

	/**
	 * 过期物品详情结果
	 * 
	 * @param errCode
	 * @param good
	 */
	public void onGetPastDueGoodsDetail(int errCode, ExchangeGood good);
}
