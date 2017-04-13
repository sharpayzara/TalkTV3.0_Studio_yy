package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.MailData;

/**
 * 发私信ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnSendMailListener {
	public void sendMailResult(int errCode,int mailCount, ArrayList<MailData> mailList);
}
