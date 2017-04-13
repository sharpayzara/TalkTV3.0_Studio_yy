package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

public class ReplyParser extends BaseJsonParser {

	public ArrayList<CommentData> commentList = new ArrayList<CommentData>();
	public int replyCount;

	@Override
	public void parse(JSONObject jsonObject) {
		commentList = new ArrayList<CommentData>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");
				if (content.has("newUserInfo")) {
					JSONObject info = content.optJSONObject("newUserInfo");
					if (info.has("point")) {
						UserNow.current().point = info.optInt("point");
						UserNow.current().point = info.optInt("totalPoint");
						UserNow.current().getExp = info.optInt("exp");
						UserNow.current().exp = info.optInt("totalExp");
						UserNow.current().lvlUp = info.optInt("changeLevel");
						UserNow.current().level = info.optString("level");
						UserNow.current().userID = info.optInt("userId");

					}
				}

				CommentData.current().replyCount = content.optInt("replyCount");
				if (content.has("reply")) {
					JSONArray reply = content.optJSONArray("reply");
					for (int i = 0; i < reply.length(); i++) {
						JSONObject c = reply.optJSONObject(i);
						CommentData comment = new CommentData();
						comment.talkId = c.optInt("id");
						comment.commentTime = c.optString("createTime");
						comment.content = c.optString("content");
						comment.contentURL = c.optString("photoUrl");
						if (!"".equals(comment.contentURL))
							comment.talkType = 1;
						comment.audioURL = c.optString("audioUrl");
						if (!"".equals(comment.audioURL))
							comment.talkType = 4;
						comment.source = c.optString("source");
						
						comment.userName = c.optString("username");
						comment.isAnonymousUser = c.optInt("isAnonymousUser");
						JSONObject user = c.optJSONObject("user");
						if(user != null){
							comment.userName = user.optString("name");
							comment.userURL = user.optString("pic");
							comment.userId = user.optInt("id");
							comment.isAnonymousUser = user
									.optInt("isAnonymousUser");
						}
						commentList.add(comment);
					}
				}
				CommentData.current().reply = (commentList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
