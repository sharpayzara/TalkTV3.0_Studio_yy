package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 评论列表请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentListRequest extends BaseJsonRequest {

	private int topicId;
	private int cpId;
	private int first;
	private int count;

	public CommentListRequest(int topicId, int cpId, int first, int count) {
		super();
		this.topicId = topicId;
		this.cpId = cpId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.talkList);
			jsonObject.put("version", JSONMessageType.APP_VERSION);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("topicId", topicId);
			if (cpId != 0) {
				jsonObject.put("cpId", cpId);
			}
			jsonObject.put("first", first);
			jsonObject.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
