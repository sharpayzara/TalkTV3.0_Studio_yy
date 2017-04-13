package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * @description
 */
public class SendCommentRequestNew extends BaseJsonRequest {

	private int topicId;

	public SendCommentRequestNew(int topicId) {
		this.topicId = topicId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.talkAdd);
			holder.put("version", "3.0.9");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("topicId", topicId);
			if (UserNow.current().userID != 0) {
				holder.put("userId", UserNow.current().userID);
			}
			holder.put("jsession", UserNow.current().jsession);
			holder.put("source",
					StringUtils.AllStrTOUnicode(CommentData.COMMENT_SOURCE));
			if (!CommentData.current().content.equals(""))
				holder.put("content", StringUtils.AllStrTOUnicode(CommentData
						.current().content));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
