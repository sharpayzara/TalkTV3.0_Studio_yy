package com.sumavision.talktv2.bean;

import java.util.ArrayList;

public class RecommendCommonData
{
	public int id;
	public int type;
	public String intro;
	public boolean subject;
	public String name;
	public String title;
	public String pic;
	public String icon;
	public String webUrl;
	public String videoPath;
	public String videoPathHigh;
	public String videoPathSuper;
	public int columnType;
	public int columnPicType;
	public int tempId;//首页用作节目id
	public int subId;//子节目id （311）
	
	public String userName;
	public String userPic;
	public int evaluate,showPic;
	
	public ArrayList<NetPlayData> netPlayDatas = null;
	private ColumnData columnData;//仅用作title
	
	public ColumnData getColumnData()
	{
		return columnData;
	}
	public void setColumnData(ColumnData columnData)
	{
		this.columnData = columnData;
	}

	public int pType;//节目类型
	public boolean skipToWeb;
	public int tvfanProgId,tvfanProgSubId;
	public boolean isAd;//jvxiao广告标识
	public Object adObj;
	public String adId;
}
