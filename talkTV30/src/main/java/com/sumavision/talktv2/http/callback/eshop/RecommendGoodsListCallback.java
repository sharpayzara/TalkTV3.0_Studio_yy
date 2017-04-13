package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.RecommendGoodsListParser;
import com.sumavision.talktv2.http.json.eshop.RecommendGoodsListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnRecommendGoodsListListener;

/**
 * 物品列表回调-商城
 * 
 * @author suma-hpb
 * 
 */
public class RecommendGoodsListCallback extends BaseCallback {

	private OnRecommendGoodsListListener mListener;

	/**
	 * @param errorListener
	 * @param mListener
	 */
	public RecommendGoodsListCallback(OnHttpErrorListener errorListener,
			OnRecommendGoodsListListener mListener) {
		super(errorListener);
		this.mListener = mListener;
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener
					.OnGetRecommendGoodsList(mParser.errCode, mParser.hotGoodsList);
		}

	}

	RecommendGoodsListParser mParser = new RecommendGoodsListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new RecommendGoodsListRequest().make();
	}
}
