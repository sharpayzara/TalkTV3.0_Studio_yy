package com.sumavision.talktv2.service;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.GoodTalkRequest;
import com.sumavision.talktv2.http.json.ResultParser;

import android.app.IntentService;
import android.content.Intent;

public class GoodCommentService extends IntentService {
	
	private int objectType;
	private ResultParser resultParser = new ResultParser();
	
	public GoodCommentService() {
		super("GoodCommentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		objectType = 11;
		VolleyHelper.post(new GoodTalkRequest(objectType).make(), new ParseListener(resultParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (resultParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					UserNow.current().setTotalPoint(resultParser.userInfo.totalPoint,resultParser.userInfo.vipIncPoint);
				}
			}
		}, null);
	}

}
