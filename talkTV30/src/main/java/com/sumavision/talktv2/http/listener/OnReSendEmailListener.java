package com.sumavision.talktv2.http.listener;

/**
 * 重新发送验证码邮件ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnReSendEmailListener {

	/**
	 * 重新发送验证码邮件
	 * 
	 * @param errCode
	 * @param errMsg
	 */
	public void onSendEmail(int errCode, String errMsg);
}
