package com.sumavision.talktv2.http.listener.eshop;

import com.sumavision.talktv2.bean.ReceiverInfo;

/**
 * 实体物品详情ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnEntityGoodDetailListener {

	/**
	 * 实体物品结果
	 * 
	 * @param errCode
	 * @param info
	 */
	public void OnEntityGoodDetail(int errCode, ReceiverInfo info);
}
