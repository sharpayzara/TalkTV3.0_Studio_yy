package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @version 3.0
 * @description 片库节目页筛选点击确定后进行筛选
 * @changeLog
 */
public class FilterRequest extends BaseJsonRequest {
	private int programId;
	private int first;
	private int count;
	private String[] keywords;

	public FilterRequest(int programType, String[] keywords, int first,
			int count) {
		this.programId = programType;
		this.first = first;
		this.count = count;
		this.keywords = keywords;
	}

	@Override
	public JSONObject make() {
		setAllToNull();
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.vaultColumnsSearch);
			holder.put("version", JSONMessageType.APP_VERSION_THREE);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("programType", programId);
			holder.put("contentTypeKeyword", keywords[0]);
			holder.put("ageKeyword", keywords[1]);
			holder.put("areaKeyword", keywords[2]);
			holder.put("actorKeyword", keywords[3]);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			reset();
		}
		return holder;
	}

	private void setAllToNull() {
		if (keywords != null) {
			for (int i = 0; i < keywords.length; i++) {
				if (keywords[i].equals("全部")) {
					keywords[i] = "";
				}
			}
		}
	}
	
	private void reset() {
		if (keywords != null) {
			for (int i = 0; i < keywords.length; i++) {
				if (keywords[i].equals("")) {
					keywords[i] = "全部";
				}
			}
		}
	}
}
