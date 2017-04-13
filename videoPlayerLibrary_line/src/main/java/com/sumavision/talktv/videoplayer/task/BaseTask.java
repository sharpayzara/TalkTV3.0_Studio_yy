package com.sumavision.talktv.videoplayer.task;

import android.os.AsyncTask;

import com.sumavision.talktv.videoplayer.utils.NetConnectionListener;

public abstract class BaseTask<T> extends AsyncTask<Object, Integer, Integer> {

	protected NetConnectionListener listener;
	protected String method;
	protected boolean isLoadMore;
	protected String errMsg = null;

	public BaseTask(NetConnectionListener listener, String method) {
		this(listener, method, false);
	}

	public BaseTask(NetConnectionListener listener, String method,
			boolean isLoadMore) {
		this.listener = listener;
		this.method = method;
		this.isLoadMore = isLoadMore;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onNetEnd(result, errMsg, method, isLoadMore);
		}
	}

	public abstract String generateRequest(Object... params);

	public abstract String parse(T data, String s);

}
