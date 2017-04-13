package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VersionData;

/**
 * 新版本获取解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GetAppNewVersionParser extends BaseJsonParser {
	public VersionData versionData = new VersionData();
	public String msg;

	@Override
	public void parse(JSONObject jsonObject) {
		JSONObject content = null;
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			msg = jsonObject.getString("msg");
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					content = jsonObject.getJSONObject("content");
					if (content != null) {
						versionData.versionId = content.optString("vid");
						versionData.info = content.optString("info");
						versionData.pubDate = content.optString("pubDate");
						versionData.size = content.optInt("size");
						versionData.downLoadUrl = content.optString("url");
						boolean control = content.optBoolean("control");
						if (control) {
							versionData.force = 1;
						} else {
							versionData.force = 0;
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
