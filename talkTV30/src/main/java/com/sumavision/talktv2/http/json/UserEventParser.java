package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

public class UserEventParser extends BaseJsonParser {

	public int eventCount;
	public ArrayList<EventData> list = new ArrayList<EventData>();

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			JSONObject content = jsonObject.optJSONObject("content");
			eventCount = content.optInt("eventCount");
			JSONArray events = content.optJSONArray("event");
			if (events != null) {
				for (int i = 0; i < events.length(); i++) {
					EventData temp = new EventData();
					JSONObject event = events.optJSONObject(i);
					temp.id = event.optInt("id");
					temp.createTime = event.optString("createTime");
					temp.preMsg = event.optString("preMsg");
					temp.eventTypeName = event.optString("eventTypeName");

					temp.toObjectType = event.optInt("toObjectType");
					if (temp.toObjectType > 0) {
						JSONObject toObject = event.optJSONObject("toObject");
						temp.toObjectId = toObject.optInt("id");
						temp.toObjectName = toObject.optString("name");
						temp.toObjectPicUrl = toObject.optString("photo");

						if (!temp.toObjectPicUrl.contains("http://")) {
							temp.toObjectPicUrl = Constants.picUrlFor
									+ temp.toObjectPicUrl;
						}

						if (!temp.toObjectPicUrl.contains(".jpg")) {
							temp.toObjectPicUrl += ".jpg";
						}
					}
					list.add(temp);
				}
			}

		}

	}

}
