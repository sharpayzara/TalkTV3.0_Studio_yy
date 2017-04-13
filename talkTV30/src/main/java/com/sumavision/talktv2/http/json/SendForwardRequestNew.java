package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 转发评论请求类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SendForwardRequestNew extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.talkForwardAdd);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", "3.0.9");
			holder.put("forwardId", CommentData.current().forwardId);
			holder.put("rootId", CommentData.current().rootId);
			if (UserNow.current().userID != 0) {
				holder.put("userId", UserNow.current().userID);
				holder.put("jsession", UserNow.current().jsession);
			}
			holder.put("source",
					StringUtils.AllStrTOUnicode(CommentData.COMMENT_SOURCE));
			holder.put("content",
					StringUtils.AllStrTOUnicode(CommentData.current().content));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
