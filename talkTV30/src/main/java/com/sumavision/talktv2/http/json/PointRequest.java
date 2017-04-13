package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 赚积分任务列表
 * 
 * @author suma-hpb
 * 
 */
public class PointRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.userTaskList);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("userId", UserNow.current().userID);
			jsonObject.put("jsession", UserNow.current().jsession);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
