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
public class SendReplyRequestNew extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.replyAdd);
			holder.put("version", "3.0.9");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("source",
					StringUtils.AllStrTOUnicode(CommentData.COMMENT_SOURCE));
			holder.put("talkId", CommentData.current().talkId);
			if (UserNow.current().userID != 0) {
				// userId long 登陆用户id(匿名用户不传该参数) 可选
				holder.put("userId", UserNow.current().userID);
			}
			holder.put("replyUserId", CommentData.current().userId);

			if (CommentData.current().content != null
					&& !CommentData.current().content.equals(""))
				holder.put("content", StringUtils.AllStrTOUnicode(CommentData
						.current().content));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
