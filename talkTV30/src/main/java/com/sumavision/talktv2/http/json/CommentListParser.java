package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 评论列表响应解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentListParser extends BaseJsonParser {

	public ArrayList<CommentData> commentList = new ArrayList<CommentData>();
	public int commentCount;

	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
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
				JSONObject jsonContent = jsonObject.getJSONObject("content");
				JSONArray content = jsonContent.getJSONArray("talk");
				commentCount = jsonContent.optInt("talkCount", 0);
				for (int i = 0; i < content.length(); i++) {
					JSONObject item = content.getJSONObject(i);
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
						if (!item.isNull("rootTalk") && item.has("rootTalk")) {
							CommentData rootC = new CommentData();
							JSONObject rootTalk = item
									.getJSONObject("rootTalk");
							if (rootTalk.has("id")) {
								if (rootTalk.getInt("id") != 0) {
									rootC.commentTime = rootTalk
											.getString("displayTime");
									rootC.forwardCount = rootTalk
											.getInt("forwardCount");
									rootC.userName = rootTalk.getJSONObject(
											"user").getString("name");
									rootC.replyCount = rootTalk
											.getInt("replyCount");
									rootC.source = rootTalk.getString("source");
									// rootC.objectName = rootTalk
									// .getString("topicName");
									rootC.topicID = rootTalk.getLong("rootId");
									rootC.talkId = rootTalk.getInt("id");
									rootC.userURL =
									// JSONMessageType.URL_TITLE_SERVER+
									rootTalk.getJSONObject("user").getString(
											"pic");
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

					c.userName = item.getJSONObject("user").getString("name");
					c.userURL =
					// JSONMessageType.URL_TITLE_SERVER +
					item.getJSONObject("user").getString("pic");
					c.userId = item.getJSONObject("user").getInt("id");
					c.isAnonymousUser = item.getJSONObject("user").getInt(
							"isAnonymousUser");
					commentList.add(c);
				}

			}
		} catch (JSONException e) {
		}

	}

}
