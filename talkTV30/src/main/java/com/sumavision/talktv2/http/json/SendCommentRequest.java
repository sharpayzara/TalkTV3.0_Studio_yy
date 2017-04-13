package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 发表评论请求类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendCommentRequest extends BaseJsonRequest {

	private String topicId;

	public SendCommentRequest(String topicId) {
		this.topicId = topicId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.talkAdd);
			holder.put("version", JSONMessageType.APP_VERSION_309);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("topicId", topicId);
			if (UserNow.current().userID != 0) {
				holder.put("userId", UserNow.current().userID);
				holder.put("sessionId", UserNow.current().sessionID);
			}
			// else {
			// holder.put("macAddress", UserNow.current().mac);
			// }
			holder.put("jsession", UserNow.current().jsession);
			holder.put("source",
					StringUtils.AllStrTOUnicode(CommentData.COMMENT_SOURCE));
			if (!CommentData.current().content.equals(""))
				holder.put("content", StringUtils.AllStrTOUnicode(CommentData
						.current().content));
			holder.put("first", 0);
			holder.put("count", 20);
			if (!CommentData.current().pic.equals(""))
				holder.put("pic", CommentData.current().pic);
			if (CommentData.current().audio != null)
				holder.put("audio", CommentData.current().audio);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
