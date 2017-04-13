package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 通用解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ResultParser extends BaseJsonParser {

	public ArrayList<BadgeData> badgeList = new ArrayList<BadgeData>();
	public String info;
	
	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code", 1);
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode", 1);
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode", 1);
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}

			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.getJSONObject("content");
					info = content.optString("content");
					JSONArray newBadge = content.optJSONArray("newBadge");
					if (newBadge != null) {
						for (int i = 0; i < newBadge.length(); i++) {
							JSONObject badge = newBadge.getJSONObject(i);
							BadgeData b = new BadgeData();
							b.picPath = badge.getString("pic");
							b.name = badge.getString("name");
							badgeList.add(b);
						}
					}
					UserNow.current().newBadge = badgeList;
					JSONObject userNowInfo = content
							.optJSONObject("newUserInfo");
					setPointInfo(userNowInfo);
				}
			} else {
				errMsg = jsonObject.optString("msg", "");
			}
		} catch (JSONException e) {
			errMsg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
	}

}
