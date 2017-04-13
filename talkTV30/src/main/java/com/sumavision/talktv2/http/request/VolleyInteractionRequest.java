package com.sumavision.talktv2.http.request;

import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.interactive.GuessProgramListCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveActivityDetailCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveActivityListCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveCyclopediaCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveGuessDetailCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveGuessJoinCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveGuessListCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveSupportCallback;
import com.sumavision.talktv2.http.callback.interactive.InteractiveUserGuessListCallback;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnGuessProgramListListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveActivityDetailListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveActivityListListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveCyclopediaListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessDetailListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessJoinListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessListListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveSupportListener;
import com.sumavision.talktv2.http.listener.interactive.OnUserGuessListListener;

/**
 * 互动http请求
 * 
 * @author suma-hpb
 * 
 */
public class VolleyInteractionRequest extends VolleyRequest {


	/**
	 * 竞猜详情
	 * 
	 * @param errorListener
	 * @param mListener
	 * @param mListener
	 * @param guessId
	 */
	public static void guessDetail(OnHttpErrorListener errorListener,
			OnInteractiveGuessDetailListener mListener, int guessId) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveGuessDetailCallback(errorListener, mListener,
						guessId)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 参与竞猜
	 * 
	 * @param errorListener
	 * @param mListener
	 * @param guessId
	 * @param optionId
	 * @param bet
	 */
	public static void joinGuess(OnHttpErrorListener errorListener,
			OnInteractiveGuessJoinListener mListener, int guessId,
			int optionId, long bet) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveGuessJoinCallback(errorListener, mListener,
						guessId, optionId, bet)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 用户竞猜列表
	 * 
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param mListener
	 */
	public static void userGuessList(OnHttpErrorListener errorListener,
			int first, int count, OnUserGuessListListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveUserGuessListCallback(errorListener, first,
						count, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 常规正在进行竞猜列表
	 * 
	 * @param errorListener
	 * @param mListener
	 * @param count
	 * @param first
	 * @param activityId
	 */
	public static void guessingList(OnHttpErrorListener errorListener,
			OnInteractiveGuessListListener mListener, int first, int count,
			int activityId) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveGuessListCallback(errorListener, activityId,
						first, count, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 竞猜节目列表
	 * 
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param activityId
	 * @param mListener
	 */
	public static void guessProgramList(OnHttpErrorListener errorListener,
			int first, int count, int activityId,
			OnGuessProgramListListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new GuessProgramListCallback(errorListener, count, first,
						activityId, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 百科关键字列表
	 * 
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param mListener
	 */
	public static void cyclopediaList(OnHttpErrorListener errorListener,
			int first, int count, OnInteractiveCyclopediaListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveCyclopediaCallback(errorListener, first, count,
						mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 互动列表
	 * 
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param mListener
	 */
	public static void interactiveActivityList(
			OnHttpErrorListener errorListener, int first, int count,
			OnInteractiveActivityListListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveActivityListCallback(errorListener, first,
						count, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 互动活动详情信息
	 * 
	 * @param errorListener
	 * @param mInteractiveActivity
	 *            待填充活动信息
	 * @param mListener
	 */
	public static void interactiveActivityDetail(
			OnHttpErrorListener errorListener,
			InteractiveActivity mInteractiveActivity,
			OnInteractiveActivityDetailListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveActivityDetailCallback(errorListener,
						mInteractiveActivity, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 互动支持参与
	 * 
	 * @param errorListener
	 * @param activityId
	 * @param support
	 * @param mListener
	 */
	public static void joinInteractiveSupport(OnHttpErrorListener errorListener,
			int activityId, int support, OnInteractiveSupportListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new InteractiveSupportCallback(errorListener, activityId,
						support, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}
}
