package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.MailData;

public interface OnMailListListener {
	public void getMail(int errCode, int mailCount, ArrayList<MailData> mailList);
}
