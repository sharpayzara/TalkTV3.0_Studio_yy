package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author cx
 * @version 3.0.7
 * @description 演唱会请求
 * @changeLog
 */
public class ConcertRequest extends BaseJsonRequest {
	
	private int programId;
	private String requestName;
	
	public ConcertRequest(int programId, String requestName) {
		this.programId = programId;
		this.requestName = requestName;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", requestName);
			holder.put("version", "3.0.7");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			if (programId != 0) {
				holder.put("programId", programId);
			}
			if (UserNow.current().userID != 0) {
				holder.put("userId", UserNow.current().userID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
