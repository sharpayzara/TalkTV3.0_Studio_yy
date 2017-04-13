package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.SubVideoData;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 反馈数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FindSubVideoUrlParser extends BaseJsonParser {

	public SubVideoData subVideoData;

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
			if (errCode != JSONMessageType.SERVER_CODE_OK) {
				return;
			}
			JSONObject content = jsonObject.optJSONObject("content");
			if (content != null){
				subVideoData = new SubVideoData();
				subVideoData.platformId = content.optInt("platformId");
				subVideoData.programId = content.optInt("programId");
				subVideoData.subId = content.optInt("subId");
				subVideoData.ptype = content.optInt("ptype");
				subVideoData.stage = content.optInt("stage") -1;
				subVideoData.webUrl = content.optString("webUrl");
				subVideoData.videoPath = content.optString("videoPath");
				subVideoData.progName = content.optString("progName");
				subVideoData.subName = content.optString("subName");
				subVideoData.skipToWeb = content.optInt("skipWeb") == 1 ? true:false;
				subVideoData.isNative = content.optInt("waShu") == 1 ? false:true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
