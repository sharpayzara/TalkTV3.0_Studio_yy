package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 节目页秀场开关
 * 
 * @author suma-hpb
 * 
 */
public class UshowOnOffRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.searchShowOnOff);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
