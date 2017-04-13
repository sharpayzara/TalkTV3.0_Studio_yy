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
 * @version 2.2
 * @description 私信列表解析类
 */
public class SendMailParser extends BaseJsonParser {
	public int mailCount;
	public ArrayList<MailData> mailList = new ArrayList<MailData>();

	@Override
	public void parse(JSONObject jsonObject) {

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
				mailCount = content.getInt("mailCount");
				JSONArray mails = content.optJSONArray("mail");
				if (mails != null) {
					for (int i = 0; i < mails.length(); ++i) {
						JSONObject mail = mails.getJSONObject(i);
						MailData m = new MailData();
						m.id = mail.getLong("id");
						m.content = mail.getString("content");
						m.pic = mail.getString("pic");
						m.timeStemp = mail.getString("createTime");
						m.sid = mail.getInt("sendUserId");
						m.sUserName = mail.getString("sendUserName");
						m.sUserPhoto = mail.getString("sendUserPic");
						if (m.sid == UserNow.current().userID)
							m.isFromSelf = true;
						else
							m.isFromSelf = false;
						mailList.add(m);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
