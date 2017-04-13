package com.sumavision.talktv2.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramVideoData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	// 1正序，2倒叙
	public int subOrderType;
	public String name;
	public int jishuCount;
	public ArrayList<JiShuData> jishus;
}
