package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 3.0
 * @description 片库节目页每个标签对应的请求
 * @changeLog
 */
public class LibNormalDetailRequest extends BaseJsonRequest {
	private int columnId;
	private int programId;
	private int contentType;
	private int first;
	private int count;
	public LibNormalDetailRequest(int columnId, int programId, int contentType, int first, int count) {
		this.columnId = columnId;
		this.programId = programId;
		this.contentType = contentType;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.vaultColumnsContentTagDetail);
			holder.put("version", JSONMessageType.APP_VERSION_312);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("columnId", columnId);
			holder.put("contentTypeId", contentType);
			holder.put("programTypeId", programId);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
