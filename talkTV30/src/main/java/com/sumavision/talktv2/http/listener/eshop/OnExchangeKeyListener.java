package com.sumavision.talktv2.http.listener.eshop;

import com.sumavision.talktv2.bean.EKeyData;

/**
 * 获取兑换秘钥ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnExchangeKeyListener {
	/**
	 * 获取兑换秘钥
	 * 
	 * @param errCode
	 * @param eKeyData
	 */
	public void onGetExchangeKey(int errCode, EKeyData eKeyData);
}
