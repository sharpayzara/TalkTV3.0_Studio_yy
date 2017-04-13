package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author 李梦思
 * @version 2.2
 * @description 用户发表的评论请求组装类
 * @changLog
 */
public class UserTalkListRequest extends BaseJsonRequest {

	private int userId;
	private int first;
	private int count;

	public UserTalkListRequest(int userId, int first, int count) {
		super();
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.userTalkList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("first", first);
			holder.put("count", count);
			holder.put("userId", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
