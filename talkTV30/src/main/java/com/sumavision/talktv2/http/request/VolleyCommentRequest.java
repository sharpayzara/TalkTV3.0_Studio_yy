package com.sumavision.talktv2.http.request;

import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.CommentDetailCallback;
import com.sumavision.talktv2.http.callback.CommentListCallback;
import com.sumavision.talktv2.http.callback.ForwardListCallback;
import com.sumavision.talktv2.http.callback.ReplyByListCallback;
import com.sumavision.talktv2.http.callback.ReplyListCallback;
import com.sumavision.talktv2.http.callback.SendCommentCallback;
import com.sumavision.talktv2.http.callback.SendForwardCallback;
import com.sumavision.talktv2.http.callback.SendReplyCallback;
import com.sumavision.talktv2.http.callback.TalkAtCallback;
import com.sumavision.talktv2.http.callback.UserTalkListCallback;
import com.sumavision.talktv2.http.listener.OnCommentDetailListener;
import com.sumavision.talktv2.http.listener.OnCommentListListener;
import com.sumavision.talktv2.http.listener.OnForwardListListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnReplyByListListener;
import com.sumavision.talktv2.http.listener.OnReplyListListener;
import com.sumavision.talktv2.http.listener.OnSendCommentListener;
import com.sumavision.talktv2.http.listener.OnSendFowardListener;
import com.sumavision.talktv2.http.listener.OnSendReplyListener;
import com.sumavision.talktv2.http.listener.OnTalkAtListener;
import com.sumavision.talktv2.http.listener.OnUserTalkListListener;

public class VolleyCommentRequest extends VolleyRequest {

	/**
	 * 评论详情
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void getCommentDetail(OnCommentDetailListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new CommentDetailCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 评论列表--talkList
	 * 
	 * @param topicId
	 * @param cpId
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void getCommentList(int topicId, int cpId, int first,
			int count, OnCommentListListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new CommentListCallback(
				errorListener, topicId, cpId, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 被@评论列表
	 * 
	 * @param first
	 *            原othercachedata中offset
	 * @param count
	 *            原othercachedata中pagecount
	 * @param listener
	 * @param errorListener
	 */
	public static void talkAtList(int first, int count,
			OnTalkAtListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new TalkAtCallback(
				errorListener, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 用户评论列表--userTalkList
	 * 
	 * @param userId
	 * @param first
	 *            原othercachedata中offset
	 * @param count
	 *            原othercachedata中pagecount
	 * @param listener
	 * @param errorListener
	 */
	public static void userTalkList(int userId, int first, int count,
			OnUserTalkListListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new UserTalkListCallback(
				errorListener, userId, listener, first, count)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 回复列表
	 * 
	 * @param first
	 *            原othercachedata中offset
	 * @param count
	 *            原othercachedata中pagecount
	 * @param listener
	 * @param errorListener
	 */
	public static void getReplyList(int first, int count,
			OnReplyListListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ReplyListCallback(
				errorListener, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 被回复列表
	 * 
	 * @param userId
	 * @param first
	 *            原othercachedata中offset
	 * @param count
	 *            原othercachedata中pagecount
	 * @param listener
	 * @param errorListener
	 */
	public static void getReplyByList(int userId, int first, int count,
			OnReplyByListListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ReplyByListCallback(
				errorListener, userId, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 获取转发列表
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void getForwardList(OnForwardListListener listener,
			int first, int count, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ForwardListCallback(
				first, count, errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 发表评论
	 * 
	 * @param errorListener
	 * @param topicId
	 * @param listener
	 */
	public static void sendComment(OnHttpErrorListener errorListener,
			String topicId, OnSendCommentListener listener) {
		BaseRequest request = new BaseRequest(url, new SendCommentCallback(
				errorListener, topicId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 转发评论
	 * 
	 * @param errorListener
	 * @param listener
	 */
	public static void sendForward(OnHttpErrorListener errorListener,
			OnSendFowardListener listener) {
		BaseRequest request = new BaseRequest(url, new SendForwardCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 发表回复
	 * 
	 * @param errorListener
	 * @param listener
	 */
	public static void sendReply(OnHttpErrorListener errorListener,
			OnSendReplyListener listener) {
		BaseRequest request = new BaseRequest(url, new SendReplyCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}
}
