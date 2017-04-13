package com.sumavision.talktv2.http.json;

import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.GoodsPiece;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 摇一摇解析
 * 
 * @author cx
 * @version
 * @description
 */
public class MyDebrisParser extends BaseJsonParser {
	public ArrayList<GoodsPiece> pieces = new ArrayList<>();
	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				pieces.clear();
				JSONObject content = jsonObject.getJSONObject("content");
				if (content != null){
					JSONArray array = content.optJSONArray("debris");
					if (array != null && array.length()>0){
						for (int i=0; i<array.length(); i++){
							GoodsPiece gp = new GoodsPiece();
							JSONObject temp = array.optJSONObject(i);
							gp.goodsId = temp.optLong("goodsId");
							gp.goodsName = temp.optString("goodsName");
							gp.hasCount = temp.optInt("hasCount");
							gp.pic = temp.optString("pic");
							gp.text = temp.optString("text");
							gp.totalCount = temp.optInt("totalCount");
							gp.hotGoodsId = temp.optInt("hotGoodsId");
							pieces.add(gp);
						}
					}
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
