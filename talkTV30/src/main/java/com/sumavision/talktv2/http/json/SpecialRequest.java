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
public class SpecialRequest extends BaseJsonRequest {
	private int id;
	private int offset;
	private int pageCount;

	public SpecialRequest(int id, int offset, int pageCount) {
		super();
		this.id = id;
		this.offset = offset;
		this.pageCount = pageCount;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.subColumnList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("columnId", id);
			holder.put("first", offset);
			holder.put("count", pageCount);
			
//			holder.put("method", "subColumnList");
//			holder.put("version", "2.2");
//			holder.put("client", "1");
//			holder.put("jsession", "asdfasdfas");
//			holder.put("columnId", 52);
//			holder.put("first", 0);
//			holder.put("count", 10);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
