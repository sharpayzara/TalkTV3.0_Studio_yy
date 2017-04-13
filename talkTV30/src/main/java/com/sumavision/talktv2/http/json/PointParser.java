package com.sumavision.talktv2.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PointBase;
import com.sumavision.talktv2.bean.PointList;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 赚积分任务列表
 * 
 * @author suma-hpb
 * 
 */
public class PointParser extends BaseJsonParser {

	public ArrayList<PointList> pointLists = new ArrayList<PointList>();

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (jsonObject.has("sessionId")) {
				UserNow.current().sessionID = jsonObject.optString("sessionId");
			}

			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");
				JSONArray basktask = content.optJSONArray("baskeTask");
				PointList blist1 = new PointList();
				List<PointBase> baseList1 = new ArrayList<PointBase>();
				if (basktask != null) {
					for (int i = 0; i < basktask.length(); i++) {
						PointBase pb = new PointBase();
						JSONObject data = basktask.optJSONObject(i);
						pb.baseName = data.optString("name");
						pb.baseScore = data.optInt("point");
						pb.baseFinish = data.optBoolean("complete");
						baseList1.add(pb);
					}
				}
				blist1.pointList = baseList1;
				blist1.pointTitle = "基础奖励";
				JSONArray daytask = content.optJSONArray("dayTask");
				PointList blist2 = new PointList();
				List<PointBase> baseList2 = new ArrayList<PointBase>();
				if (daytask != null) {
					for (int i = 0; i < daytask.length(); i++) {
						PointBase pb = new PointBase();
						JSONObject data = daytask.optJSONObject(i);
						pb.baseName = data.optString("name");
						if (pb.baseName.equals("参与竞猜")) {
							continue;
						}
						pb.baseScore = data.optInt("point");
						pb.baseFinish = data.optBoolean("complete");
						pb.baseSpecial = data.optBoolean("special");
						baseList2.add(pb);
					}
				}
				blist2.pointList = baseList2;
				blist2.pointTitle = "每日成长";
				JSONArray speedtask = content.optJSONArray("serialTask");
				PointList blist3 = new PointList();
				List<PointBase> baseList3 = new ArrayList<PointBase>();
				if (speedtask != null) {
					for (int i = 0; i < speedtask.length(); i++) {
						PointBase pb = new PointBase();
						JSONObject data = speedtask.optJSONObject(i);
						pb.baseName = data.optString("name");
						pb.baseScore = data.optInt("point");
						pb.baseFinish = data.optBoolean("complete");
						baseList3.add(pb);
					}
				}
				blist3.pointList = baseList3;
				blist3.pointTitle = "加速成长";
				pointLists.add(blist1);
				pointLists.add(blist2);
				pointLists.add(blist3);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
