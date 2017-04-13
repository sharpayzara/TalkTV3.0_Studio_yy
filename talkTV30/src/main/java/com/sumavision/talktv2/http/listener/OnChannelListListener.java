package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ShortChannelData;

public interface OnChannelListListener {

	public void channelList(int errCode, int channelCount,
			ArrayList<ShortChannelData> channelList);
}
