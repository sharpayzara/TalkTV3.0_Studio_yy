package com.sumavision.talktv2.dlna.services;

import com.sumavision.itv.lib.dlna.service.DlnaService;

public class TalkTvDlnaService extends DlnaService {

	public static String SEARCH = "com.sumavision.talktv2.dlna.SEARCH_DEVICES";

	@Override
	protected void updateAction() {
		SEARCH_DEVICES = SEARCH;
	}

	@Override
	public void onDLNAGetResponse(String result) {

	}

}
