package com.sumavision.talktv2.http.listener;

public interface OnHttpErrorListener {

	/**
	 * code=1；无网络
	 * 
	 * @param code
	 */
	public void onError(int code);
}
