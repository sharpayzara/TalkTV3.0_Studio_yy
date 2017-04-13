package com.sumavision.talktv2.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.EmergencyFocusData;
import com.sumavision.talktv2.bean.EmergencyObjectData;
import com.sumavision.talktv2.bean.EmergencyPicData;
import com.sumavision.talktv2.bean.EmergencyZoneData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author
 * @version 2.2
 * @createTime
 * @description 急诊室专题活动
 * @changeLog
 */
public class EmergencyZoneParser extends BaseJsonParser {
	public EmergencyZoneData emergencyZone;

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			emergencyZone = new EmergencyZoneData();
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");

				// 推荐页面顶部焦点图
				JSONArray focus = content.optJSONArray("focus");
				if (focus != null) {
					List<EmergencyFocusData> efd = new ArrayList<EmergencyFocusData>();
					for (int i = 0; i < focus.length(); ++i) {
						EmergencyFocusData r = new EmergencyFocusData();
						JSONObject data = focus.optJSONObject(i);
						r.focusObjectId = data.optLong("objectId");
						r.focustype = data.optInt("type");
						r.focusName = data.optString("name");
						r.focuspic = data.optString("pic");
						efd.add(r);
					}
					emergencyZone.emergencyFocus = efd;
				}

				// 栏目数组
				if (content.has("object")) {
					JSONArray objectList = content.optJSONArray("object");
					List<EmergencyObjectData> eo = new ArrayList<EmergencyObjectData>();
					// RecommendPageData.current().liveProgramCount =
					// liveProgram
					// .length();
					for (int i = 0; i < objectList.length(); i++) {
						EmergencyObjectData eod = new EmergencyObjectData();
						JSONObject data = objectList.optJSONObject(i);
						eod.objectId = data.optLong("id");
						eod.objectIntro = data.optString("intro");
						// eod.objectPic = data.optString("playTimes");
						eod.objectTalkcount = data.optInt("talkCount");
						eod.objectPiccount = data.optInt("picCount");
						eod.objectTitle = data.optString("title");
						eod.objectType = data.optInt("type");

						if (data.has("pic")) {
							JSONArray netPicAddrList = data.getJSONArray("pic");
							ArrayList<EmergencyPicData> epDatas = new ArrayList<EmergencyPicData>();
							for (int ipic = 0; ipic < netPicAddrList.length(); ipic++) {

								JSONObject play = netPicAddrList
										.optJSONObject(ipic);
								EmergencyPicData epd = new EmergencyPicData();

								epd.objectpicid = play.optString("id");
								epd.objectpicpic = play.optString("pic");

								epDatas.add(epd);
							}
							eod.objectPic = epDatas;
						}
						eo.add(eod);
					}
					emergencyZone.emergencyObject = eo;
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
