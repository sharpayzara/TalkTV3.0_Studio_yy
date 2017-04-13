package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 专题类节目信息请求数据类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SubjectProgramListRequest extends BaseJsonRequest {

	private int programId;
	private int first;
	private int count;

	public SubjectProgramListRequest(int programId, int first, int count) {
		super();
		this.programId = programId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.subjectProgramList);
			holder.put("version", "2.3.2");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("programId", programId);
			holder.put("first", first);
			holder.put("count", count);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
