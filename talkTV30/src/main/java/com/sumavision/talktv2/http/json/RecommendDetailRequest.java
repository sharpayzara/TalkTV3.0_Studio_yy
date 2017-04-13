package com.sumavision.talktv2.http.json;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author
 * @version 3.0
 * @description 推荐页请求类
 * @changLog
 */
public class RecommendDetailRequest extends BaseJsonRequest {
	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.recommendDetail);
			holder.put("version", JSONMessageType.APP_VERSION_312);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("p2pType", 2);
			Log.i("mylog", holder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
