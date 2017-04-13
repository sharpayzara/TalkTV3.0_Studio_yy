package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 筛选页数据获取
 * */
public class FilterListParser extends BaseJsonParser {

	public ArrayList<String> listType;
	public ArrayList<String> listActor;
	public ArrayList<String> listAge;
	public ArrayList<String> listCountry;

	@Override
	public void parse(JSONObject jsonObject) {
		listCountry = null;
		listAge = null;
		listActor = null;
		listType = null;
		listCountry = new ArrayList<String>();
		listAge = new ArrayList<String>();
		listActor = new ArrayList<String>();
		listType = new ArrayList<String>();
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
					parseFilterData("type", content, listType);
					parseFilterData("actor", content, listActor);
					parseFilterData("age", content, listAge);
					parseFilterData("country", content, listCountry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseFilterData(String type, JSONObject content, ArrayList<String> list) throws Exception {
		if (content.has(type)) {
			JSONObject obj = content.optJSONObject(type);
			String []names;
			String name;
			if (obj.has("name")) {
				name = obj.optString("name");
				names = name.split(",");
				for (int i = 0; i < names.length; i++) {
					list.add(names[i]);
				}
			}
		}
	}
}
