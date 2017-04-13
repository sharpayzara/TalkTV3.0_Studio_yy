package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.CommentData;

public interface OnReplyListListener {

	public void getReplyList(int errCode, int replyCount,
			ArrayList<CommentData> commentList);
}
