package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author hpb
 * @version v2.2
 * @description 推荐软件列表请求类
 * @changeLog
 */
public class RecommendAppRequest extends BaseJsonRequest {

	private int first;
	private int count;

	public RecommendAppRequest(int first, int count) {
		super();
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.recommendAppList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("count", count);
			holder.put("first", first);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
