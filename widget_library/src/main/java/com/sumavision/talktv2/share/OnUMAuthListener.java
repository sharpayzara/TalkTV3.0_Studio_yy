package com.sumavision.talktv2.share;

import com.umeng.socialize.bean.SHARE_MEDIA;

public interface OnUMAuthListener {
	/**
	 * 友盟授权
	 * 
	 * @param platform
	 *            如SHARE_MEDIA.SINA.name()
	 * @param authSucc
	 */
	public void umAuthResult(SHARE_MEDIA platform, boolean authSucc,
			String openId, String token);
}
