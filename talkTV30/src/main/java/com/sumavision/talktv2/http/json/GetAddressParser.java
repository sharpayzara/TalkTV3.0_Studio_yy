package com.sumavision.talktv2.http.json;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.AddressData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 收获地址解析
 * */
public class GetAddressParser extends BaseJsonParser {
	
	public AddressData address;
	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");
				int userId = content.optInt("userId");
				if (content.has("address")) {
					JSONArray addr = content.optJSONArray("address");
					JSONObject data = addr.optJSONObject(0);
					if (data != null) {
						address = new AddressData();
						address.userId = userId;
						address.id = data.optInt("id");
						address.name = data.optString("name");
						address.phone = data.optString("phone");
						address.province = data.optString("province");
						address.city = data.optString("city");
						address.district = data.optString("area");
						address.code = data.optString("postCode");
						address.street = data.optString("street");
					}
				}
			}
		} catch (Exception e) {}
	}

}
