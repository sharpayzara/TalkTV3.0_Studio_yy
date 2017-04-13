package com.sumavision.talktv2.http.listener.epay;

import com.sumavision.talktv2.bean.OrderResultData;

/**
 * 查询支付订单ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnOrderPaySearchListener {

	/**
	 * 查询支付订单结果
	 * 
	 * @param errCode
	 * @param orderResult
	 */
	public void onSearchOrderPay(int errCode, OrderResultData orderResult);
}
