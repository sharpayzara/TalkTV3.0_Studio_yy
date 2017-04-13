package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.VersionData;

/**
 * 版本信息获取ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnAppNewVersionListener {
	/**
	 * 版本信息获取
	 * 
	 * @param errCode
	 * @param version
	 */
	public void getNewVersion(int errCode, String msg,VersionData version);
}
