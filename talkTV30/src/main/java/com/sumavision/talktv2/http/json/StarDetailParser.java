package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.StarData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;

/**
 * @author
 * @version 2.2
 * @description 明星详情解析类
 */
public class StarDetailParser extends BaseJsonParser {
	public StarData starData;

	@Override
	public void parse(JSONObject jsonObject) {
		JSONObject content = null;
		starData = new StarData();
		ArrayList<VodProgramData> programList = new ArrayList<VodProgramData>();
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
				content = jsonObject.getJSONObject("content");
				starData.name = content.getString("chineseName");
				starData.nameEng = content.getString("englishName");
				starData.photoBig_V = content.getString("pic");
				starData.starType = content.getString("starSign");
				starData.hobby = content.getString("hobby");
				starData.intro = content.getString("intro");
				if (content.has("photo")) {
					JSONArray photoList = content.getJSONArray("photo");
					starData.picCount = photoList.length();
					String photo[] = new String[starData.picCount];
					for (int i = 0; i < starData.picCount; i++) {
						photo[i] = photoList.getJSONObject(i).getString("pic");
					}
					starData.photo = photo;
				}
				starData.programCount = content.getInt("programCount");
				if (starData.programCount > 0) {
					JSONArray programs = content.getJSONArray("program");
					for (int i = 0; i < programs.length(); ++i) {
						JSONObject program = programs.getJSONObject(i);
						VodProgramData p = new VodProgramData();
						p.id = program.getString("id");
						p.topicId = program.getString("topicId");
						p.pic = program.getString("pic");
						p.name = program.getString("name");
						p.contentTypeName = program.getString("typeName");
						programList.add(p);
					}
					starData.program = programList;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
