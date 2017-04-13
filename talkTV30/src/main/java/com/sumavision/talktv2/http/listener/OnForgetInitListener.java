package com.sumavision.talktv2.http.listener;

/**
 * 密码找回ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnForgetInitListener {

	/**
	 * 密码找回--获取邮箱
	 * 
	 * @param errCode
	 * @param errMsg
	 * @param email
	 */
	public void onGetEmailInFindPasswd(int errCode, String errMsg, String email);
}
