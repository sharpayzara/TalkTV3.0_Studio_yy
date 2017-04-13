package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.DownloadRecommendAppParser;
import com.sumavision.talktv2.http.json.DownloadRecommendAppRequest;
import com.sumavision.talktv2.http.listener.OnDownloadRecommendAppListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 推荐软件下载回调
 * 
 * @author suma-hpb
 * 
 */
public class DownloadRecommendAppCallback extends BaseCallback {

	private long appId;
	private OnDownloadRecommendAppListener mListener;

	/**
	 * @param errorListener
	 * @param appId
	 * @param mListener
	 */
	public DownloadRecommendAppCallback(OnHttpErrorListener errorListener,
			long appId, OnDownloadRecommendAppListener mListener) {
		super(errorListener);
		this.appId = appId;
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new DownloadRecommendAppRequest(appId).make();
	}

	DownloadRecommendAppParser mParser = new DownloadRecommendAppParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.OnDownloadRecommendApp(mParser.errCode,
					mParser.totalPoint);
		}

	}

}
