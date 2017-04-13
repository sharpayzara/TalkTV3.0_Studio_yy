package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

public class EventRoomParser extends BaseJsonParser {
	public ArrayList<EventData> evenList;
	public int eventCount;

	@Override
	public void parse(JSONObject jsonObject) {
		evenList = new ArrayList<EventData>();
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
				eventCount = content.optInt("eventCount", 0);
				JSONArray events = content.optJSONArray("event");
				if (events != null) {
					for (int i = 0; i < events.length(); i++) {
						EventData temp = new EventData();
						JSONObject event = events.getJSONObject(i);
						temp.id = event.getInt("id");
						temp.createTime = event.getString("createTime");
						temp.preMsg = event.getString("preMsg");
						temp.eventTypeName = event.optString("eventTypeName");

						JSONObject user = event.getJSONObject("user");
						temp.userId = user.getInt("id");
						temp.userName = user.getString("name");
						temp.userPicUrl = user.getString("photo");

						temp.toObjectType = event.getInt("toObjectType");
						if (temp.toObjectType != 0) {
							JSONObject toObject = event
									.getJSONObject("toObject");
							temp.toObjectId = toObject.getInt("id");
							temp.toObjectName = toObject.optString("name");
							temp.toObjectPicUrl = toObject.optString("photo");
						}
						evenList.add(temp);
					}
				}
			}
		} catch (JSONException e) {
		}

	}

}
