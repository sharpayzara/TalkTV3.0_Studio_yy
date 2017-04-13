package com.sumavision.talktv2.http.listener.interactive;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 竞猜节目列表接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnGuessProgramListListener {

	/**
	 * 竞猜节目回调
	 * 
	 * @param errCode
	 * @param programList
	 */
	public void onGetGuessProgramlist(int errCode,
			ArrayList<VodProgramData> programList);
}
