package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.UserNow;

/**
 * 绑定登录ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnBindLogInListener {

	/**
	 * 绑定登录结果
	 * 
	 * @param errCode
	 * @param user
	 */
	public void bindLogInResult(int errCode, String err, UserNow user);
}
