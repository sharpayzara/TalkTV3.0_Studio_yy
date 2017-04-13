package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.CommentData;

public interface OnUserTalkListListener {

	public void getUserTalkList(int errCode, int talkCount,
			ArrayList<CommentData> talkList,boolean isVip);
}
