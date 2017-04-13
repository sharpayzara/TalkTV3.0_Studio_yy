package com.sumavision.talktv2.http.listener.activities;

import com.sumavision.talktv2.bean.PlayNewData;

/**
 * 旧版活动详情ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnActivityDetailListener {
	/**
	 * 旧版活动详情结果
	 * 
	 * @param errCode
	 * @param playNewData
	 * @param errMsg
	 */
	public void getActivityDetail(int errCode, PlayNewData playNewData,
			String errMsg);
}
