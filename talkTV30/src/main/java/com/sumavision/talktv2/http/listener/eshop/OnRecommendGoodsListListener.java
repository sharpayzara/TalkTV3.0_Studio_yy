package com.sumavision.talktv2.http.listener.eshop;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.HotGoodsBean;

/**
 * 推荐物品列表ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnRecommendGoodsListListener {

	/**
	 * 推荐物品列表结果
	 * 
	 * @param errCode
	 * @param shoplist
	 */
	public void OnGetRecommendGoodsList(int errCode,
			ArrayList<HotGoodsBean> hotGoodsList);
}
