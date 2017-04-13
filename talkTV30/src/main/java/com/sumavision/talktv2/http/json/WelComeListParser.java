package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UpdateData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.WelcomeData;

/**
 * 欢迎页数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class WelComeListParser extends BaseJsonParser {
	public ArrayList<WelcomeData> welcomeList = new ArrayList<WelcomeData>();

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
				if (content.has("welcome")) {
					JSONArray welcomes = content.getJSONArray("welcome");
					for (int i = 0; i < welcomes.length(); ++i) {
						JSONObject w = welcomes.getJSONObject(i);
						WelcomeData wd = new WelcomeData();

						wd.picUrl = w.optString("pic");
						wd.endTime = w.optString("endTime");
						wd.startTime = w.optString("startTime");

						if (i == 0) {
							UpdateData.current().logoDownURL = wd.picUrl;
							UpdateData.current().startTime = wd.startTime;
							UpdateData.current().endTime = wd.endTime;
							if (wd.picUrl.indexOf(".") < 0) {
								UpdateData.current().logoServerFileName = wd.picUrl
										.substring(wd.picUrl.lastIndexOf("/"))
										+ "b.jpg";
								UpdateData.current().logoFileName = UpdateData
										.current().logoServerFileName;
							} else {
								UpdateData.current().logoServerFileName = wd.picUrl
										.substring(wd.picUrl.lastIndexOf("/"));
								UpdateData.current().logoFileName = UpdateData
										.current().logoServerFileName;
							}
						}
						welcomeList.add(wd);
					}

					UpdateData.current().isNeedUpdateLogo = true;
					UpdateData.current().listWelconme = welcomeList;
					if (welcomes.length() == 0) {
						UpdateData.current().isNeedUpdateLogo = false;
						UpdateData.current().isOnNewLogoTime = false;
						UpdateData.current().logoDownURL = "";
						UpdateData.current().startTime = "00";
						UpdateData.current().endTime = "00";

					}
				}
			} else {
				UpdateData.current().isNeedUpdateLogo = false;
				UpdateData.current().isOnNewLogoTime = false;
				UpdateData.current().logoDownURL = "";
				UpdateData.current().startTime = "00";
				UpdateData.current().endTime = "00";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
