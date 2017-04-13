package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

public class MyBadgeParser extends BaseJsonParser {

	public ArrayList<BadgeData> badgeList = new ArrayList<BadgeData>();

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
				UserNow.current().badgeCount = content.getInt("badgeCount");
				UserNow.current().badgeRate = content.getString("rate");
				if (UserNow.current().badgeCount > 0) {
					JSONArray chases = content.getJSONArray("badge");
					for (int i = 0; i < chases.length(); ++i) {
						JSONObject chase = chases.getJSONObject(i);
						BadgeData u = new BadgeData();
						u.id = chase.getLong("id");
						u.badgeId = chase.getLong("badgeId");
						u.picPath = chase.getString("badgePic");
						u.name = chase.getString("badgeName");
						u.createTime = chase.getString("getTime");
						badgeList.add(u);
					}
				}
				// UserNow.current().setBadgesGained(badgeList);
			} else
				msg = jsonObject.getString("msg");
		} catch (JSONException e) {
			msg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
		errMsg = msg;

	}

}
