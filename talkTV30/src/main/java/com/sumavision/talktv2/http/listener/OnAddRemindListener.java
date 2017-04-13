package com.sumavision.talktv2.http.listener;

/**
 * 添加预约ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnAddRemindListener {
	public void addRemindResult(int errCode, String msg, int remindId);
}
