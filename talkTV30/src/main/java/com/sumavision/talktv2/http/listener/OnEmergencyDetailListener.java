package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.EmergencyDetailData;

public interface OnEmergencyDetailListener {

	public void getEmergencyDetail(int errCode, EmergencyDetailData eDetaildata);
}
