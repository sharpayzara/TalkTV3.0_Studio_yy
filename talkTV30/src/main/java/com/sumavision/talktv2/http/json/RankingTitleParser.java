package com.sumavision.talktv2.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RankingTitleData;
import com.sumavision.talktv2.bean.UserNow;

public class RankingTitleParser extends BaseJsonParser {
	
	public List<RankingTitleData> listRankingTitle;
	public int count;
	@Override
	public void parse(JSONObject jsonObject) {
		listRankingTitle = null;
		listRankingTitle = new ArrayList<RankingTitleData>();
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
				if (content.has("column")) {
					JSONArray column = content.optJSONArray("column");
					if (column != null) {
						for (int i = 0; i < column.length(); i++) {
							JSONObject data = column.optJSONObject(i);
							RankingTitleData r = new RankingTitleData();
							r.columnName = data.optString("name");
							r.columnId = data.optLong("id") + "";
							listRankingTitle.add(r);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
