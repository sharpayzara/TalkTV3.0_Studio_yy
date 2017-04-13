package com.sumavision.talktv2.actionprovider.bean;

public class CustomChooser {

	private String id;
	private int ic_resource;// 图片资源
	private String title;// 标题
	private String other;// 其他，提示什么的

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIc_resource() {
		return ic_resource;
	}

	public void setIc_resource(int ic_resource) {
		this.ic_resource = ic_resource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

}
