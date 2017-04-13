package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
/**
 * 节目详情请求数据
 * @author suma-hpb
 * @version 
 * @description
 */
public class ProgramDetailRequest extends BaseJsonRequest {

	private long programId;

	public ProgramDetailRequest(long programId) {
		super();
		this.programId = programId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.programDetail);
			jsonObject.put("version","2.3.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("programId", programId);
			int userId = UserNow.current().userID;
			if (userId != 0) {
				jsonObject.put("userId", userId);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
