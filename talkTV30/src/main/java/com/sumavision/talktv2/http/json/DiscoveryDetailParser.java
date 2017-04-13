package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.DiscoveryData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

/**
 * 发现页解析
 * 
 * @author yanzhidan
 * 
 */
public class DiscoveryDetailParser extends BaseJsonParser {
	public ArrayList<DiscoveryData> datas;

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			errCode = jsonObject.optInt("code",
					JSONMessageType.SERVER_CODE_ERROR);
			JSONObject content = jsonObject.optJSONObject("content");
			JSONArray array = content.optJSONArray("array");
			datas = new ArrayList<DiscoveryData>();
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					DiscoveryData data = new DiscoveryData();
					data.id = obj.optInt("id");
					data.name = obj.optString("name");
					data.pic = obj.optString("pic");
					if (!TextUtils.isEmpty(data.pic)) {
						if (!data.pic.startsWith("http")) {
							data.pic = Constants.picUrlFor + data.pic
									+ Constants.PIC_SUFF;
						}
					}
					data.point = obj.optInt("point");
					data.goodsId = obj.optInt("goodsId");
					data.count = obj.optInt("count");
					data.discoveryType = obj.optInt("objectType");
					if (data.discoveryType == 1){
						datas.add(data);
					}
				}
			}

		} catch (Exception e) {
			errCode = JSONMessageType.SERVER_CODE_ERROR;
			e.printStackTrace();
		}
	}

}
