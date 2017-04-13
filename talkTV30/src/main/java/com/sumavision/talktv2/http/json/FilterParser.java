package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;

/**
 * 片库下每个频道标签下详细数据解析
 * */
public class FilterParser extends BaseJsonParser {
	
	public ArrayList<VodProgramData> listProgram;
	public int count;
	
	@Override
	public void parse(JSONObject jsonObject) {
		count = 0;
		listProgram = null;
		listProgram = new ArrayList<VodProgramData>();
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
					count = content.optInt("count");
					if (content.has("program")) {
						JSONArray program = content.optJSONArray("program");
						if (program != null && program.length() > 0) {
							for (int i = 0; i < program.length(); i++) {
								VodProgramData v = new VodProgramData();
								JSONObject obj = program.getJSONObject(i);
								v.id = new String(obj.optInt("id") + "");
								v.ptype = obj.optInt("programType");
								v.updateName = obj.optString("updateName");
								v.point = obj.optString("doubanPoint", "");
								v.contentTypeName = obj.optString("contentType");
								v.name = obj.optString("title");
								v.pic = Constants.picUrlFor + obj.optString("pic") + ".jpg";
								v.playTimes = obj.optInt("playTime");
								listProgram.add(v);
							}
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
