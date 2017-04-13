package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 实物领取信息提交json请求
 * 
 * @author suma-hpb
 * 
 */
public class CommitReceiverInfoRequest extends BaseJsonRequest {

	private long userGoodsId;
	private ReceiverInfo info;

	public CommitReceiverInfoRequest(long userGoodsId, ReceiverInfo info) {
		this.userGoodsId = userGoodsId;
		this.info = info;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.submitAddress);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("userId", UserNow.current().userID);
			object.put("userGoodsId", userGoodsId);
			object.put("realName", StringUtils.AllStrTOUnicode(info.name));
			object.put("address", StringUtils.AllStrTOUnicode(info.address));
			object.put("phone", info.phone);
			object.put("useTicket", info.ticket);
			if (!TextUtils.isEmpty(info.remark)) {
				object.put("remarks", StringUtils.AllStrTOUnicode(info.remark));
			}
			Log.i("mylog", object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
