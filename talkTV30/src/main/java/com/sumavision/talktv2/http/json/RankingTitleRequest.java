package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @version 3.0
 * @description 片库首页请求
 * @changeLog
 */
public class RankingTitleRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.evaluateColumnList);
			holder.put("version", "2.5.4");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
//			holder.put("first", 0);
//			holder.put("count", 10);
		} catch (JSONException e) {
			e.printStackTrace();
			mLog.e(e.toString());
		}
		return holder;
	}
}
