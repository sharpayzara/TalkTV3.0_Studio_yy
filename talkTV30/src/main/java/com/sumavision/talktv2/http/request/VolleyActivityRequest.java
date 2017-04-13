package com.sumavision.talktv2.http.request;

import android.content.Context;

import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.NewActivityCompleteCallback;
import com.sumavision.talktv2.http.callback.NewActivitygetBadgeCallback;
import com.sumavision.talktv2.http.callback.activities.ActivityListcallback;
import com.sumavision.talktv2.http.callback.activities.ActivityShakeCallback;
import com.sumavision.talktv2.http.callback.activities.FetchActivityGoodsCallback;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnNewActivityCompleteListener;
import com.sumavision.talktv2.http.listener.OnNewActivitygetBadgeListener;
import com.sumavision.talktv2.http.listener.activities.OnActivityListListener;
import com.sumavision.talktv2.http.listener.activities.OnActivityShakeListener;
import com.sumavision.talktv2.http.listener.activities.OnFetchActivityGoodsListener;

public class VolleyActivityRequest extends VolleyRequest {

	/**
	 * 活动列表
	 * 
	 * @param context
	 * @param listener
	 * @param errorListener
	 * @param first
	 * @param count
	 */
	public static void activityList(Context context,
			OnActivityListListener listener, OnHttpErrorListener errorListener,
			int first, int count) {
		BaseRequest request = new BaseRequest(url, new ActivityListcallback(
				errorListener, listener, context, first, count)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 
	 * @param listener
	 * @param errorListener
	 * @param context
	 * @param activityId
	 */
	public static void newActivityComplete(
			OnNewActivityCompleteListener listener,
			OnHttpErrorListener errorListener, Context context, long activityId) {
		BaseRequest request = new BaseRequest(url,
				new NewActivityCompleteCallback(errorListener, context,
						activityId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 
	 * @param listener
	 * @param errorListener
	 * @param context
	 * @param activityId
	 */
	public static void newActivityGetbadge(
			OnNewActivitygetBadgeListener listener,
			OnHttpErrorListener errorListener, Context context, long activityId) {
		BaseRequest request = new BaseRequest(url,
				new NewActivitygetBadgeCallback(errorListener, activityId,
						context, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 刮奖
	 * 
	 * @param listener
	 * @param errorListener
	 * @param context
	 * @param activityId
	 */
	public static void newActivityShake(OnActivityShakeListener listener,
			OnHttpErrorListener errorListener, Context context, int activityId) {
		BaseRequest request = new BaseRequest(url, new ActivityShakeCallback(
				errorListener, context, activityId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 活动奖品悄悄领取
	 * 
	 * @param errorListener
	 * @param goodsId
	 * @param activityId
	 * @param mListener
	 * 
	 */
	public static void fetchActivityGoods(OnHttpErrorListener errorListener,
			long goodsId, long activityId, String imei,
			OnFetchActivityGoodsListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new FetchActivityGoodsCallback(errorListener, goodsId,
						activityId, imei, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}
}
