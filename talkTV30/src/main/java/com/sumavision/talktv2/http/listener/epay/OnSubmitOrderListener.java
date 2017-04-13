package com.sumavision.talktv2.http.listener.epay;

import com.suamvision.data.Product;

/**
 * 提交订单ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnSubmitOrderListener {

	/**
	 * 提交订单结果
	 * 
	 * @param errCode
	 * @param product
	 */
	public void onSubmitOrder(int errCode, Product product);
}
