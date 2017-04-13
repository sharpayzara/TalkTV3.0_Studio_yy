package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;

/**
 * 专题类节目信息解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SubjectProgramListParser extends BaseJsonParser {
	public ArrayList<VodProgramData> programList = new ArrayList<VodProgramData>();

	@Override
	public void parse(JSONObject jsonObject) {
		programList.clear();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				if (content.has("program")) {
					JSONArray programArr = content.getJSONArray("program");
//					int programCount = programArr.length();
					for (int i = 0; i < programArr.length(); i++) {
						VodProgramData vpd = new VodProgramData();
						JSONObject p = programArr.getJSONObject(i);
						vpd.id = p.optString("id");
						vpd.topicId = p.optString("topicId");
						vpd.name = p.optString("name");
						vpd.shortIntro = p.optString("shortIntro");
						vpd.playTimes = p.optInt("playTimes");
						vpd.pic = p.optString("pic") + Constants.PIC_MIDDLE;
						// ptype 字段暂时不取，后面如有需要添加即可
						// vpd.ptype = p.getInt("ptype");
						double tempPoint = p.optDouble("doubanPoint");
						if (tempPoint > 1.0) {
							vpd.point = String.valueOf(tempPoint);
						}
						vpd.updateName = p.optString("updateName");
						programList.add(vpd);
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
