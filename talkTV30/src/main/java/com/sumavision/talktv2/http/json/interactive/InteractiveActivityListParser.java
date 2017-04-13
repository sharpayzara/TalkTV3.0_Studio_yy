package com.sumavision.talktv2.http.json.interactive;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 互动列表json解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveActivityListParser extends BaseJsonParser {

	public ArrayList<InteractiveActivity> activityList = new ArrayList<InteractiveActivity>();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONArray interactiveObj = content.getJSONArray("activity");
				if (interactiveObj != null && interactiveObj.length() > 0) {
					Type type = new TypeToken<ArrayList<InteractiveActivity>>() {
					}.getType();
					activityList = new Gson().fromJson(
							interactiveObj.toString(), type);
				}
			}
		} catch (JSONException e) {
			android.util.Log.e("InteractiveActivityListTask", e.toString());
		}

	}

}
