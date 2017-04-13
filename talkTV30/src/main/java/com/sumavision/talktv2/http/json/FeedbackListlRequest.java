package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 反馈请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FeedbackListlRequest extends BaseJsonRequest {
	private Context context;
	private int first,count;

	public FeedbackListlRequest(Context context,int first,int count){
		this.context = context;
		this.first  = first;
		this.count = count;
	}
	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.feedBackList);
			requestObj.put("version", JSONMessageType.APP_VERSION_313);
			requestObj.put("userId", UserNow.current().userID);
			requestObj.put("imei", AppUtil.getImei(context));
			requestObj.put("first",first);
			requestObj.put("count",count);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return requestObj;
	}

}
