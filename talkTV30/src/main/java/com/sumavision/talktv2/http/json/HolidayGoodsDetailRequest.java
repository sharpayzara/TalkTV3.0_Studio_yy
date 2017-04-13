package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author hpb
 * @version 3.0
 * @description 追剧列表请求类
 * @changeLog
 */
public class HolidayGoodsDetailRequest extends BaseJsonRequest {

	private int goodsId;

	public HolidayGoodsDetailRequest(int goodsId) {
		super();
		this.goodsId = goodsId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.holidayGoodsDetail);
			holder.put("version", JSONMessageType.APP_VERSION_312);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("goodsId",goodsId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
