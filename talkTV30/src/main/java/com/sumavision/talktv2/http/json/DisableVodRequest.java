package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加预约请求
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DisableVodRequest extends BaseJsonRequest {

	private int subid,type;

	public DisableVodRequest(int subid, int type) {
		this.subid = subid;
		this.type = type;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.disablePlayVod);
			jsonObject.put("version", "3.0.4");
			jsonObject.put("playVodId", subid);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("type",type);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
