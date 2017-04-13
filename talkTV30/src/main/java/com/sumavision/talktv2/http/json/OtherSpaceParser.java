package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author
 * @version 3.0
 * @description 其他用户中心解析类
 * @changeLog
 */
public class OtherSpaceParser extends BaseJsonParser {
	public User uo = new User();

	public void parse(JSONObject jAData) {

		try {
			if (jAData.has("code")) {
				errCode = jAData.getInt("code");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jAData.getJSONObject("content");
				uo.isVip = content.optInt("isVip")==1 ? true:false;
				uo.userId = content.optInt("userId");
				uo.name = content.getString("userName");
				uo.gender = content.getInt("sex");
				uo.level = content.getString("level");
				uo.iconURL = content.getString("pic");
				uo.signature = content.getString("signature");
				uo.friendCount = content.getInt("guanzhuCount");
				uo.fansCount = content.getInt("fensiCount");
				uo.talkCount = content.getInt("talkCount");
				uo.mailCount = content.getInt("mailCount");
				uo.chaseCount = content.getInt("chaseCount");
				uo.remindCount = content.getInt("remindCount");
				uo.isGuanzhu = content.getInt("isGuanzhu");
				uo.isFensi = content.getInt("isFensi");
				uo.eventCount = content.getInt("eventCount");
				if (uo.eventCount > 0) {
					ArrayList<EventData> list = new ArrayList<EventData>();
					JSONArray events = content.optJSONArray("event");
					if (events != null) {
						for (int i = 0; i < events.length(); i++) {
							EventData temp = new EventData();
							JSONObject event = events.getJSONObject(i);
							temp.id = event.getInt("id");
							temp.createTime = event.getString("createTime");
							temp.preMsg = event.getString("preMsg");
							temp.eventTypeName = event
									.optString("eventTypeName");

							JSONObject user = event.getJSONObject("user");
							temp.userId = user.getInt("id");
							temp.userName = user.getString("name");
							temp.userPicUrl = user.getString("photo");

							temp.toObjectType = event.getInt("toObjectType");
							if (temp.toObjectType > 0) {
								JSONObject toObject = event
										.getJSONObject("toObject");
								temp.toObjectId = toObject.getInt("id");
								temp.toObjectName = toObject.optString("name");
								temp.toObjectPicUrl = toObject
										.optString("photo");

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
					uo.event = list;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
