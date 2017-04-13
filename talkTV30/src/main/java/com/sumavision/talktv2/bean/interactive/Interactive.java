package com.sumavision.talktv2.bean.interactive;

/**
 * 互动专区信息
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class Interactive {
	private static Interactive instance;

	private Interactive() {

	}

	public static Interactive getInstance() {
		if (instance == null) {
			instance = new Interactive();
		}
		return instance;
	}

	public int id;
	public String name;
	public int zoneId;// 资讯专区id

}
