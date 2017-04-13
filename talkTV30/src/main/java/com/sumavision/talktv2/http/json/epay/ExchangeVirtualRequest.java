package com.sumavision.talktv2.http.json.epay;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import comsumavision.utils.ExchangeSecureUtils;

/**
 * 兑换虚拟货币json请求
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeVirtualRequest {
	private int type;
	private int diamondCount;
	private String key;
	private String comm;

	public ExchangeVirtualRequest(int type, int diamondCount, String key,
			String comm) {
		super();
		this.type = type;
		this.diamondCount = diamondCount;
		this.key = key;
		this.comm = comm;
	}

	public String make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.exchangeVirtual);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("userId", UserNow.current().userID);
			jsonObject.put("exchageType", type);
			if (type == 2 && !TextUtils.isEmpty(comm)) {
				jsonObject.put("comm", comm);
			}
			jsonObject.put("diamondCount", diamondCount);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ExchangeSecureUtils()
				.textEncrypt(jsonObject.toString(), key);
	}
}
