package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.http.json.GetAppNewVersionParser;
import com.sumavision.talktv2.http.json.GetAppNewVersionRequest;
import com.sumavision.talktv2.http.listener.OnAppNewVersionListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 获取新版本信息回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class NewVersionCallback extends BaseCallback {

	private Context context;
	private OnAppNewVersionListener listener;

	public NewVersionCallback(OnHttpErrorListener errorListener,
			Context context, OnAppNewVersionListener listener) {
		super(errorListener);
		this.context = context;
		this.listener = listener;
	}

	GetAppNewVersionParser parser = new GetAppNewVersionParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getNewVersion(parser.errCode, parser.msg,
					parser.versionData);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new GetAppNewVersionRequest(context).make();
	}

}
