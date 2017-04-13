package com.sumavision.talktv2.http.listener.epay;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.DiamondData;

/**
 * 充值规则列表ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnPayRuleListListener {
	/**
	 * 充值规则列表
	 * 
	 * @param errCode
	 * @param payList
	 */
	public void onUpdatePayRuleList(int errCode, ArrayList<DiamondData> payList);
}
