package com.sumavision.talktv2.http.listener;

/**
 * 密码找回-校验验证码ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnCheckCodeCheckListener {

	/**
	 * 密码找回-校验验证码
	 * 
	 * @param errCode
	 * @param errMsg
	 */
	public void onCheckCode(int errCode, String errMsg);
}
