package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * @author hpb
 * @version 2.2
 * @description 发私信请求类
 */
public class SendMailRequest extends BaseJsonRequest {

	private int userId;
	String content;
	String pic;

	public SendMailRequest(int userId, String content, String pic) {
		this.userId = userId;
		this.content = content;
		this.pic = pic;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.mailAdd);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("otherUserId", userId);
			holder.put("userId", UserNow.current().userID);
			holder.put("content", StringUtils.AllStrTOUnicode(content));
			holder.put("sessionId", UserNow.current().sessionID);
			holder.put("count", 20);
			holder.put("pic", pic);
			holder.put("jsession", UserNow.current().jsession);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
