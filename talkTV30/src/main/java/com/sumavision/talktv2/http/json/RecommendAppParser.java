package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.AppData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * @author 李梦思
 * @version 2.2
 * @createTime 2012-12-24
 * @description 推荐软件解析类
 * @changeLog
 */
public class RecommendAppParser extends BaseJsonParser {
	public ArrayList<AppData> appList;

	@Override
	public void parse(JSONObject jsonObject) {
		appList = new ArrayList<AppData>();
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
				if (content.has("recommendApp")) {
					JSONArray recommendApps = content
							.getJSONArray("recommendApp");
					for (int i = 0; i < recommendApps.length(); ++i) {
						AppData r = new AppData();
						JSONObject data = recommendApps.getJSONObject(i);
						r.id = data.getLong("id");
						r.name = data.getString("name");
						r.pic = data.getString("pic");
						r.url = data.getString("url");
						r.shortIntro = data.getString("shortIntro");
						r.packageName = data.getString("identifyName");
						appList.add(r);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
