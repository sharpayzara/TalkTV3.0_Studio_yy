package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.CommentData;

/**
 * 评论详情ui
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnCommentDetailListener {
	/**
	 * 评论详情
	 * 
	 * @param errCode
	 * @param replyCount
	 * @param replyList
	 *            回复列表
	 */
	public void commenetDetailResult(int errCode, int replyCount,
			ArrayList<CommentData> replyList);
}
