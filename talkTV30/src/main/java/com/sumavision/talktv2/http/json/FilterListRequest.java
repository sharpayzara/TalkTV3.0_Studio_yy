package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @version 3.0
 * @description 筛选页获取数据的请求
 * @changeLog
 */
public class FilterListRequest extends BaseJsonRequest {
	private int columnId;

	public FilterListRequest(int columnId) {
		this.columnId = columnId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.filterList);
			holder.put("version", JSONMessageType.APP_VERSION_THREE);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("columnsId", columnId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
