package com.sumavision.talktv2.http.json;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author 李梦思
 * @version 2.2
 * @createTime 2012-6-14
 * @description 私信列表解析类
 * @changeLog 修改为2.2版本 by 李梦思 2012-12-24
 */
public class FeedbackListParser extends BaseJsonParser {

	public int mailCount;
	public ArrayList<MailData> mailList = new ArrayList<MailData>();
	public ArrayList<MailData> tempList = new ArrayList<MailData>();

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
				mailCount = content.optInt("mailCount");
				if (mailCount > 0) {
					JSONArray mails = content.optJSONArray("mail");
					if (mails != null && mails.length()>0){
						for (int i = 0; i < mails.length(); ++i) {
							JSONObject mail = mails.optJSONObject(i);
							MailData m = new MailData();
							m.id = mail.optLong("id");
							m.content = mail.optString("content");
							m.pic = mail.optString("pic");
							m.timeStemp = mail.optString("createTime");
							m.sid = mail.optInt("sendUserId");
							m.sUserName = mail.optString("sendUserName");
							m.isVip = mail.optInt("isVip")==1?true:false;
							if (!TextUtils.isEmpty(m.pic)) {
								m.pic = Constants.picUrlFor + m.pic
										+ Constants.PIC_BIG;
							}
							m.sUserPhoto = mail.optString("sendUserPic");
							if (!TextUtils.isEmpty(m.sUserPhoto)) {
								m.sUserPhoto = Constants.picUrlFor + m.sUserPhoto
										+ Constants.PIC_SMALL;
							}
							m.type = mail.optInt("mailType");
							JSONObject obj = mail.optJSONObject("hideParameter");
							if (obj != null) {
								if (m.type == MailData.TYPE_PROGRAM
										|| m.type == MailData.TYPE_PLAY) {
									m.otherId = obj.optLong("programId");
								} else if (m.type == MailData.TYPE_LIVE_PLAY) {
									m.otherId = obj.optLong("channelId");
								} else if (m.type == MailData.TYPE_ACTIVITY) {
									m.otherId = obj.optLong("activityId");
								} else if (m.type == MailData.TYPE_EXHCNAGE_GOODS) {
									m.otherId = obj.optLong("hotGoodsId");
								} else if (m.type == MailData.TYPE_ZONE) {
									m.otherId = obj.optLong("zoneId");
								} else if (m.type == MailData.TYPE_SPECIAL) {
									m.otherId = obj.optLong("columnsId");
								}
							}
							JSONObject urlObj = mail.optJSONObject("hideContent");
							if (urlObj != null) {
								if (m.type == MailData.TYPE_LIVE_PLAY) {
									m.playVideopath = urlObj.optString("playUrl");
								} else if (m.type == MailData.TYPE_WEB) {
									m.url = urlObj.optString("url");
								} else if (m.type == MailData.TYPE_PLAY) {
									m.url = urlObj.optString("webUrl");
									m.playVideopath = urlObj.optString("videoPath");
									m.superPath = urlObj
											.optString("phoneSuperPath");
									m.highPath = urlObj.optString("highQualityUrl");
								} else if (m.type == MailData.TYPE_SPECIAL) {
									m.specialType = urlObj.optInt("type");
								}
							}
							if (m.sid == UserNow.current().userID || m.sid != 191697)
								m.isFromSelf = true;
							else
								m.isFromSelf = false;
							tempList.add(m);
						}
					}
					if (tempList != null && tempList.size()>0){
						mailList.clear();
						mailList.addAll(tempList);
						Collections.reverse(mailList);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
