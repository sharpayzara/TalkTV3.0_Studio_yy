package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 取消追剧请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChaseDeleteRequest extends BaseJsonRequest {

	private String programId;

	public ChaseDeleteRequest(String programId) {
		super();
		this.programId = programId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.chaseDelete);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("userId", UserNow.current().userID);
			holder.put("programIds", programId);
			holder.put("sessionId", UserNow.current().sessionID);
			holder.put("jsession", UserNow.current().jsession);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
