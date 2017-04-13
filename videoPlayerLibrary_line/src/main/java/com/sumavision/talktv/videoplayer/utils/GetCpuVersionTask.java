package com.sumavision.talktv.videoplayer.utils;

import com.baidu.cyberplayer.utils.VersionManager;
import com.sumavision.talktv.videoplayer.task.BaseTask;

public class GetCpuVersionTask extends BaseTask<CpuData> {

	public GetCpuVersionTask(NetConnectionListener listener, String method) {
		super(listener, method);
	}

	@Override
	public String generateRequest(Object... params) {
		return null;
	}

	@Override
	public String parse(CpuData data, String s) {
		return null;
	}

	@Override
	protected Integer doInBackground(Object... params) {
		// final CpuData cpuData = (CpuData) params[0];
		String ak = "qSN3scX2ct8hpaD2zd6VLxlT";
		String sk = "8Tn38lA9IgpmXqMo";
		VersionManager.RequestCpuTypeAndFeatureCallback callBack = (VersionManager.RequestCpuTypeAndFeatureCallback) params[0];
		VersionManager.getInstance().getCurrentSystemCpuTypeAndFeature(5000,
				ak, sk, callBack);
		return 0;
	}

}
