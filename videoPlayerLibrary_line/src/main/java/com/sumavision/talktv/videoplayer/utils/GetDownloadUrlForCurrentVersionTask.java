package com.sumavision.talktv.videoplayer.utils;

import com.baidu.cyberplayer.utils.VersionManager;
import com.baidu.cyberplayer.utils.VersionManager.CPU_TYPE;
import com.sumavision.talktv.videoplayer.task.BaseTask;

public class GetDownloadUrlForCurrentVersionTask extends BaseTask<CpuData>
{

    public GetDownloadUrlForCurrentVersionTask(NetConnectionListener listener, String method)
    {
	super(listener, method);
    }

    @Override
    public String generateRequest(Object... params)
    {
	return null;
    }

    @Override
    public String parse(CpuData data, String s)
    {
	return null;
    }

    @Override
    protected Integer doInBackground(Object... params)
    {
	String ak = "qSN3scX2ct8hpaD2zd6VLxlT";
	String sk = "8Tn38lA9IgpmXqMo";
	VersionManager.RequestDownloadUrlForCurrentVersionCallback callBack = (VersionManager.RequestDownloadUrlForCurrentVersionCallback) params[2];
	CPU_TYPE type = (CPU_TYPE) params[1];
	VersionManager.getInstance().getDownloadUrlForCurrentVersion(0, type, ak, sk, callBack);
	return 0;
    }
}
