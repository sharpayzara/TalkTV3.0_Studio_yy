package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.RecommendActivityData;

public interface OnRecommendActivityListener {
	public void getRecommendActivityData(int errCode,
			RecommendActivityData recommendActivityData);
}
