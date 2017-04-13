package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 摇一摇类型选择
 * 
 * @author cx
 * 
 */
public class ShakeProgramTagRequest extends BaseJsonRequest
{

	@Override
	public JSONObject make()
	{
		JSONObject holder = new JSONObject();
		try
		{
			holder.put("method", "shakeProgramTag");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return holder;
	}

}
