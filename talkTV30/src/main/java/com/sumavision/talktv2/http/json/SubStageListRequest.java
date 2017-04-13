package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 节目页(3.0)请求
 * 
 * @author suma-hpb
 * 
 */
public class SubStageListRequest extends BaseJsonRequest {

	long programId;
	int platformId;
	int stage;

	public SubStageListRequest(long programId, int platformId, int stage) {
		this.programId = programId;
		this.platformId = platformId;
		this.stage = stage;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.subStageList);
			requestObj.put("version", JSONMessageType.APP_VERSION_311);
			requestObj.put("progId", programId);
			requestObj.put("platformId", platformId);
			requestObj.put("stage", stage+1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}
}
