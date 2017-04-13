package com.sumavision.talktv2.http.listener.activities;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.BadgeData;


public interface OnActivityJoinListener{
	public void getBadgeList(int errCode,ArrayList<BadgeData> badgeList);
}
