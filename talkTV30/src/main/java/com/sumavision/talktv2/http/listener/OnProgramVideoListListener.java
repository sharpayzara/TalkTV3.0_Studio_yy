package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.ProgramVideoData;

public interface OnProgramVideoListListener {

	public void getProgramVideoList(int errCode, ProgramVideoData programVideo);
}
