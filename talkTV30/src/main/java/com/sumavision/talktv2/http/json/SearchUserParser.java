package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 搜索用户解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchUserParser extends BaseJsonParser {

	public ArrayList<User> userList = new ArrayList<User>();
	public int count;

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
				count = jsonObject.optInt("count");
				if (jsonObject.has("content")) {
					JSONArray content = jsonObject.getJSONArray("content");
					for (int i = 0; i < content.length(); i++) {
						JSONObject user = content.getJSONObject(i);
						User u = new User();
						u.userId = user.getInt("id");
						u.name = user.getString("name");
						u.iconURL = user.getString("pic");
						u.signature = user.getString("signature");
						u.isFriend = user.getInt("isGuanzhu");
						u.level = user.getString("level");
						u.isVip = user.optInt("isVip")==1 ? true:false;
						userList.add(u);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
