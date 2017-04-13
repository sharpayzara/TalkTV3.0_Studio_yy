package com.sumavision.talktv2.http.listener.activities;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.PlayNewData;


public interface OnActivityListListener{
	public void getActivityList(int errCode,ArrayList<PlayNewData> activityList);
}
