package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.BadgeData;

public interface OnMyBadgeListener {

	public void myBadgeResult(int errCode, ArrayList<BadgeData> badgeList);
}
