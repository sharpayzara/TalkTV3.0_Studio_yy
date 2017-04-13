package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 节目搜索ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnSearchListener {
	/**
	 * 节目搜索
	 * 
	 * @param errCode
	 * @param programList
	 */
	public void getSearchProgramList(int errCode, int totalCount,
			ArrayList<VodProgramData> programList);
}
