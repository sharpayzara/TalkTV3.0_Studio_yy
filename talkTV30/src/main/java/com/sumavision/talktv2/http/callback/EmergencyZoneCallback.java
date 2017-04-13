package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.EmergencyZoneParser;
import com.sumavision.talktv2.http.json.EmergencyZoneRequest;
import com.sumavision.talktv2.http.listener.OnEmergencyZoneListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public class EmergencyZoneCallback extends BaseCallback {

	private int zoneId;
	private OnEmergencyZoneListener listener;

	public EmergencyZoneCallback(OnHttpErrorListener errorListener,
			OnEmergencyZoneListener listener, int zoneId) {
		super(errorListener);
		this.listener = listener;
		this.zoneId = zoneId;
	}

	EmergencyZoneParser parser = new EmergencyZoneParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getEmergencyZone(parser.errCode, parser.emergencyZone);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new EmergencyZoneRequest(zoneId).make();
	}

}
