package com.sumavision.talktv2.http.json.interactive;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveCyclopedia;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 百科json解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveCyclopediaParser extends BaseJsonParser {
	public ArrayList<InteractiveCyclopedia> keyWordsList = new ArrayList<InteractiveCyclopedia>();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONArray cyclopediaArr = content.optJSONArray("baike");
				if (cyclopediaArr != null && cyclopediaArr.length() > 0) {
					Type type = new TypeToken<ArrayList<InteractiveCyclopedia>>() {
					}.getType();
					keyWordsList = new Gson().fromJson(
							cyclopediaArr.toString(), type);
				}
			}
		} catch (JSONException e) {
			Log.e("InteractiveCyclopediaListTask-parse", e.toString());
		}

	}

}
