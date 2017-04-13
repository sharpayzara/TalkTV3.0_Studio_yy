package com.sumavision.talktv2.http.listener.epay;

import com.sumavision.talktv2.bean.ExchangeData;

/**
 * 兑换虚拟货币ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnExchangeVirtualListener {

	/**
	 * 兑换虚拟货币结果
	 * 
	 * @param errCode
	 * @param exchangeData
	 */
	public void OnExchangeVirtualMoney(int errCode, ExchangeData exchangeData);
}
