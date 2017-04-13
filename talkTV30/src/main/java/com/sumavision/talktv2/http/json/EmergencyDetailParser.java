package com.sumavision.talktv2.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.EmergencyDetailData;
import com.sumavision.talktv2.bean.EmergencyDetailPic;
import com.sumavision.talktv2.bean.EmergencyDetailPlayData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

public class EmergencyDetailParser extends BaseJsonParser {

	public EmergencyDetailData emergencyDetail;

	@Override
	public void parse(JSONObject jsonObject) {
		emergencyDetail = new EmergencyDetailData();
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
			emergencyDetail.emDetailname = content.optString("name");
			emergencyDetail.emDetailTopicId = content.optLong("topicId");
			emergencyDetail.emDetailTalkCount = content.optInt("talkCount");
			emergencyDetail.emDetailTime = content.optString("time");
			emergencyDetail.emDetailObjectId = content.optLong("objectId");
			emergencyDetail.emDetailProgramId = content.optLong("programId");
			// 推荐页面顶部焦点图
			JSONArray play = content.optJSONArray("play");
			// List<RecommendData> lr = new ArrayList<RecommendData>();
			List<EmergencyDetailPlayData> edpd = new ArrayList<EmergencyDetailPlayData>();
			for (int i = 0; i < play.length(); ++i) {
				EmergencyDetailPlayData r = new EmergencyDetailPlayData();
				JSONObject data = play.optJSONObject(i);
				r.emPlayName = data.optString("name");
				r.emPlayPic = data.optString("pic");
				r.emPlayUrl = data.optString("url");
				r.emPlayVideo = data.optString("video");
				edpd.add(r);
			}
			emergencyDetail.emDetaiPlayList = edpd;

			// 栏目数组
			if (content.has("pic")) {
				JSONArray objectList = content.optJSONArray("pic");
				List<EmergencyDetailPic> edp = new ArrayList<EmergencyDetailPic>();
				for (int i = 0; i < objectList.length(); i++) {
					EmergencyDetailPic dp = new EmergencyDetailPic();
					JSONObject data = objectList.optJSONObject(i);
					dp.emDetailPicContent = data.optString("content");
					dp.emDetailPicPic = data.optString("pic");
					edp.add(dp);
				}
				emergencyDetail.emDetailPicList = edp;
			}
		}
	}

}
