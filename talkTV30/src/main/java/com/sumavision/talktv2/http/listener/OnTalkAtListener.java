package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.CommentData;

public interface OnTalkAtListener {

	public void getTalkAtList(int errCode, int talkCount,
			ArrayList<CommentData> talkList);

}
