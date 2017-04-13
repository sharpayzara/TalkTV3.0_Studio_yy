package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.BadgeData;

/**
 * 添加关注
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnAddGuanzhuListener {
	/**
	 * 
	 * @param errCode
	 * @param badgeList
	 *            徽章列表
	 */
	public void addGuanzhuResult(int errCode, ArrayList<BadgeData> badgeList);
}
