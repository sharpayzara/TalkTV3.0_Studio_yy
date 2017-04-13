package com.sumavision.talktv2.http;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public class BaseErrorListener implements ErrorListener {

	OnHttpErrorListener errListener;

	public BaseErrorListener(OnHttpErrorListener errListener) {
		this.errListener = errListener;
	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		if (errListener != null) {
			errListener.onError(1);
		}

	}

}
