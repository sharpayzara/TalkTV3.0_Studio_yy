package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author
 * @version 2.2
 * @description 明星详情请求组装类
 */
public class StarDetailRequest extends BaseJsonRequest {

	private int stagerId;

	public StarDetailRequest(int stagerId) {
		this.stagerId = stagerId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.starDetail);
			holder.put("starId", stagerId);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
