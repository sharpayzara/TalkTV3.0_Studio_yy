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
 * @author 李梦思
 * @version 2.2
 * @description 被@解析类
 * @changeLog
 */
public class TalkListParser extends BaseJsonParser {
	public ArrayList<CommentData> commentData;
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
				JSONArray comment = content.optJSONArray("talk");
				talkCount = content.optInt("talkCount");
				commentData = new ArrayList<CommentData>();
				if (comment != null && comment.length() > 0) {
					for (int i = 0; i < comment.length(); i++) {
						CommentData c = new CommentData();
						JSONObject obj = comment.optJSONObject(i);
						c.talkId = (int) obj.optLong("id");
						c.content = obj.optString("content");
						c.actionType = obj.optInt("actionType");
						c.time = obj.optString("displayTime");
						c.pic = obj.optString("userPic");
						c.userName = obj.optString("userName");
						c.replyCount = obj.optInt("replyCount");
						c.rootId = (int) obj.optLong("rootId");
						c.userId = (int) obj.optLong("userId");
						c.isVip = obj.optInt("isVip") == 1 ? true:false;
						
						JSONArray forward = obj.optJSONArray("forward");
						List<CommentData> forwardData = new ArrayList<CommentData>();
						if (forward != null && forward.length() > 0) {
							for (int j = 0; j < forward.length(); j++) {
								CommentData f = new CommentData();
								JSONObject fobj = forward.optJSONObject(j);
								f.forwardId = fobj.optInt("Id");
								f.userName = fobj.optString("userNname");
								f.content = fobj.optString("content");
								forwardData.add(f);
							}
							c.forward = new ArrayList<CommentData>();
							c.forward = forwardData;
						}
						JSONArray reply = obj.optJSONArray("reply");
						List<CommentData> replyData = new ArrayList<CommentData>();
						if (reply != null && reply.length() > 0) {
							for (int k = 0; k < reply.length(); k++) {
								CommentData r = new CommentData();
								JSONObject robj = reply.optJSONObject(k);
								r.userName = robj.optString("username");
								r.content = robj.optString("content");
								replyData.add(r);
							}
							c.reply = new ArrayList<CommentData>();
							c.reply = replyData;
						}
						
						commentData.add(c);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
