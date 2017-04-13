package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 删除上传记录
 * 
 * @author cx
 * 
 */
public class DeleteUploadRequest extends BaseJsonRequest {
	
	private String uploadIds;

	public DeleteUploadRequest(String uploadIds) {
		this.uploadIds = uploadIds;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "deleteUpload");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("userId", UserNow.current().userID);
			holder.put("uploadIds", uploadIds);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
