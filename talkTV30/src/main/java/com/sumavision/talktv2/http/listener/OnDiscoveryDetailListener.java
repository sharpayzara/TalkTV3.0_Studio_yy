package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.DiscoveryData;

public interface OnDiscoveryDetailListener
{
	public void onDiscoveryDetail(int errCode, ArrayList<DiscoveryData> datas);
}
