package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 查询上传列表
 * 
 * @author cx
 * 
 */
public class UploadListRequest extends BaseJsonRequest {
	
	private int first;
	private int count;

	public UploadListRequest(int first, int count) {
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "uploadList");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("userId", UserNow.current().userID);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
