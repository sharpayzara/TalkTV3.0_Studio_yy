package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 热门搜索解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class HolidayGoodsDetailParser extends BaseJsonParser {

	public String name,pic,description;

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				if (content != null){
					JSONObject obj = content.optJSONObject("goods");
					name = obj.optString("name");
					pic = obj.optString("pic");
					description = obj.optString("goodsInfo");
				}
			}
		} catch (JSONException e) {
		}

	}

}
