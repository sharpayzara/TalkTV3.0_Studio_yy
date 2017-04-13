package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 发现页请求
 * 
 * @author yanzhidan
 * 
 */
public class DiscoveryRequest extends BaseJsonRequest
{

	@Override
	public JSONObject make()
	{
		JSONObject holder = new JSONObject();
		try
		{
			holder.put("method", Constants.discoveryDetail);
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			if (UserNow.current().userID > 0){
				holder.put("userId",UserNow.current().userID);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return holder;
	}

}
