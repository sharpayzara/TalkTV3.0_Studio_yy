package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.ProgramData;

public interface OnProgramHeaderListener {

	public void getProgramHeader(int errCode, ProgramData program);
}
