package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 发表回复请求类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendReplyRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.replyAdd);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("source",
					StringUtils.AllStrTOUnicode(CommentData.COMMENT_SOURCE));
			holder.put("talkId", CommentData.current().talkId);
			// replyId long 回复id 可选（回复回复时，此字段要传）
			// int replyId = 0;
			// if (replyId != 0) {
			// holder.put("replyId", CommentData.current().talkId);
			// }
			if (UserNow.current().userID != 0) {
				// userId long 登陆用户id(匿名用户不传该参数) 可选
				holder.put("userId", UserNow.current().userID);
				holder.put("sessionId", UserNow.current().sessionID);
			}
			// replyUserId long 被回复的用户id 必选
			if (CommentData.current().isReply)
				holder.put("replyUserId", CommentData.current().userId);
			else
				holder.put("replyUserId", CommentData.current().userId);

			if (CommentData.current().content != null
					&& !CommentData.current().content.equals(""))
				holder.put("content", StringUtils.AllStrTOUnicode(CommentData
						.current().content));
			if (CommentData.current().pic != null
					&& !CommentData.current().pic.equals(""))
				holder.put("pic", CommentData.current().pic);
			if (CommentData.current().audio != null
					&& !"".equals(CommentData.current().audio))
				holder.put("audio", CommentData.current().audio);
			// getContent int 是否要返回content对象：
			// 1=要返回，0=不要返回（快速回复）。 默认为1 可选
			// holder.put("getContent", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
