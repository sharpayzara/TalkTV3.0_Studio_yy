package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.User;

public interface OnOtherUserCommentListener {
	public void getCommentlist(int errCode, User uo);
}
