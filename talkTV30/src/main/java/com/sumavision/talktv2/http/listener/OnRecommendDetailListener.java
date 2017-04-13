package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.RecommendPageNewData;

public interface OnRecommendDetailListener {
	public void getRecommendDetail(int errCode,
			RecommendPageNewData recommendData);
}
