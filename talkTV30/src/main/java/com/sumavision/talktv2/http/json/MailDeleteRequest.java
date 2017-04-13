package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 删除私信
 * 
 * @author suma-hpb
 * 
 */
public class MailDeleteRequest extends BaseJsonRequest {

	String[] otherUserIds;

	public MailDeleteRequest(String[] otherUserIds) {
		this.otherUserIds = otherUserIds;
	}

	@Override
	public JSONObject make() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("method", Constants.mailDelete);
			obj.put("client", JSONMessageType.SOURCE);
			obj.put("jsession", UserNow.current().jsession);
			obj.put("userId", UserNow.current().userID);
			StringBuffer ids = new StringBuffer();
			for (String s : otherUserIds) {
				ids.append(s).append(",");
			}
			obj.put("otherUserIds", ids.substring(0, ids.length() - 1));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
