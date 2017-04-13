package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author 李梦思
 * @version 2.2
 * @createTime 2013-1-13
 * @description 被回复列表请求类
 * @changLog
 */
public class ReplyByListRequest extends BaseJsonRequest {
	private int userId;
	private int first;
	private int count;

	public ReplyByListRequest(int userId, int first, int count) {
		super();
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.replyByList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", userId);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
