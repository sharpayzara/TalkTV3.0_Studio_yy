package com.sumavision.talktv.videoplayer.utils;

import com.sumavision.talktv.videoplayer.task.BaseTask;

public class SoDownLoadTask extends BaseTask<CpuData>
{

    public SoDownLoadTask(NetConnectionListener listener, String method)
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
	String url = (String) params[0];
	String path = (String) params[1];
	String fileName = (String) params[2];
	HttpDownloader httpDownloader = new HttpDownloader();

	int lrc = httpDownloader.downFile(url, path, fileName);
	return lrc;
    }

}
