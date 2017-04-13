package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.BadgeDetailData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author suma-hpb
 * @version 3.0
 * @description 徽章详情解析
 * @changeLog
 */
public class BadgeDetailParser extends BaseJsonParser {

	public BadgeDetailData badgeDetailData;

	public void parse(JSONObject jsonObjct) {
		badgeDetailData = new BadgeDetailData();
		try {
			if (jsonObjct.has("code")) {
				errCode = jsonObjct.getInt("code");
			} else if (jsonObjct.has("errcode")) {
				errCode = jsonObjct.getInt("errcode");
			} else if (jsonObjct.has("errorCode")) {
				errCode = jsonObjct.getInt("errorCode");
			}
			if (jsonObjct.has("jsession")) {
				UserNow.current().jsession = jsonObjct.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObjct.getJSONObject("content");
				badgeDetailData.id = content.optInt("id");
				badgeDetailData.name = content.optString("name");
				badgeDetailData.pic = content.optString("pic");
				badgeDetailData.intro = content.optString("intro");
				badgeDetailData.getCount = content.optInt("getCount");
				badgeDetailData.getTime = content.optInt("getTime");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
