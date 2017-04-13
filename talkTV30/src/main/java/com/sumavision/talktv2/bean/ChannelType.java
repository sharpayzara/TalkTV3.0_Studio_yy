package com.sumavision.talktv2.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ChannelType implements Serializable {

	private static final long serialVersionUID = 1L;
	public ArrayList<ShortChannelData> channelList;
	public String channelTypeName;
	public int channleTypeId;
	// 状态，1选中，0未选中
	public int type = 0;
}
