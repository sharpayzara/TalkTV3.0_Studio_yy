package com.sumavision.talktv2.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class JiShuData implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String pic;
	public String url;
	public String videoPath;
	public String hVideoPath;
	public String superVideoPath;
	public int topicId;

	public ArrayList<NetPlayData> netPlayDatas = null;
	public CacheInfo cacheInfo = null;

	public int playCount;
	public String shortName;

}
