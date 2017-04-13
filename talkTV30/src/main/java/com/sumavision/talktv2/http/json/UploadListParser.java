package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 查询上传列表
 * 
 * @author cx
 * @version
 * @description
 */
public class UploadListParser extends BaseJsonParser {
	
	public int uploadCount;
	public ArrayList<VodProgramData> listProgram;
	
	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		listProgram = new ArrayList<VodProgramData>();
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
					uploadCount = content.optInt("uploadCount");
					JSONArray program = content.optJSONArray("program");
					if (program != null) {
						for (int i = 0; i < program.length(); i++) {
							JSONObject obj = program.optJSONObject(i);
							VodProgramData v = new VodProgramData();
							v.id = obj.optLong("progId") + "";
							v.name = obj.optString("pname");
							v.pic = obj.optString("pic");
							v.playUrl = obj.optString("url");
							v.monthGoodCount = obj.optInt("praiseCount");
							listProgram.add(v);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
