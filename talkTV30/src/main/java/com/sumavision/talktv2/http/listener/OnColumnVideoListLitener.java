package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 推荐页的2 3 4 标签:ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnColumnVideoListLitener {
	/**
	 * 
	 * @param errCode
	 * @param totalCount
	 * @param programList
	 */
	public void columnVideoList(int errCode, int totalCount,
			ArrayList<VodProgramData> programList);
}
