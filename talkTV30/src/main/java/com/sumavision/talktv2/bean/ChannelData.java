package com.sumavision.talktv2.bean;

import java.util.ArrayList;

public class ChannelData {
	public ArrayList<NetPlayData> netPlayDatas = new ArrayList<NetPlayData>();
	public String channelName;
	public String channelId = "0";
	public CpData now;
	public ArrayList<CpData> cpList;
	public String shareProgramName;
}
