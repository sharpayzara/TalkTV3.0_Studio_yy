package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadSubListParser extends BaseJsonParser {
	public List<JiShuData> subList;
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
				JSONObject content = jsonObject.optJSONObject("content");
				if (content == null){
					return;
				}
				JSONArray subArr = content.optJSONArray("progSub");
				if (subArr != null && subArr.length() > 0) {
					subList = new ArrayList<JiShuData>();
					int sublen = subArr.length();
					for (int sindex = 0; sindex < sublen; sindex++) {
						JSONObject subObj = subArr.optJSONObject(sindex);
						JiShuData jishu = new JiShuData();
						jishu.id = subObj.optInt("subId");
						jishu.name = subObj.optString("name");
						jishu.shortName = subObj.optString("shortName");
						jishu.url = subObj.optString("webUrl");
						jishu.topicId = subObj.optInt("topicId");
						jishu.videoPath = subObj.optString("videoPath");
						jishu.pic = subObj.optString("pic");
						jishu.playCount = subObj.optInt("playCount");
						subList.add(jishu);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
