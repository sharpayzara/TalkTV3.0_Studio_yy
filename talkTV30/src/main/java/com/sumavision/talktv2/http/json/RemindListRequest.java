package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author hpb
 * @version 3.0
 * @description 用户预约列表请求
 * @changeLog
 */
public class RemindListRequest extends BaseJsonRequest {
	private int userId;
	private int first;
	private int count;

	public RemindListRequest(int userId, int first, int count) {
		super();
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.remindList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("first", first);
			holder.put("count", count);
			holder.put("sessionId", UserNow.current().sessionID);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
