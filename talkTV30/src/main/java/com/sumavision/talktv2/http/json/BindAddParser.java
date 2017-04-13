package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 添加账号绑定数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindAddParser extends BaseJsonParser {

	public ArrayList<ThirdPlatInfo> bindList;
	public int sinaIndex = -1;
	public int qqIndex = -1;
	public int flymeIndex = -1;

	@Override
	public void parse(JSONObject jsonObject) {
		JSONObject user = null;
		sinaIndex = -1;
		qqIndex = -1;
		bindList = new ArrayList<ThirdPlatInfo>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			errMsg = jsonObject.optString("msg");
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				user = jsonObject.getJSONObject("content");
				JSONObject userNowInfo = user.optJSONObject("newUserInfo");
				if (userNowInfo != null) {
					setPointInfo(userNowInfo);
				}
				JSONArray clients = user.getJSONArray("client");
				if (clients != null) {
					for (int i = 0, len = clients.length(); i < len; i++) {
						ThirdPlatInfo data = new ThirdPlatInfo();
						JSONObject obj = clients.optJSONObject(i);
						data.bindId = obj.optInt("id");
						data.token = obj.optString("token");
						data.openId = obj.optString("userId");
						data.expiresIn = obj.optString("validTime");
						data.type = obj.optInt("type");
						if (data.type == ThirdPlatInfo.TYPE_SINA) {
							sinaIndex = i;
						} else if (data.type == ThirdPlatInfo.TYPE_QQ) {
							qqIndex = i;
						} else if (data.type == ThirdPlatInfo.TYPE_FLYME) {
							flymeIndex = i;
						}
						bindList.add(data);
					}
				}
			}
		} catch (JSONException e) {
			errMsg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}

	}

}
