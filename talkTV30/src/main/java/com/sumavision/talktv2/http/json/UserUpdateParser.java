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
 * @author hpb
 * @version v2.2
 * @description 用户资料更新解析类
 * @changeLog
 */
public class UserUpdateParser extends BaseJsonParser {

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
			if (jsonObject.has("sessionId")) {
				UserNow.current().sessionID = jsonObject.getString("sessionId");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				user = jsonObject.getJSONObject("content");
				JSONObject infoObj = user.optJSONObject("newUserInfo");
				setPointInfo(infoObj);
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
				UserNow.current().userID = user.getInt("userId");
				UserNow.current().gender = user.getInt("sex");
				if (!"".equals(user.getString("signature"))) {
					UserNow.current().signature = user.getString("signature");
				} else {
					UserNow.current().signature = "这个家伙神马也木有留下";
				}
				UserNow.current().iconURL = user.getString("pic");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
