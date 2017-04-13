package com.sumavision.talktv2.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 视频源平台
 * 
 * @author suma-hpb
 * 
 */
public class SourcePlatform implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String pic;
	public boolean isNative = true;
	public ArrayList<JiShuData> jishuList = new ArrayList<JiShuData>();
}
