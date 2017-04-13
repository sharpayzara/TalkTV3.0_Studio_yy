package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 上传节目
 * 
 * @author cx
 * @version
 * @description
 */
public class UploadProgramParser extends BaseJsonParser {
	
	@Override
	public void parse(JSONObject jsonObject) {
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
				if (content != null) {
					JSONObject newUserInfo = content.optJSONObject("newUserInfo");
					setPointInfo(newUserInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
