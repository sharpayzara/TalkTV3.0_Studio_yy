package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author hpb
 * @version v3.0
 * @description 粉丝列表请求类
 * @changeLog
 */
public class OtherFansRequest extends BaseJsonRequest {

	private int first;
	private int count;
	private int otherid;
	public OtherFansRequest(int first, int count,int otherid) {
		super();
		this.first = first;
		this.count = count;
		this.otherid = otherid;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.fensiList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("first", first);
			holder.put("count", count);
			holder.put("sessionId", UserNow.current().sessionID);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", otherid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
