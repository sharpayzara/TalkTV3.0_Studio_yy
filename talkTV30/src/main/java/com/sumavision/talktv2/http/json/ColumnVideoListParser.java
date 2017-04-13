package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 推荐页的2 3 4 标签：响应解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ColumnVideoListParser extends BaseJsonParser {
	public ArrayList<VodProgramData> programList;
	public int totalCount;

	@Override
	public void parse(JSONObject jsonObject) {
		programList = new ArrayList<VodProgramData>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {

				JSONObject room = jsonObject.getJSONObject("content");

				totalCount= room.getInt("columnVideoCount");
				if (totalCount > 0) {
					JSONArray liveProgram = room.getJSONArray("columnVideo");
					programList.clear();
					for (int i = 0; i < liveProgram.length(); i++) {
						VodProgramData r = new VodProgramData();
						JSONObject data = liveProgram.getJSONObject(i);
						r.id = data.optString("id");
						r.topicId = data.optString("topicId");
						r.name = data.optString("name");
						r.shortIntro = data.optString("shortIntro");
						r.playTimes = data.optInt("playTimes");
						r.pic = data.optString("pic");
						r.ptype = data.optInt("pType");
						double tempPoint = data.optDouble("doubanPoint");
						if (tempPoint > 1.0) {
							r.point = String.valueOf(tempPoint);
						}
						r.updateName = data.optString("updateName");
						r.playType = data.optInt("playType");
						r.playUrl = data.optString("playUrl");
						programList.add(r);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
