package com.sumavision.talktv2.bean;

import java.util.ArrayList;

public class ConcertData {
	public long id;
	public String title;
	public float price;
	public boolean hasBuy;
	public long startTime;
	public long endTime;
	public long payRuleId;
	public String content;
	public ArrayList<String> picName = new ArrayList<String>();
	public ArrayList<String> picUrl = new ArrayList<String>();
	public ArrayList<NetPlayData> videoList = new ArrayList<NetPlayData>();
}
