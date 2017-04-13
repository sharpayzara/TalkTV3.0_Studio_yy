package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.EmergencyZoneData;

public interface OnEmergencyZoneListener {

	public void getEmergencyZone(int errCode,  EmergencyZoneData emergencyZone);
}
