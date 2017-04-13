package com.sumavision.talktv2.http.listener;

/**
 * 登录ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnLogInListener {
	/**
	 * 登录结果
	 * 
	 * @param errCode
	 * @param changePoint
	 *            登录增加积分
	 * @param errMsg
	 */
	public void loginResult(int errCode, int changePoint, String errMsg);
}
