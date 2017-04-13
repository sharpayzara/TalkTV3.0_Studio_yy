package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 发现页请求
 * 
 * @author yanzhidan
 * 
 */
public class DiscoveryDetailRequest extends BaseJsonRequest
{

	@Override
	public JSONObject make()
	{
		JSONObject holder = new JSONObject();
		try
		{
			holder.put("method", Constants.discoveryDetail);
			holder.put("version", "3.0.7");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return holder;
	}

}
