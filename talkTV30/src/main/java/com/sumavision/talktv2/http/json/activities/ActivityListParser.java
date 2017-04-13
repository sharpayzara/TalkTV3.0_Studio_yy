package com.sumavision.talktv2.http.json.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PlayNewData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * @author suma-hpb
 * @version 3.0
 * @description 活动列表解析类
 * @changeLog
 */
public class ActivityListParser extends BaseJsonParser {
	public ArrayList<PlayNewData> activityList;

	public void parse(JSONObject object) {
		activityList = new ArrayList<PlayNewData>();
		String msg = "";
		try {
			if (object.has("code")) {
				errCode = object.optInt("code");
			}
			if (object.has("jsession")) {
				UserNow.current().jsession = object.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = object.getJSONObject("content");
				JSONArray activities = content.getJSONArray("activity");
				for (int i = 0; i < activities.length(); ++i) {
					PlayNewData a = new PlayNewData();
					JSONObject activity = activities.getJSONObject(i);
					a.id = activity.optInt("id");
					a.name = activity.optString("name");
					a.typeId = activity.optInt("typeId");
					a.typeName = activity.optString("typeName");
					a.intro = activity.optString("shortIntro");
					a.pic = activity.optString("pic");
					a.state = activity.optInt("status");
					a.joinStatus = activity.optInt("joinStatus");
					activityList.add(a);
				}
			} else {
				msg = object.optString("msg");
			}
		} catch (JSONException e) {
			msg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
		errMsg = msg;
	}
}
