package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更多剧集列表
 * 
 * @author fa
 * @version
 * @description
 */
public class LoadSubListRequest extends BaseJsonRequest {

	int programId,first,platformId;
	int count = 20;
	public LoadSubListRequest(int programId,int platformId, int first, int count) {
		this.programId = programId;
		this.first = first;
		this.platformId = platformId;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.loadSubList);
			requestObj.put("version", JSONMessageType.APP_VERSION_311);
			requestObj.put("progId", programId);
			requestObj.put("first",first);
			requestObj.put("count",count);
			requestObj.put("platformId",platformId);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return requestObj;
	}

}
