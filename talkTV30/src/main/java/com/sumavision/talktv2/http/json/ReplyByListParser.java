package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author 李梦思
 * @version 2.2
 * @createTime 2013-1-17
 * @description 被回复解析类
 * @changeLog
 */
public class ReplyByListParser extends BaseJsonParser {
	public ArrayList<CommentData> replyList = new ArrayList<CommentData>();
	public int replyCount;

	@Override
	public void parse(JSONObject jAData) {

		try {
			if (jAData.has("code")) {
				errCode = jAData.getInt("code");
			} else if (jAData.has("errcode")) {
				errCode = jAData.getInt("errcode");
			} else if (jAData.has("errorCode")) {
				errCode = jAData.getInt("errorCode");
			}
			if (jAData.has("jsession")) {
				UserNow.current().jsession = jAData.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jAData.getJSONObject("content");
				if (content.has("newUserInfo")) {
					JSONObject info = content.getJSONObject("newUserInfo");
					setPointInfo(info);
				}

				replyCount = content.getInt("replyCount");
				UserNow.current().replyMeCount = replyCount;

				if (UserNow.current().replyMeCount > 0) {
					JSONArray reply = content.getJSONArray("reply");
					for (int i = 0; i < reply.length(); i++) {
						JSONObject c = reply.getJSONObject(i);
						CommentData comment = new CommentData();
						comment.isReply = true;
						comment.talkId = c.getInt("talkId");
						comment.replyId = c.getInt("id");
						comment.content = c.getString("content");
						comment.commentTime = c.getString("createTime");
						comment.pic = c.getString("photoUrl");
						if (!comment.pic.equals(""))
							comment.talkType = 1;
						comment.audioURL = c.getString("audioUrl");
						if (!comment.audioURL.equals(""))
							comment.talkType = 4;
						comment.source = c.getString("source");
						JSONObject user = c.getJSONObject("user");
						comment.userName = user.getString("name");
						comment.userURL = user.getString("pic");
						comment.userId = user.getInt("id");
						comment.isAnonymousUser = user
								.getInt("isAnonymousUser");
						comment.isVip = user.optInt("isVip") == 1 ? true:false;
						// content.reply[].byType int 1=回复我的评论，2=回复我的回复
						// content.reply[].byObject object 被回复对象
						// content.reply[].byObject.content string byType
						// =1时，我的评论文本内容，byType =2时，我的回复文本内容
						// content.reply[].byObject.audioUrl string byType
						// =1时，我的评论声音绝对路径，byType =2时，我的回复声音绝对路径
						// content.reply[].byObject.photoUrl string byType
						// =1时，我的评论图片绝对路径，byType =2时，我的回复图片绝对路径
						CommentData replyTalk = new CommentData();
						JSONObject r = c.getJSONObject("byObject");
						replyTalk.content = r.getString("content");
						replyTalk.pic = r.getString("photoUrl");
						if (!replyTalk.pic.equals(""))
							replyTalk.talkType = 1;
						replyTalk.audioURL = r.getString("audioUrl");
						if (!replyTalk.audioURL.equals(""))
							replyTalk.talkType = 4;
						if (c.getInt("byType") == 1) {
							replyTalk.isReply = false;
						} else {
							replyTalk.isReply = true;
						}
						comment.replyTalk = replyTalk;
						replyList.add(comment);
					}
				}
				UserNow.current().replyList=replyList;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
