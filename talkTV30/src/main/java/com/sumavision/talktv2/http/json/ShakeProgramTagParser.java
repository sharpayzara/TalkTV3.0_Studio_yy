package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.GeneralData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 摇一摇类型解析
 * 
 * @author cx
 * @version
 * @description
 */
public class ShakeProgramTagParser extends BaseJsonParser {
	public ArrayList<GeneralData> list;
	public String scrollText;
	
	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		list = new ArrayList<GeneralData>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				if (content != null) {
					scrollText = content.optString("rollStr");
					JSONArray tag = content.optJSONArray("tag");
					if (tag != null) {
						for (int i = 0; i < tag.length(); i++) {
							JSONObject obj = tag.optJSONObject(i);
							GeneralData d = new GeneralData();
							d.id = (int) obj.optLong("id");
							d.name = obj.optString("name");
							list.add(d);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
