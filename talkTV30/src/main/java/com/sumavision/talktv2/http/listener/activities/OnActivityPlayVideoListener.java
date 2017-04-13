package com.sumavision.talktv2.http.listener.activities;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.BadgeData;

/**
 * 活动视频播放页面接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnActivityPlayVideoListener {
	/**
	 * 视频播放结果
	 * 
	 * @param errCode
	 * @param badgeList
	 */
	public void playVideoResult(int errCode, ArrayList<BadgeData> badgeList);
}
