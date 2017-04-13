package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.BadgeDetailData;

public interface OnBadgeDetailListener {

	/**
	 * 徽章详情返回
	 * 
	 * @param errCode
	 * @param badgeDetail
	 */
	public void getBadgeDetail(int errCode, BadgeDetailData badgeDetail);
}
