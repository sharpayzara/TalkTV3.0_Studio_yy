package com.sumavision.talktv2.bean;

import java.util.List;

/**
 * 
 * @author guopeng
 * 
 */
public class RecommendPageNewData {

	private List<RecommendData> recommend;
	private List<ColumnData> column;
	private List<AppData> apps;
	public int activityPicType;//1=第一条数据有大横图，2=第一条数据有大竖图，3=都是普通图
	
	public List<AppData> getApps() {
		return apps;
	}

	public void setApps(List<AppData> apps) {
		this.apps = apps;
	}

	public List<RecommendData> getRecommend() {
		return recommend;
	}

	public void setRecommend(List<RecommendData> recommend) {
		this.recommend = recommend;
	}

	public void setColumn(List<ColumnData> column) {
		this.column = column;
	}

	public List<ColumnData> getColumn() {
		return column;
	}
}
