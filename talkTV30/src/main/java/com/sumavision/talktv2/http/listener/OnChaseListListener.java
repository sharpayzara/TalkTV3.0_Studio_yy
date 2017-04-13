package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ChaseData;

public interface OnChaseListListener {

	public void getChaseList(int errCode, int chaseCount,
			ArrayList<ChaseData> chaseList);
}
