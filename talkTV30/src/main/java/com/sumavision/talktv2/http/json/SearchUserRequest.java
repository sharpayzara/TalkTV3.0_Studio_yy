package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 搜索用户请求类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchUserRequest extends BaseJsonRequest {

	private int first;
	private int count;
	private String name;

	public SearchUserRequest(int first, int count, String name) {
		super();
		this.first = first;
		this.count = count;
		this.name = name;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.searchUser);
			jsonObject.put("version", JSONMessageType.APP_VERSION);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("first", first);
			jsonObject.put("count", count);
			if (UserNow.current().userID != 0) {
				jsonObject.put("userId", UserNow.current().userID);
			}
			jsonObject.put("userName", StringUtils.AllStrTOUnicode(name));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
