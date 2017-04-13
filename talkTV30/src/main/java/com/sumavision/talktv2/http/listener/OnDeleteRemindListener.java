package com.sumavision.talktv2.http.listener;

/**
 * 取消预约
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnDeleteRemindListener {

	/**
	 * 取消预约
	 * 
	 * @param errCode
	 */
	public void deleteRemindResult(int errCode);
}
