package com.sumavision.talktv2.http.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.DiscoveryDetailCallback;
import com.sumavision.talktv2.http.callback.DownloadRecommendAppCallback;
import com.sumavision.talktv2.http.callback.HotSearchCallback;
import com.sumavision.talktv2.http.callback.NewVersionCallback;
import com.sumavision.talktv2.http.callback.SearchCallback;
import com.sumavision.talktv2.http.callback.SearchUserCallback;
import com.sumavision.talktv2.http.callback.WelcomeListCallback;
import com.sumavision.talktv2.http.listener.OnAppNewVersionListener;
import com.sumavision.talktv2.http.listener.OnDiscoveryDetailListener;
import com.sumavision.talktv2.http.listener.OnDownloadRecommendAppListener;
import com.sumavision.talktv2.http.listener.OnHotSearchListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSearchListener;
import com.sumavision.talktv2.http.listener.OnSearchUserListener;
import com.sumavision.talktv2.http.listener.OnWelcomeListListener;
import com.sumavision.talktv2.utils.Constants;

public class VolleyRequest {
	public static String url = Constants.host;

	/**
	 * 获取新版本信息
	 * 
	 * @param context
	 * @param listener
	 * @param errorListener
	 */
	public static BaseRequest getAppNewVersion(Context context,
			OnAppNewVersionListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new NewVersionCallback(
				errorListener, context, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
		return request;
	}

	/**
	 * 欢迎页
	 * 
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param listener
	 */
	public static void welcomeList(OnHttpErrorListener errorListener,
			int first, int count, OnWelcomeListListener listener) {
		BaseRequest request = new BaseRequest(url, new WelcomeListCallback(
				errorListener, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 热门搜索节目
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void hotSearch(OnHotSearchListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new HotSearchCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 节目搜索
	 * 
	 * @param listener
	 * @param errorListener
	 * @param searchKeyWords
	 * @param first
	 * @param count
	 */
	public static void search(OnSearchListener listener,
			OnHttpErrorListener errorListener, String searchKeyWords,
			int first, int count) {
		BaseRequest request = new BaseRequest(url, new SearchCallback(
				errorListener, searchKeyWords, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 用户搜索
	 * 
	 * @param listener
	 * @param first
	 * @param count
	 * @param name
	 * @param errorListener
	 */
	public static void searchUser(OnSearchUserListener listener, int first,
			int count, String name, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new SearchUserCallback(
				errorListener, first, count, name, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 下载推荐应用
	 * 
	 * @param errorListener
	 * @param appId
	 * @param mListener
	 */
	public static void DownloadRecommendApp(OnHttpErrorListener errorListener,
			long appId, OnDownloadRecommendAppListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new DownloadRecommendAppCallback(errorListener, appId,
						mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	public static void getDiscoveryDetail(OnHttpErrorListener errorListener,
			OnDiscoveryDetailListener listener) {
		BaseRequest request = new BaseRequest(url, new DiscoveryDetailCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	public static void cancelRequest(final String tag) {
		Log.i(VolleyRequest.class.getSimpleName(), "cancel request,tag=" + tag);
		VolleyQueueManage.getRequestQueue().cancelAll(
				new RequestQueue.RequestFilter() {
					@Override
					public boolean apply(Request<?> request) {
						boolean result = false;
						Object cancelTag = request.getTag();
						if (cancelTag instanceof String) {
							result = ((String) cancelTag).equals(tag);
						}
						return result;
					}
				});
	}
}
