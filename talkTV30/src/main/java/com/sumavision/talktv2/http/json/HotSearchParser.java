package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.KeyWordData;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 热门搜索解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class HotSearchParser extends BaseJsonParser {

	public ArrayList<KeyWordData> keyWordList = new ArrayList<KeyWordData>();

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
				JSONArray keyWords = content.optJSONArray("keywords");
				if (keyWords != null) {
					for (int i = 0; i < keyWords.length(); ++i) {
						KeyWordData keyWordData = new KeyWordData();
						keyWordData.name = keyWords.getJSONObject(i).optString(
								"name");
						keyWordList.add(keyWordData);
					}
				}
			}
		} catch (JSONException e) {
		}

	}

}
