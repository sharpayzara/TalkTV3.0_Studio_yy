package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.AppData;

public interface OnRecommendAppListener {
	public void getRecommendAppList(int errCode, ArrayList<AppData> appList);
}
