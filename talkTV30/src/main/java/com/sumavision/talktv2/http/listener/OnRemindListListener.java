package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 预约列表
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnRemindListListener {

	/**
	 * 预约列表
	 * 
	 * @param errCode
	 * @param remindCount
	 * @param remindList
	 */
	public void getRemindList(int errCode, int remindCount,
			ArrayList<VodProgramData> remindList);
}
