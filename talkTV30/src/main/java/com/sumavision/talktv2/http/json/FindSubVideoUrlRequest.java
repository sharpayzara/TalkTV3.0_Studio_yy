package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 反馈请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FindSubVideoUrlRequest extends BaseJsonRequest {

	int programId,subId,position;
	public FindSubVideoUrlRequest(int programId,int subId,int position) {
		this.programId = programId;
		this.subId = subId;
		this.position = position;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.findSubVideoUrl);
			requestObj.put("version", JSONMessageType.APP_VERSION_311);
			requestObj.put("progId", programId);
			requestObj.put("subId",subId);
			requestObj.put("position",position);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return requestObj;
	}

}
