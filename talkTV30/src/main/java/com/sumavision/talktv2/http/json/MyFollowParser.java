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
 * @description 关注列表解析类
 * @changeLog
 */
public class MyFollowParser extends BaseJsonParser {
	public int guanzhuCont;
	public ArrayList<User> guanzhuList = new ArrayList<User>();

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
				UserNow.current().friendCount = content.getInt("guanzhuCount");
				guanzhuCont = UserNow.current().friendCount;
				JSONArray users = content.getJSONArray("guanzhu");
				for (int i = 0; i < users.length(); ++i) {
					JSONObject user = users.getJSONObject(i);
					User u = new User();
					u.userId = user.getInt("userId");
					u.name = user.getString("userName");
					u.iconURL = user.getString("userPic");
					u.isFans = user.getInt("isFensi");
					u.signature = user.getString("signature");
					u.isVip = user.optInt("isVip")==1 ? true:false;
					guanzhuList.add(u);
				}
				UserNow.current().friend = guanzhuList;
			} else
				msg = jsonObject.getString("msg");
		} catch (JSONException e) {
			msg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
		errMsg = msg;
	}

}
