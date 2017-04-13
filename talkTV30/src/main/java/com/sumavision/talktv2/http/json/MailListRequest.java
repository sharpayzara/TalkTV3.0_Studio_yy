package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author 李梦思
 * @version v2.2
 * @description 私信列表请求类
 */
public class MailListRequest extends BaseJsonRequest {

	private int userId;
	private int first;
	private int count;

	public MailListRequest(int userId, int first, int count) {
		super();
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.mailDetail);
			holder.put("version", "3.0.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("first", first);
			holder.put("count", count);
			holder.put("userId", UserNow.current().userID);
			holder.put("otherUserId", userId);
			holder.put("jsession", UserNow.current().jsession);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
