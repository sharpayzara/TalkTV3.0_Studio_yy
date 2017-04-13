package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author hpb
 * @version 3.0
 * @description 粉丝列表解析类
 * @changeLog
 */
public class MyFansParser extends BaseJsonParser {

	public int fensiCount;
	public ArrayList<User> userList = new ArrayList<User>();

	@Override
	public void parse(JSONObject jsonObject) {
		String msg = "";

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
				UserNow.current().fansCount = content.getInt("fensiCount");
				fensiCount = UserNow.current().fansCount;
				JSONArray users = content.optJSONArray("fensi");
				for (int i = 0; i < users.length(); ++i) {
					JSONObject user = users.optJSONObject(i);
					User u = new User();
					u.userId = user.optInt("userId");
					u.name = user.optString("userName");
					u.iconURL = user.optString("userPic");
					u.isFriend = user.optInt("isGuanzhu");
					u.signature = user.optString("signature");
					u.isVip = user.optInt("isVip")==1 ? true:false;
					userList.add(u);
				}
			} else
				msg = jsonObject.optString("msg");
		} catch (JSONException e) {
			msg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
		errMsg = msg;
	}

}
