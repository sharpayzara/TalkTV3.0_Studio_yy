package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.ColumnData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 专题数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SpecialParser extends BaseJsonParser {
	public ArrayList<ColumnData> columnList;
	public int subColumnCount;

	@Override
	public void parse(JSONObject jsonObject) {
		columnList = null;
		columnList = new ArrayList<ColumnData>();
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
				subColumnCount = room.optInt("subColumnCount", 0);
				if (subColumnCount > 0) {
					JSONArray column = room.getJSONArray("subColumn");
					for (int j = 0; j < column.length(); j++) {
						ColumnData sub = new ColumnData();
						JSONObject subColumn = column.getJSONObject(j);
						sub.id = subColumn.optInt("id");
						sub.name = subColumn.optString("name");
						if (sub.name.equals("午夜佳片")) {
							continue;
						}
						sub.type = subColumn.optInt("type");
						String pic = subColumn.optString("picWide");
						if (TextUtils.isEmpty(pic)) {
							pic = subColumn.optString("picPad");
						}
						sub.pic = Constants.picUrlFor + pic + ".jpg";
						sub.intro = subColumn.optString("shortIntro");
						sub.playTimes = subColumn.optInt("playTimes");
						columnList.add(sub);
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
