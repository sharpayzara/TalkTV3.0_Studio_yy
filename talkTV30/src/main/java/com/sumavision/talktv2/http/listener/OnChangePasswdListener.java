package com.sumavision.talktv2.http.listener;

/**
 * 修改密码ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnChangePasswdListener {
	/**
	 * 修改密码结果
	 * 
	 * @param errCode
	 * @param errMsg
	 */
	public void onChangePasswd(int errCode, String errMsg);
}
