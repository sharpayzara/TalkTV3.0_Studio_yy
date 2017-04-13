package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.CommentData;

public interface OnCommentListListener {
	/**
	 * 节目评论列表
	 * 
	 * @param errCode
	 * @param commentCount
	 *            评论总数
	 * @param commenList
	 */
	public void commentList(int errCode, int commentCount,
			ArrayList<CommentData> commenList);
}
