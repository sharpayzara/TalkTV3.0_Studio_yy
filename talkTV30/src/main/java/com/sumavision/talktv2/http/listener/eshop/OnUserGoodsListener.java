package com.sumavision.talktv2.http.listener.eshop;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ExchangeGood;

/**
 * 用户礼品列表ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnUserGoodsListener {

	/**
	 * 用户礼品列表结果
	 * 
	 * @param errCode
	 * @param noGetList
	 *            未领取列表
	 * @param gotList
	 *            已领取列表
	 */
	public void onGetUserGoodsList(int errCode,
			ArrayList<ExchangeGood> goodsList);
}
