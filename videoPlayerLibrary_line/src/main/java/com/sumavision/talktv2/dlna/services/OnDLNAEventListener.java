package com.sumavision.talktv2.dlna.services;

public interface OnDLNAEventListener {
	public void eventSetUrl(boolean isOk);

	public void eventPlay(boolean isOk);

	public void eventPause(boolean isOk);

	public void eventSeek(boolean isOk);

	public void eventStop(boolean isOk);

	public void eventSetVolume(boolean isOk);

	public void eventGetVolume(boolean isOk, String vol);

	public void getPosition(boolean isOk, String absTime, String realTime,
			String trackDuration);
}
