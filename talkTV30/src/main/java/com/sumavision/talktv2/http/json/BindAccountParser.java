package com.sumavision.talktv2.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 账号绑定数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindAccountParser extends BaseJsonParser {

	public UserNow userInfo = new UserNow();

	@Override
	public void parse(JSONObject jsonObject) {
		JSONObject user = null;
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
			errMsg = jsonObject.optString("msg");
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				user = jsonObject.getJSONObject("content");
				setUserInfo(user);
				JSONObject userNowInfo = user.optJSONObject("newUserInfo");
				setPointInfo(userNowInfo);
				JSONArray newBadge = user.optJSONArray("newBadge");
				if (newBadge != null) {
					List<BadgeData> lb = new ArrayList<BadgeData>();
					for (int i = 0; i < newBadge.length(); i++) {
						JSONObject badge = newBadge.getJSONObject(i);
						BadgeData b = new BadgeData();
						b.picPath = badge.getString("pic");
						b.name = badge.getString("name");
						lb.add(b);
					}
					UserNow.current().newBadge = lb;
				}
				UserNow.current().isSelf = true;
				UserNow.current().isLogedIn = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	

}
