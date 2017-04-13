package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @version 3.0.5
 * @description 不可用模块解析
 * @changeLog
 */
public class MarketNotModuleParser extends BaseJsonParser {

	public ArrayList<String> moduleName;
	public boolean quitAd = false;
	public boolean stopAd = false;
	public boolean kugouAd = false;
	public boolean xunfeiAd = false;
	public boolean vipActivity = false;
	public boolean jvxiaoAd = false;
	public boolean amgWelcome = false;
	public boolean kugouHalf = false;

	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		moduleName = new ArrayList<String>();
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
				JSONObject content = jsonObject.optJSONObject("content");
				JSONArray module = null;
				if (content != null) {
					String quitad = content.optString("quitAd");
					String stopad = content.optString("stopAd");
					String kugouSDK = content.optString("kugouSDK");
					String xunfei = content.optString("kedaxunfei");
					String vip = content.optString("vipActivity");
					String jvxiao = content.optString("jvxiao");
					String amgAd = content.optString("anMaiWelCome");
					String kugouHalfAd = content.optString("kugouHalf");
					if (quitad.equals("0")) {
						quitAd = false;
					} else {
						quitAd = true;
					}
					if (stopad.equals("0")) {
						stopAd = false;
					} else {
						stopAd = true;
					}
					kugouAd = kugouSDK.equals("0") ? false : true;
					xunfeiAd = xunfei.equals("0") ? false : true;
					vipActivity = vip.equals("0") ? false : true;
					jvxiaoAd = jvxiao.equals("0") ? false : true;
					amgWelcome = amgAd.equals("0") ? false : true;
					kugouHalf = kugouHalfAd.equals("0") ? false : true;
					Constants.liveShareTitle = content.optString("shareMsgLive");
					Constants.vodShareTitle = content.optString("shareMsgProg");
					module = content.optJSONArray("module");
				}
				if (module != null) {
					for (int i = 0; i < module.length(); i++) {
						JSONObject obj = module.optJSONObject(i);
						if (obj != null) {
							String code = obj.optString("code");
							if (!TextUtils.isEmpty(code)) {
								moduleName.add(code);
							}
						}
					}
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			errMsg = JSONMessageType.SERVER_NETFAIL;
			e.printStackTrace();
		}
	}

}
