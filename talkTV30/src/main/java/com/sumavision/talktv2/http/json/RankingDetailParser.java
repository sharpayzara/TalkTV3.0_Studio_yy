package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;

public class RankingDetailParser extends BaseJsonParser {
	
	public ArrayList<VodProgramData> listRankingDetail;
	@Override
	public void parse(JSONObject jsonObject) {
		listRankingDetail = null;
		listRankingDetail = new ArrayList<VodProgramData>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");
				
				if (content.has("columnProgram")) {
					JSONArray columnVideo = content.optJSONArray("columnProgram");
					if (columnVideo != null && columnVideo.length() > 0) {
						for (int i = 0; i < columnVideo.length(); i++) {
							VodProgramData r = new VodProgramData();
							JSONObject data = columnVideo.optJSONObject(i);
							r.id = data.optLong("id") + "";
							r.topicId = data.optLong("topicId") + "";
							r.name = data.optString("name");
							r.shortIntro = data.optString("shortIntro");
							r.updateName = data.optString("updateName");
							r.playTimes = data.optInt("playTimes");
							r.pic = Constants.picUrlFor + data.optString("pic") + ".jpg";
							r.monthGoodCount = data.optInt("monthGoodCount");
							listRankingDetail.add(r);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
