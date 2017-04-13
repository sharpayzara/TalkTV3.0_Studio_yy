package com.sumavision.talktv2.http.listener.activities;

import com.sumavision.talktv2.bean.Good;

/**
 * 刮奖ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnActivityShakeListener {

	/**
	 * 刮奖结果
	 * 
	 * @param errCode
	 * @param activityGood
	 */
	public void getActivityGood(int errCode,String errmsg, Good activityGood);
}
