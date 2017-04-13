package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author 李梦思
 * @version v2.2
 * @description 推荐好友列表解析类
 * @changeLog
 */
public class RecommendUserListParser extends BaseJsonParser {

	public ArrayList<User> userList = new ArrayList<User>();

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				JSONArray users = content.getJSONArray("recommend");
				for (int i = 0; i < users.length(); ++i) {
					JSONObject user = users.getJSONObject(i);
					User u = new User();
					u.userId = user.getInt("userId");
					u.name = user.getString("userName");
					u.iconURL = user.getString("userPic");
					u.signature = user.getString("signature");
					userList.add(u);
				}
				UserNow.current().friend = userList;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
