package com.sumavision.talktv.videoplayer.utils;

public interface NetConnectionListener {
	public void onNetEnd(int code, String msg, String method, boolean isLoadMore);
}