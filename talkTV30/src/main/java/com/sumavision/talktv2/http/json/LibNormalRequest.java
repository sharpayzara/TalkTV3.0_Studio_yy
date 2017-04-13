package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 3.0
 * @description 片库节目请求
 * @changeLog
 */
public class LibNormalRequest extends BaseJsonRequest {
	private int columnId;
	private int first;
	private int count;
	public LibNormalRequest(int columnId, int first, int count) {
		this.columnId = columnId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.vaultColumnsRecommendDetail);
			holder.put("version", JSONMessageType.APP_VERSION_312);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("columnsId", columnId);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
