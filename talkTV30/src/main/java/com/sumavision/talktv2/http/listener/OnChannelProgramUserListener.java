package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ShortChannelData;

public interface OnChannelProgramUserListener {
	public void channelList(int errCode, ArrayList<ShortChannelData> channelList);
}
