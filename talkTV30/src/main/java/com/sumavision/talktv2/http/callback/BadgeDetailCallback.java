package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.BadgeDetailParser;
import com.sumavision.talktv2.http.json.BadgeDetailRequest;
import com.sumavision.talktv2.http.listener.OnBadgeDetailListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 徽章详情回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BadgeDetailCallback extends BaseCallback {

	private int badgeId;
	private OnBadgeDetailListener listener;

	public BadgeDetailCallback(OnHttpErrorListener errorListener, int badgeId,
			OnBadgeDetailListener listener) {
		super(errorListener);
		this.badgeId = badgeId;
		this.listener = listener;
	}
	BadgeDetailParser parser = new BadgeDetailParser();
	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}
	@Override
	protected void onResponseDelegate() {
		
		if (listener != null) {
			listener.getBadgeDetail(parser.errCode, parser.badgeDetailData);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new BadgeDetailRequest(badgeId).make();
	}

}
