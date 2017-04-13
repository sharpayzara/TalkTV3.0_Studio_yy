package com.sumavision.talktv2.http.json.eshop;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.utils.Constants;

/**
 * 商城限时兑换
 * 
 * @author suma-hpb
 * 
 */
public class GoodsLimitDetailParser extends BaseJsonParser {

	public ExchangeGood goods;

	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		goods = new ExchangeGood();
		ArrayList<ExchangeGood> contentList = new ArrayList<ExchangeGood>();
		errCode = jsonObject.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
		errMsg = jsonObject.optString("msg");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {

			JSONObject content = jsonObject.optJSONObject("content");
			goods.goodsTicketCount = content.optInt("goodsTicketCount");
			goods.userGoodsId = content.optLong("userGoodsId");
			if (content != null) {
				try {
					JSONArray detail = content.getJSONArray("titleArray");
					for (int i = 0; i < detail.length(); i++) {
						JSONObject obj = detail.getJSONObject(i);
						ExchangeGood info = new ExchangeGood();
						info.title = obj.optString("title");
						info.content = obj.optString("content");
						if (info.title.equals("所需碎片")){
							goods.totalPieceCount = obj.optInt("totalCount");
							goods.hasPieceCount = obj.optInt("hasCount");
						} else {
							contentList.add(info);
						}
					}
					goods.contentList = contentList;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			JSONObject hotgoodsObj = content.optJSONObject("hotGoods");
			goods.hotGoodsId = hotgoodsObj.optLong("id");
			goods.id = hotgoodsObj.optInt("goodsId");
			goods.type = hotgoodsObj.optInt("goodsType");
			goods.name = hotgoodsObj.optString("name");
			goods.point = hotgoodsObj.optInt("point");
			goods.intro = hotgoodsObj.optString("intro");
			goods.count = hotgoodsObj.optInt("remainingQty");
			goods.startTime = hotgoodsObj.optString("startTime");
			goods.currentTime = hotgoodsObj.optString("currentTime");
			goods.endTime = hotgoodsObj.optString("endTime");
			ArrayList<String> picList = new ArrayList<String>();
			JSONArray picArr = hotgoodsObj.optJSONArray("pic");
			if (picArr != null && picArr.length() > 0) {
				int len = picArr.length();
				for (int index = 0; index < len; index++) {
					JSONObject picObj = picArr.optJSONObject(index);
					StringBuffer picBuf = new StringBuffer(Constants.picUrlFor);
					picBuf.append(picObj.optString("picPath"));
					picBuf.append(Constants.PIC_BIG);
					picList.add(picBuf.toString());
				}
			}
			goods.picList = picList;

		}

	}

}
