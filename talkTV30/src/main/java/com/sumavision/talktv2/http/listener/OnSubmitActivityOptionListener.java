package com.sumavision.talktv2.http.listener;

import com.sumavision.talktv2.bean.PlayNewData;

public interface OnSubmitActivityOptionListener {
	public void submitActivityOptionResult(int errCode, PlayNewData playNewData);
}
