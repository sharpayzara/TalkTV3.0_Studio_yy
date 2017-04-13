package com.sumavision.talktv2.http.callback;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.PlayCountRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShakePlayCountRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnPlayCountListener;

import org.json.JSONObject;

/**
 * 播放次数统计回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ShakePlayCountCallback extends BaseCallback {

	private int programId;
	private OnPlayCountListener listener;
	private Context context;

	public ShakePlayCountCallback(OnHttpErrorListener errorListener, Context context, int programId,
								  OnPlayCountListener listener) {
		super(errorListener);
		this.programId = programId;
		this.context = context;
		this.listener = listener;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
//		if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
//			if (parser.userInfo.point>0) {
//				UserNow.current().setTotalPoint(parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//			}
//		}
		if (listener != null) {
			listener.playCountResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ShakePlayCountRequest(context,programId).make();
	}

}
