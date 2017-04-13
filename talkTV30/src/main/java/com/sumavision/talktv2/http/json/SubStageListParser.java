package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubStageListParser extends BaseJsonParser {
	public List<JiShuData> subList;
	public List<String> tags = new ArrayList<String>();
	public boolean normalOrder = true;
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
				if (content == null){
					return;
				}
				normalOrder = content.optInt("order") == 0 ? true:false;
				JSONArray subArr = content.optJSONArray("progSub");
				if (subArr != null && subArr.length() > 0) {
					subList = new ArrayList<JiShuData>();
					int sublen = subArr.length();
					for (int sindex = 0; sindex < sublen; sindex++) {
						JSONObject subObj = subArr.optJSONObject(sindex);
						JiShuData jishu = new JiShuData();
						jishu.id = subObj.optInt("subId");
						jishu.name = subObj.optString("name");
						jishu.url = subObj.optString("webUrl");
						jishu.topicId = subObj.optInt("topicId");
						jishu.videoPath = subObj.optString("videoPath");
						jishu.pic = subObj.optString("pic");
						jishu.playCount = subObj.optInt("playCount");
						jishu.shortName = subObj.optString("shortName");
						subList.add(jishu);
					}
				}
				JSONArray tagArray = content.optJSONArray("stageTag");
				if (tagArray != null && tagArray.length()>0){
					int leng = tagArray.length();
					tags.clear();
					for (int i=0; i<leng; i++){
						tags.add(tagArray.optJSONObject(i).optString("tag"));
					}
				}else{
					tags.clear();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
