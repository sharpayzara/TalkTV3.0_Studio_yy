package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author hpb
 * @version 3.0
 * @description 用户私信列表解析类
 */
public class MyPrivateMsgParser extends BaseJsonParser {
	public int mailCount;
	public ArrayList<MailData> mailList = new ArrayList<MailData>();

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
				JSONArray mailUsers = content.getJSONArray("mailUser");
				UserNow.current().mailCount = content.getInt("mailUserCount");
				for (int i = 0; i < mailUsers.length(); ++i) {
					JSONObject mailUser = mailUsers.getJSONObject(i);
					MailData m = new MailData();
					m.id = mailUser.getLong("id");
					m.content = mailUser.getString("content");
					m.timeStemp = mailUser.getString("createTime");
					m.sid = mailUser.getInt("otherUserId");
					m.sUserName = mailUser.getString("otherUserName");
					m.sUserPhoto = mailUser.getString("otherUserPic");
					m.isVip = mailUser.optInt("isVip")==1?true:false;
					mailList.add(m);
				}
				UserNow.current().mail = mailList;
			} else
				msg = jsonObject.getString("msg");
		} catch (JSONException e) {
			msg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
		errMsg = msg;
	}

}
