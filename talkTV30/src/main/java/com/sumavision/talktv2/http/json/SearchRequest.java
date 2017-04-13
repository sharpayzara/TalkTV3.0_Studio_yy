package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 节目搜索请求类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchRequest extends BaseJsonRequest {

	private String searchKeyWords;
	private int first;
	private int count;

	public SearchRequest(String searchKeyWords, int first, int count) {
		super();
		this.searchKeyWords = searchKeyWords;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.searchProgram);
			jsonObject.put("version", JSONMessageType.APP_VERSION);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("keyword",
					StringUtils.AllStrTOUnicode(searchKeyWords));
			jsonObject.put("first", first);
			jsonObject.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
