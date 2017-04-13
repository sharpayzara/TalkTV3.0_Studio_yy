package com.sumavision.talktv2.http.listener.interactive;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.interactive.InteractiveActivity;

public interface OnInteractiveActivityListListener {

	/**
	 * 
	 * @param errCode
	 * @param mInteractiveActivityList
	 *            获取的互动列表
	 */
	public void OnInteractiveActivityListResult(int errCode,
			ArrayList<InteractiveActivity> mInteractiveActivityList);
}
