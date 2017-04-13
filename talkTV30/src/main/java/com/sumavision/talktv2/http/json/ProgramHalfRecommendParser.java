package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;

public class ProgramHalfRecommendParser extends BaseJsonParser {

	public ArrayList<VodProgramData> recommendData;
	public String recVersion;//智能推荐版本号

	@Override
	public void parse(JSONObject jsonObject) {
		recommendData = new ArrayList<VodProgramData>();
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
				if (content != null) {
					recVersion = content.optString("recVersion");
					if (content.has("program")){
						JSONArray program = content.optJSONArray("program");
						for (int i = 0; i < program.length(); i++) {
							JSONObject obj = program.optJSONObject(i);
							VodProgramData data = new VodProgramData();
							data.id = obj.optInt("id") + "";
							data.name = obj.optString("name");
							data.pic = obj.optString("pic");
							recommendData.add(data);
						}
					}
				}
			}
		} catch (JSONException e) {
		}

	}

}
