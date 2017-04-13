package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 上传节目
 * 
 * @author cx
 * 
 */
public class UploadProgramRequest extends BaseJsonRequest {
	
	private int tagId;
	private String url;
	private String name;

	public UploadProgramRequest(int tagId, String url, String name) {
		this.tagId = tagId;
		this.url = url;
		this.name = name;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "uploadProgram");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("tagId", tagId);
			holder.put("url", url);
			holder.put("pname", name);
			holder.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
