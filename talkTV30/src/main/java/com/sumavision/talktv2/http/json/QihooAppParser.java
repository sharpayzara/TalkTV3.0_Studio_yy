package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

public class QihooAppParser extends BaseJsonParser {

	public int point;

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			errCode = jsonObject.optInt("code");
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");
				JSONObject userinfo = content.optJSONObject("newUserInfo");
				point = userinfo.optInt("point", 0);
				UserNow.current().point = userinfo.optInt("point", 0);
				UserNow.current().totalPoint = userinfo.optInt("totalPoint",
						UserNow.current().totalPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
