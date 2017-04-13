package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.ProgramDetailInfoData;

public interface OnProgramDetailListener {

	public void getProgramDetail(int errCode, ProgramDetailInfoData programDetail);
}
