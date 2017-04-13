package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.EmergencyDetailParser;
import com.sumavision.talktv2.http.json.EmergencyDetailRequest;
import com.sumavision.talktv2.http.listener.OnEmergencyDetailListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public class EmergencyDetailCallback extends BaseCallback {

	private OnEmergencyDetailListener listener;
	private long objectId;
	private long zoneId;
	private int type;
	private int way;

	public EmergencyDetailCallback(OnHttpErrorListener errorListener,
			OnEmergencyDetailListener listener, long objectId, long zoneId,
			int type, int way) {
		super(errorListener);
		this.listener = listener;
		this.objectId = objectId;
		this.zoneId = zoneId;
		this.type = type;
		this.way = way;
	}

	EmergencyDetailParser parser = new EmergencyDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getEmergencyDetail(parser.errCode, parser.emergencyDetail);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new EmergencyDetailRequest(objectId, zoneId, type, way).make();
	}

}
