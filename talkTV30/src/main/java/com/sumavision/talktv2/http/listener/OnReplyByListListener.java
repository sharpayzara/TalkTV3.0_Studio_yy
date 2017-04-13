package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.CommentData;

/**
 * 被回复列表
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnReplyByListListener {

	/**
	 * 被回复列表
	 * 
	 * @param errCode
	 * @param replyCount
	 * @param replyList
	 */
	public void getReplyByList(int errCode, int replyCount,
			ArrayList<CommentData> replyList);
}
