package com.sumavision.talktv2.bean;

public class DiscoveryData
{
	public static final int TYPE_BADGE = 1;//徽章抽奖
	public static final int TYPE_GOODS = 2;//商城限时
	public static final int TYPE_FESTIVAL = 3;//节日抽奖
	
	public int id;
	public String name;
	public String pic;
	public int point;//积分
	public int goodsId;
	public int count;//活动参与人数
	public int discoveryType = -1;//活动类型
	
}
