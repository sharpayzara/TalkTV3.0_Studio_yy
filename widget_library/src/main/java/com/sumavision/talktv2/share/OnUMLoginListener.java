package com.sumavision.talktv2.share;

import com.sumavision.talktv2.bean.UmLoginInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;

public interface OnUMLoginListener {
	/**
	 * 
	 * @param loginSucc
	 * @param info
	 */
	public void onUmLogin(SHARE_MEDIA plat, boolean loginSucc,
			UmLoginInfo loginInfo);
}
