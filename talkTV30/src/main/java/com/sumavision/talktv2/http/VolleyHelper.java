package com.sumavision.talktv2.http;

import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sumavision.talktv2.http.eventbus.HttpErrorEvent;
import com.sumavision.talktv2.http.eventbus.HttpEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.Constants;

import de.greenrobot.event.EventBus;

/**
 * 新通信流程
 * 
 * @author suma-hpb
 * 
 */
public class VolleyHelper {
	/**
	 * 
	 * @param request
	 * @param listener
	 * @param errListener
	 */
	public static void post(JSONObject request, ParseListener listener,
			OnHttpErrorListener errListener) {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(
				BaseRequest.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.host,
				request, listener, new BaseErrorListener(errListener));
		if (request != null && !TextUtils.isEmpty(request.optString("method"))) {
			jsonRequest.setTag(request.optString("method"));
		}
		jsonRequest.setRetryPolicy(retryPolicy);
		VolleyQueueManage.getRequestQueue().add(jsonRequest);
	}

	/**
	 * EventBus+volley
	 * 
	 * @param request
	 * @param parser
	 */
	public static void post(JSONObject request, final BaseJsonParser parser) {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(
				BaseRequest.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Constants.host,
				request, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						parser.parse(response);
						EventBus.getDefault().post(new HttpEvent(parser));
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						EventBus.getDefault().post(new HttpErrorEvent());
					}
				});
		if (request != null && !TextUtils.isEmpty(request.optString("method"))) {
			jsonRequest.setTag(request.optString("method"));
		}
		jsonRequest.setRetryPolicy(retryPolicy);
		VolleyQueueManage.getRequestQueue().add(jsonRequest);
	}
	public static void post(JSONObject request, final BaseJsonParser parser,String url) {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(
				BaseRequest.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		JsonObjectRequest jsonRequest = new JsonObjectRequest(url,
				request, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						parser.parse(response);
						EventBus.getDefault().post(new HttpEvent(parser));
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						EventBus.getDefault().post(new HttpErrorEvent());
					}
				});
		if (request != null && !TextUtils.isEmpty(request.optString("method"))) {
			jsonRequest.setTag(request.optString("method"));
		}
		jsonRequest.setRetryPolicy(retryPolicy);
		VolleyQueueManage.getRequestQueue().add(jsonRequest);
	}

	public static void get(String url){
		StringRequest mStringRequest = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						System.out.println("请求结果:" + response);
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				System.out.println("请求错误:" + error.toString());
			}
		});
		VolleyQueueManage.getRequestQueue().add(mStringRequest);
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
