package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.EventData;

public interface OnEventRoomListener {
	public void eventRoomResult(int errCode, int eventCount,ArrayList<EventData> evenList);
}
