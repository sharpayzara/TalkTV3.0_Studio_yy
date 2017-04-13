package com.sumavision.talktv2.http.listener.epay;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.PayHistoryData;

/**
 * 充值记录ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnPayHistoryListener {

	/**
	 * 充值记录列表
	 * 
	 * @param errCode
	 * @param historyList
	 */
	public void onUpdatePayHistory(int errCode,
			ArrayList<PayHistoryData> historyList);
}
