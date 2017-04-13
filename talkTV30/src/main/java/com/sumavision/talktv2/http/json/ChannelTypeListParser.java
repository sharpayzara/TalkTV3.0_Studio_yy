package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ChannelType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

public class ChannelTypeListParser extends BaseJsonParser {

	public ArrayList<ChannelType> channelTypeList = new ArrayList<ChannelType>();

	private String umengValue;

	public ChannelTypeListParser(String umengValue) {
		this.umengValue = umengValue;
	}

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				if (content.has("channelType")) {
					JSONArray channelTypes = content.getJSONArray("channelType");
					int size = channelTypes.length();
					for (int i = 0; i < size; ++i) {
						ChannelType typeChannelData = new ChannelType();
						JSONObject channelType = channelTypes.getJSONObject(i);
						typeChannelData.channleTypeId = channelType.getInt("id");
						typeChannelData.channelTypeName = channelType.getString("name");
						if (typeChannelData.channelTypeName.equals("推荐")) {
							typeChannelData.type = 1;
						}
						if ("anzhuo".equalsIgnoreCase(umengValue) && "港澳台".equals(typeChannelData.channelTypeName)) {

						} else {
							channelTypeList.add(typeChannelData);
						}
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
