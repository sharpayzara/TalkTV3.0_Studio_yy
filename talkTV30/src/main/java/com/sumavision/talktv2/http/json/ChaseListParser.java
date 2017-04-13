package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.ChaseData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author hpb
 * @version 3.0
 * @description 追剧列表解析类
 * @changeLog
 */
public class ChaseListParser extends BaseJsonParser {
	public ArrayList<ChaseData> chaseList = new ArrayList<ChaseData>();
	public int chaseCount;

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
				chaseCount = content.getInt("chaseCount");
				if (content.has("chase")) {
					JSONArray chases = content.getJSONArray("chase");
					for (int i = 0; i < chases.length(); ++i) {
						JSONObject chase = chases.getJSONObject(i);
						ChaseData u = new ChaseData();
						u.topicId = chase.getLong("topicId");
						u.id = chase.getLong("id");
						u.programId = chase.getLong("programId");
						u.programPic = chase.getString("programPic");
						u.latestSubId = chase.optLong("latestSubId");
						if (!u.programPic.contains("http://")) {
							u.programPic = Constants.picUrlFor + u.programPic;
						}
						if (!u.programPic.contains(".jpg")) {
							u.programPic += Constants.PIC_SUFF;
						}
						u.programName = chase.getString("programName");
						u.latestSubName = chase.optString("lastSubName");
						u.isOver = chase.getInt("isOver");
						u.pType = chase.optInt("pType");
						chaseList.add(u);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
