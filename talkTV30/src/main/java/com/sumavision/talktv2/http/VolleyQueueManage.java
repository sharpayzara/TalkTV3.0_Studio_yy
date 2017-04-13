package com.sumavision.talktv2.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Helper class that is used to provide references to initialized
 * RequestQueue(s) and ImageLoader(s)
 * 
 * 
 */
public class VolleyQueueManage {
	private static RequestQueue mRequestQueue;

	private VolleyQueueManage() {
	}

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

}
