package com.sumavision.talktv2.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 发表回复解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendReplyParser extends BaseJsonParser {

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
				if (content.has("newUserInfo")) {
					JSONObject info = content.getJSONObject("newUserInfo");
					setPointInfo(info);
				}

				List<CommentData> lc = new ArrayList<CommentData>();
				CommentData.current().replyCount = content.getInt("replyCount");
				if (content.has("reply")) {
					JSONArray reply = content.getJSONArray("reply");
					for (int i = 0; i < reply.length(); i++) {
						JSONObject c = reply.getJSONObject(i);
						CommentData comment = new CommentData();
						comment.talkId = c.getInt("id");
						comment.commentTime = c.getString("createTime");
						comment.content = c.optString("content");
						comment.contentURL = c.optString("photoUrl");
						if (!"".equals(comment.contentURL))
							comment.talkType = 1;
						comment.audioURL = c.optString("audioUrl");
						if (!"".equals(comment.audioURL))
							comment.talkType = 4;
						comment.source = c.getString("source");
						JSONObject user = c.getJSONObject("user");
						comment.userName = user.getString("name");
						comment.userURL = user.getString("pic");
						comment.userId = user.getInt("id");
						comment.isAnonymousUser = user
								.getInt("isAnonymousUser");
						lc.add(comment);
					}
				}
				CommentData.current().reply = (lc);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
