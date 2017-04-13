package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 专题请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SpecialDetailRequest extends BaseJsonRequest {
	private int id;
	private int offset;
	private int pageCount;

	public SpecialDetailRequest(int id, int offset, int pageCount) {
		super();
		this.id = id;
		this.offset = offset;
		this.pageCount = pageCount;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.columnVideoList);
			holder.put("version", JSONMessageType.APP_VERSION_THREE);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("columnId", id);
			holder.put("first", offset);
			holder.put("count", pageCount);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
