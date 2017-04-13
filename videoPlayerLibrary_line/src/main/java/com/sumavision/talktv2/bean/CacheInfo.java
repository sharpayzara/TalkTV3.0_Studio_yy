package com.sumavision.talktv2.bean;

import java.io.Serializable;

public class CacheInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 0 等待 //1正在下载 2已下完 3暂停 5 未添加 6 想要添加的
	public int state = 0;
	public boolean isSelected = false;

}
