package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

/**
 * 推荐页的2 3 4 标签请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ColumnVideoListRequest extends BaseJsonRequest {

	private int id;
	private int first;
	private int count;

	public ColumnVideoListRequest(int id, int first, int count) {
		super();
		this.id = id;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.columnVideoList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("columnId", id);
			holder.put("first", first);
			holder.put("count", count);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return holder;
	}

}
