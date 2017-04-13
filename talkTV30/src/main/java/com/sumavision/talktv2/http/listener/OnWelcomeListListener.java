package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.WelcomeData;

/**
 * 欢迎数据ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnWelcomeListListener {
	/**
	 * 
	 * @param errCode 错误码
	 * @param welcomeList 欢迎数据列表
	 */
	public void getWelcomeList(int errCode, ArrayList<WelcomeData> welcomeList);
}
