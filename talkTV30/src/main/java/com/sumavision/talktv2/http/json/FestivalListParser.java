package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * */
public class FestivalListParser extends BaseJsonParser {
	
	public ArrayList<String> listName;
	private final String[] rewards = new String[] { "一等奖", "二等奖", "三等奖", "四等奖", "五等奖",
			"六等奖", "七等奖", };
	
	@Override
	public void parse(JSONObject jsonObject) {
		listName = null;
		listName = new ArrayList<String>();
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
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.optJSONObject("content");
					if (content.has("winner")) {
						JSONArray winner = content.optJSONArray("winner");
						for (int i = 0; i < winner.length(); i++) {
							JSONObject obj = winner.optJSONObject(i);
							String name = obj.optString("userName");
							String good = obj.optString("goodsName");
							int level = obj.optInt("rewardLevel");
							StringBuilder builder = new StringBuilder(name + "中了" + rewards[level - 1]);
							listName.add(builder.toString());
						}
					}
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
