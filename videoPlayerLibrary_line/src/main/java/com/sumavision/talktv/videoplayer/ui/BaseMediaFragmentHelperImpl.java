package com.sumavision.talktv.videoplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 基本操控帮助类
 * 
 * @author suma-hpb
 * 
 */
public class BaseMediaFragmentHelperImpl extends MediaFragmentHelper {

	@Override
	public Fragment getFragment(Context context, Bundle bundle) {
		return Fragment.instantiate(context,
				MediaControllerFragment.class.getName(), bundle);
	}

	@Override
	public void feedbackReport(String programId, String channelId, String subId,
			String titleName, String problem) {

	}

	@Override
	public void playCount(Context context,int programId,int subId, int channelId) {
		
	}

	@Override
	public void disablePlayVod(int subid, int type) {
		
	}
}
