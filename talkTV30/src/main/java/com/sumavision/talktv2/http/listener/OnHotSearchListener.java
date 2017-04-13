package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.KeyWordData;

/**
 * 获取热门搜索词ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnHotSearchListener {

	/**
	 * 获取热门搜索词
	 * 
	 * @param errCode
	 * @param keyList
	 */
	public void getHotKeyList(int errCode, ArrayList<KeyWordData> keyList);
}
