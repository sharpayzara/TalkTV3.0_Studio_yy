package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.StarData;

public interface OnStarDetailListener {
	public void getStatDetail(int errCode, StarData star);
}
