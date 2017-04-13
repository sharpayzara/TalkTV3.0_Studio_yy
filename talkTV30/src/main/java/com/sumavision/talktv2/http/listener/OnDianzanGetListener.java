package com.sumavision.talktv2.http.listener;

/**
 * 点赞信息获取ui更新
 * 
 * @author suma-hpb
 * 
 */
public interface OnDianzanGetListener {

	/**
	 * 
	 * @param errCode
	 * @param type
	 *            1为赞,2为踩
	 */
	public void onGetDianzan(int errCode, int type);
}
