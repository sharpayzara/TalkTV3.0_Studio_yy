package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 实体物品领取信息提交json解析
 * 
 * @author suma-hpb
 * 
 */
public class CommitReceiverInfoParser extends BaseJsonParser {

	@Override
	public void parse(JSONObject obj) {
		errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
	}

}
