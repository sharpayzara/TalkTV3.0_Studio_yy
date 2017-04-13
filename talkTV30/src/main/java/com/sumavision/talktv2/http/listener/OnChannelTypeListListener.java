package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ChannelType;

public interface OnChannelTypeListListener {
	public void channelTypeList(int errCode, ArrayList<ChannelType> list);
}
