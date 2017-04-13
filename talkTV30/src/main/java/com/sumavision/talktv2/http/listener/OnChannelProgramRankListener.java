package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ChannelType;

public interface OnChannelProgramRankListener {
	public void channelProgramRankList(int errCode,
			ArrayList<ChannelType> channelTypeList);
}
