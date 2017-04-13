package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 转发列表请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ForwardListRequest extends BaseJsonRequest {

	private int first;
	private int count;

	public ForwardListRequest(int first, int count) {
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.talkForwardList);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("talkId", CommentData.current().talkId);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
