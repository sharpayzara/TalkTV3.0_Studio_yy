package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 专题类节目ui接口
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public interface OnSubjectProgramListListener {
	public void getSubjectProgramList(int errCode,
			ArrayList<VodProgramData> programList);
}
