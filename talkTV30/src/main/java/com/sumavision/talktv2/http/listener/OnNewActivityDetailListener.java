package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.ActivityData;

public interface OnNewActivityDetailListener {
	public void getNewAcivityDeail(int errCode, ActivityData activityData);
}
