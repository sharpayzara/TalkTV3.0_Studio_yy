package com.sumavision.talktv2.bean;

import java.util.ArrayList;

/**
 * @createTime
 * @description 明星实体类
 * @changeLog
 */
public class StarData {

	public String name;
	public String nameEng;
	public String intro;
	public int stagerID;
	public String photoBig_V;
	public String hobby;
	// 星座
	public String starType;
	public int picCount = 0;
	public String[] photo;
	public int programCount = 0;
	public ArrayList<VodProgramData> program;

}
