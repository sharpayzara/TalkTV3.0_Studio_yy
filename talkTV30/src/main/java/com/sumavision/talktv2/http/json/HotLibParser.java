package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.HotLibType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

public class HotLibParser extends BaseJsonParser {
	
	public ArrayList<RecommendData> listRecommend;
	public ArrayList<HotLibType> listColumn;
	@Override
	public void parse(JSONObject jsonObject) {
		listRecommend = null;
		listColumn = null;
		listRecommend = new ArrayList<RecommendData>();
		listColumn = new ArrayList<HotLibType>();
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
				if (content.has("recommend")) {
					JSONArray recommend = content.optJSONArray("recommend");
					for (int i = 0; i < recommend.length(); i++) {
						RecommendData r = new RecommendData();
						JSONObject data = recommend.optJSONObject(i);
						r.id = data.optLong("objectId");
						r.name = data.optString("name");
						r.pic = Constants.picUrlFor + data.optString("pic") + ".jpg";
						r.type = data.optInt("type");
						r.url = data.optString("url");
						r.otherId = data.optLong("otherId");
						r.videoPath = data.optString("videoPath");
						listRecommend.add(r);
					}
				}
				if (content.has("columns")) {
					JSONArray columns = content.optJSONArray("columns");
					for (int i = 0; i < columns.length(); i++) {
						HotLibType h = new HotLibType();
						JSONObject data = columns.optJSONObject(i);
						h.id = data.optLong("id");
						h.name = data.optString("name");
						h.icon = Constants.picUrlFor + data.optString("pic") + ".jpg";
						h.type = data.optInt("type");
						h.programType = data.optInt("programTypeId");
						listColumn.add(h);
					}
				}
			}
		} catch (Exception e) {}
	}

}
