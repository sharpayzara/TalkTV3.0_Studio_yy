package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.DiscoveryDetailParser;
import com.sumavision.talktv2.http.json.DiscoveryDetailRequest;
import com.sumavision.talktv2.http.listener.OnDiscoveryDetailListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 发现页回调
 * 
 * @author yanzhidan
 * 
 */
public class DiscoveryDetailCallback extends BaseCallback
{
	private DiscoveryDetailParser parser = new DiscoveryDetailParser();
	private OnDiscoveryDetailListener listener;
	public DiscoveryDetailCallback(OnHttpErrorListener errorListener,OnDiscoveryDetailListener listener)
	{
		super(errorListener);
		this.listener = listener;
	}

	@Override
	protected void onResponseDelegate()
	{
		if (listener != null)
		{
			listener.onDiscoveryDetail(parser.errCode, parser.datas);
		}
	}

	@Override
	public void parseNetworkRespose(JSONObject jsonObject)
	{
		parser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest()
	{
		return new DiscoveryDetailRequest().make();
	}

}
