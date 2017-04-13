package com.sumavision.talktv.videoplayer.data;

import com.baidu.cyberplayer.core.BVideoView;

public class BVideoError {

	public static String getStringError(int what) {
		String result = "";
		switch (what) {
		case BVideoView.MEDIA_ERROR_DISPLAY:
			result = "Surfaceview for playback not created or occur an error";
			break;
		case BVideoView.MEDIA_ERROR_EIO:
			result = "File or network related operation errors";
			break;
		case BVideoView.MEDIA_ERROR_INVALID_INPUTFILE:
			result = "the input video source is invalid";
			break;
		case BVideoView.MEDIA_ERROR_NO_INPUTFILE:
			result = "not set video source for playback";
			break;
		case BVideoView.MEDIA_ERROR_NO_SUPPORTED_CODEC:
			result = "codec not supportted the video source contains";
			break;
		default:
			result = String.valueOf(what);
			break;
		}
		return result;
	}
}
