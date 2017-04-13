package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.ChannelData;

public interface OnChannelDetailListener {

	/**
	 * 
	 * @param errCode
	 * @param channel
	 * @param playItemPos
	 *            直播节目单pos
	 */
	public void channelDetailResult(int errCode, String errmsg,
			ChannelData channel, int playItemPos);
}
