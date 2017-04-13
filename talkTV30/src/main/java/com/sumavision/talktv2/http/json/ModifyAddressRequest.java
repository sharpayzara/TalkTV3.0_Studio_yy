package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.AddressData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 添加收货地址请求
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ModifyAddressRequest extends BaseJsonRequest {

	private AddressData addressData;

	public ModifyAddressRequest(AddressData addressData) {
		this.addressData = addressData;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.addOrUpdateReceivePlace);
			jsonObject.put("version", "3.0.4");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID != 0)
				jsonObject.put("userId", UserNow.current().userID);
			jsonObject.put("addressId", addressData.id);
			jsonObject.put("name", addressData.name);
			jsonObject.put("phone", addressData.phone);
			jsonObject.put("province", addressData.province);
			jsonObject.put("city", addressData.city);
			jsonObject.put("area", addressData.district);
			jsonObject.put("street", addressData.street);
			jsonObject.put("postCode", addressData.code);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
