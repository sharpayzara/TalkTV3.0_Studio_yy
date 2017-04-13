package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.VodProgramData;

public interface OnHotPlayListener {

	public void getHotProgramList(int errCod,
			ArrayList<VodProgramData> hotProgramList);
}
