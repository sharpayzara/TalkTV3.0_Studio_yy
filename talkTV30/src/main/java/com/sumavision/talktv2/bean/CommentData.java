package com.sumavision.talktv2.bean;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * @createTime 2012-5-31
 * @description 评论实体类
 * @changeLog
 */
public class CommentData {

	private static CommentData current;
	public VodProgramData program;
	public String userName;
	public static final String COMMENT_SOURCE = "来自"
			+ (Build.MODEL == null ? "电视粉" : Build.MODEL) + "客户端";
	public String userURL;
	public int userId;
	// 评论对象的名字：电影名，电视剧名，演员名，私信接受者名字
	public String objectName = "";
	// 评论对象Id
	public long objectId = 0;
	// 评论内容
	public String content = "";
	// 评论时间
	public String commentTime;
	public int replyCount;
	public int forwardCount;
	// 评论内容图片：测试时用URL
	public String contentURL = "";
	// 此条评论来源：网页版，Android手机版
	public String source;
	// 评论对象(节目、演员)的ID
	public long topicID;
	// 转发评论的根评论
	public CommentData rootTalk;
	public CommentData replyTalk;
	public boolean hasRootTalk = false;
	// 回复所属于的评论talk的id
	public int talkId;
	// 被回复的用户的ID
	public int repUserId;
	// 转发时，上一级talk的 ID
	public int forwardId;
	// 0：非转发评论，1：为转发评论
	public int actionType;
	// 标记转发时的根评论是否被删除
	public boolean isDeleted = false;
	// 节目ID
	public long programId;
	// 图片base64编码
	public String pic = "";
	public Bitmap picBitMap = null;
	public Drawable picDrawable = null;
	public String picAllName = "";
	public String picAllNameLogo = "";
	public boolean isFromSelf = true;
	public int privateMessageCount;
	// talkType：谈论类型，0=原创，1=图片，2=视频，3=台词，4=语音
	public int talkType;
	// 是否是回复
	public boolean isReply = false;
	// 评论中图片
	public byte[] picLogo = null;
	// 语音base64编码
	public String audio;
	// 语音URL
	public String audioURL = "";
	// 语音文件的名字
	public String audioFileName;
	// 评论的回复
	public List<CommentData> reply = new ArrayList<CommentData>();
	// 评论的转发
	public List<CommentData> forward = new ArrayList<CommentData>();;
	// 粉播图片URL
	public String fenPlayPicURL = "";
	// 评论类型话题类型：0=自由创建的话题；1=节目话题；2=演员话题; 3=微影视; 4=粉播',5=投票，
	// 6=竞猜，7=摇奖，8=PK，9=专题模版，10=专题视频
	public int type = 1;
	// 是否为匿名用户发布 0不是1是
	public int isAnonymousUser = 0;
	// 如果是回复，这是回复Id
	public int replyId;
	public int rootId;
	public String time;
	public boolean isVip;

	public static CommentData current() {
		if (current == null) {
			current = new CommentData();
		}
		return current;
	}

}
