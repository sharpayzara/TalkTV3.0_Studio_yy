package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author hpb
 * @version 3.0
 * @description 关注列表请求类
 * @changeLog
 */
public class MyFollowRequest extends BaseJsonRequest {
	int id;
	int first;
	int count;

	public MyFollowRequest(int id, int first, int count) {
		this.id = id;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.guanzhuList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("first", first);
			holder.put("count", count);
			holder.put("sessionId", UserNow.current().sessionID);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
