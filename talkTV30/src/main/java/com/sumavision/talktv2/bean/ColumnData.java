package com.sumavision.talktv2.bean;

import java.util.ArrayList;
import java.util.List;

public class ColumnData {

	public int id;
	public int type;
	public int offset = 0;
	public int pageCount = 10;
	public int programCount;
	public int playTimes = 0;
	public String intro;
	public String pic;
	public String name;
	public String icon;
	public String identifyName;// 应用标识名
	public String downloadUrl;// 应用下载链接
	public String appName;// 应用名称
	public int parameter;
	// 节目数据图片方式：1=第一条数据有大横图，2=第一条数据有大竖图，3=都是普通图
	public int picType = 0;
	public int showPic = 0;

	private List<ColumnData> subColumn;
	private List<RecommendCommonData> datas;
	private ArrayList<HotLibType> vault;
	//3.1.2添加
	public long parentId;
	public int contentTypeId;
	public int programTypeId;
	public ArrayList<RecommendTag> recommendTags;

	public static final int TYPE_LIVE = 12;
	public static final int TYPE_USHOW_LIVE = 23;
	public static final int TYPE_ACTIVITY = 13;
	public static final int TYPE_APPLICATION = 14;
	public static final int TYPE_DIRECTLY_PLAY = 15;
	public static final int TYPE_ADVERT = 19;
	public static final int TYPE_SHAKE = 21;
	
	public static final int PIC_TYPE_VERTICAL = 1;
	public static final int PIC_TYPE_HORIZONTAL = 2;
	public static final int PIC_TYPE_NORMAL = 3;
	public static final int PIC_TYPE_THREE = 4;

	public List<RecommendCommonData> getDatas() {
		return datas;
	}

	public void setDatas(List<RecommendCommonData> datas) {
		this.datas = datas;
	}

	public static ColumnData current = new ColumnData();

	public void setSubColumn(List<ColumnData> subColumn) {
		this.subColumn = subColumn;
	}

	public List<ColumnData> getSubColumn() {
		return subColumn;
	}

	public ArrayList<HotLibType> getVault() {
		return vault;
	}

	public void setVault(ArrayList<HotLibType> vault) {
		this.vault = vault;
	}
	
}
