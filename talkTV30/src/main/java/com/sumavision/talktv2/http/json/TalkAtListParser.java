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
 * @description 被@解析类
 * @changeLog
 */
public class TalkAtListParser extends BaseJsonParser {
	public ArrayList<CommentData> userTalkList = new ArrayList<CommentData>();
	public int talkCount;

	@Override
	public void parse(JSONObject jsonObject) {

		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				setPointInfo(content.getJSONObject("newUserInfo"));
				talkCount = content.getInt("talkCount");
				UserNow.current().atMeCount = talkCount;
				if (UserNow.current().atMeCount > 0) {
					JSONArray talks = content.getJSONArray("talk");
					for (int i = 0; i < talks.length(); i++) {
						JSONObject item = talks.getJSONObject(i);

						CommentData c = new CommentData();

						c.talkId = item.getInt("id");
						c.actionType = item.getInt("actionType");
						c.commentTime = item.getString("displayTime");

						// talkType：谈论类型，0=原创文字，1=图片，2=视频，3=台词，4=语音
						if (item.getInt("talkType") == 1) {
							c.contentURL =
							// JSONMessageType.URL_TITLE_SERVER +
							item.getString("photoUrl");
							c.content = item.getString("content");
						} else if (item.getInt("talkType") == 4) {
							c.audioURL =
							// JSONMessageType.URL_TITLE_SERVER +
							item.getString("audioUrl");
						} else if (item.getInt("talkType") == 2) {
							c.audioURL = "";
							c.content = item.getString("content");
						} else if (item.getInt("talkType") == 3) {
							c.audioURL = "";
							c.content = item.getString("content");
						} else {
							c.audioURL = "";
							c.content = item.getString("content");
						}

						c.replyCount = item.getInt("replyCount");
						c.source = item.getString("source");
						c.talkType = item.getInt("talkType");
						if (item.getInt("actionType") == 1) {
							if (!item.isNull("rootTalk")
									&& item.has("rootTalk")) {
								CommentData rootC = new CommentData();
								JSONObject rootTalk = item
										.getJSONObject("rootTalk");
								if (rootTalk.has("id")) {
									if (rootTalk.getInt("id") != 0) {
										rootC.commentTime = rootTalk
												.getString("displayTime");
										rootC.forwardCount = rootTalk
												.getInt("forwardCount");
										rootC.userName = rootTalk
												.getJSONObject("user")
												.getString("name");
										rootC.replyCount = rootTalk
												.getInt("replyCount");
										rootC.source = rootTalk
												.getString("source");
										// rootC.objectName = rootTalk
										// .getString("topicName");
										rootC.topicID = rootTalk
												.getLong("rootId");
										rootC.talkId = rootTalk.getInt("id");
										rootC.userURL =
										// JSONMessageType.URL_TITLE_SERVER+
										rootTalk.getJSONObject("user")
												.getString("pic");
										rootC.isAnonymousUser = rootTalk
												.getJSONObject("user").getInt(
														"isAnonymousUser");
										rootC.talkType = rootTalk
												.getInt("talkType");
										if (rootC.talkType == 1) {

											rootC.contentURL =
											// JSONMessageType.URL_TITLE_SERVER+
											rootTalk.getString("photoUrl");
											rootC.content = rootTalk
													.getString("content");
										} else if (rootC.talkType == 4) {

											rootC.audioURL =
											// JSONMessageType.URL_TITLE_SERVER
											// +
											rootTalk.getString("audioUrl");
										} else {
											rootC.content = rootTalk
													.getString("content");
										}
									}
								} else {
									c.isDeleted = true;
									rootC.commentTime = "";
									rootC.forwardCount = 0;
									rootC.userName = "";
									rootC.replyCount = 0;
									rootC.source = "";
									rootC.objectName = "";
									rootC.topicID = 0;
									rootC.userURL = "";
									rootC.content = "此条评论已被原作者删除";
								}
								c.rootTalk = rootC;
								c.hasRootTalk = true;
							}
							c.forwardCount = item.getInt("forwardCount");
							c.forwardId = item.getInt("forwardId");
						} else {
							c.hasRootTalk = false;
						}

						c.userName = item.getJSONObject("user").getString(
								"name");
						c.userURL = item.getJSONObject("user").getString("pic");
						c.userId = item.getJSONObject("user").getInt("id");
						c.isAnonymousUser = item.getJSONObject("user").getInt(
								"isAnonymousUser");
						userTalkList.add(c);
					}
				}
				UserNow.current().talkAtList = userTalkList;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
