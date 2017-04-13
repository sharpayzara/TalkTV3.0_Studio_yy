package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.MailData;

public interface OnMyPrivateMsgListener {
	/**
	 * 私信获取
	 * 
	 * @param errCode
	 * @param mailCount
	 *            私信总数
	 * @param mailList
	 */
	public void getMyPrivateMessage(int errCode, int mailCount,
			ArrayList<MailData> mailList);
}
