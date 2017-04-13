package com.sumavision.talktv.videoplayer.utils;

import android.app.Activity;
import android.os.Message;
import android.util.Log;

import com.vbyte.p2p.P2PHandler;

public class StarP2PHandler extends P2PHandler {
	
	private Activity activity;
	
	public StarP2PHandler(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case p2p_ChannelInfoSuccess:
			// TODO something
			// activity.doSomething();
			break;
		case p2p_FileHeaderSuccess:
			break;
		default:
			super.handleMessage(msg);	
		}
	}
}
