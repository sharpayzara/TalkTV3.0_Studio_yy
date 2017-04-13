package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 专题数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SpecialSubDetailParser extends BaseJsonParser {
	public ArrayList<VodProgramData> columnList;
	public int count;
	public String longIntro;
	public long columnId;
	public int columnType;
	public int columnVideoCount;

	@Override
	public void parse(JSONObject jsonObject) {
		columnList = null;
		columnList = new ArrayList<VodProgramData>();
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
				JSONObject room = jsonObject.getJSONObject("content");
				count = room.optInt("columnVideoCount", 0);
				longIntro = room.optString("longIntro");
				columnId = room.optLong("columnId");
				columnType = room.optInt("columnType");
				columnVideoCount = room.optInt("columnVideoCount");

				if (count > 0) {
					JSONArray column = room.getJSONArray("columnVideo");
					for (int j = 0; j < column.length(); ++j) {
						VodProgramData sub = new VodProgramData();
						JSONObject subColumn = column.getJSONObject(j);
						sub.id = subColumn.optLong("programId") + "";
						sub.name = subColumn.optString("subName");
						sub.pic = subColumn.optString("pic");
						sub.intro = subColumn.optString("introShort");
						sub.subVideoPath = subColumn.optString("videoPath");
						sub.subHighPath = subColumn.optString("highPlayUrl");
						sub.playLength = subColumn.optInt("playLength");
						sub.playUrl = subColumn.optString("webURL");
						sub.subId = subColumn.optLong("subId") + "";
						columnList.add(sub);
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
