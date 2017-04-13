package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author
 * @version
 * @createTime
 * @description
 * @changLog
 */
public class EmergencyZoneRequest extends BaseJsonRequest {
	long zoneId;

	public EmergencyZoneRequest() {
	}

	public EmergencyZoneRequest(long zoneId) {
		this.zoneId = zoneId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.zoneDetail);
			// 2.3.2新版本推荐页面使用
			holder.put("version", "2.3.8");
			holder.put("client", JSONMessageType.SOURCE);
			// 2.3.2新版本推荐页面使用
			holder.put("jsession", UserNow.current().jsession);
			holder.put("zoneId", zoneId != 0 ? zoneId
					: UserNow.current().zoneId);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
