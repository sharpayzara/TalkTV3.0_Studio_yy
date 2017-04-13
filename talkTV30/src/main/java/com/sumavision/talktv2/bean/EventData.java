package com.sumavision.talktv2.bean;

/**
 * @author jianghao
 * @version 2.2
 * @createTime 2012-1-14
 * 
 */
public class EventData {
	// 用户id
	public int userId;
	// 用户名字
	public String userName;
	// 用户头像地址
	public String userPicUrl;
	// 事件id
	public int id;

	public String createTime;
	// 预设定内容 用来直接放在list列表
	public String preMsg;
	public String msgTemplate;
	// 事件类型名称
	public String eventTypeName;
	// 被动对象类型
	public int toObjectType;
	// 被动事件ID
	public int toObjectId;

	// 被动事件 名字
	public String toObjectName;

	// 被动对象图片url
	public String toObjectPicUrl;

	// 对象类型 （对象和被动对象的关系 如果该事件是评论事件， 那么节目是别动对象，评论是对象）
	public int objectType;
	// 对象ID
	public int objectId;
	// 对象名字
	public String objectName;
	// 对象图片
	public String objectPicUrl;
}
