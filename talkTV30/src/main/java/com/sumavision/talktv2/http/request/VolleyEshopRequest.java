package com.sumavision.talktv2.http.request;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.BaseStringReuqest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.eshop.CommitReceiverInfoCallback;
import com.sumavision.talktv2.http.callback.eshop.EntityGoodDetailCallback;
import com.sumavision.talktv2.http.callback.eshop.ExchangeGoodCallback;
import com.sumavision.talktv2.http.callback.eshop.ExchangeKeyCallback;
import com.sumavision.talktv2.http.callback.eshop.PastDueGoodsDetailCallback;
import com.sumavision.talktv2.http.callback.eshop.RecommendGoodsListCallback;
import com.sumavision.talktv2.http.callback.eshop.UserGoodsCallback;
import com.sumavision.talktv2.http.callback.eshop.VirtualGoodDetailCallback;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnCommitReceiverInfoListener;
import com.sumavision.talktv2.http.listener.eshop.OnEntityGoodDetailListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeGoodsListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeKeyListener;
import com.sumavision.talktv2.http.listener.eshop.OnPastDueGoodsDetailListener;
import com.sumavision.talktv2.http.listener.eshop.OnRecommendGoodsListListener;
import com.sumavision.talktv2.http.listener.eshop.OnUserGoodsListener;
import com.sumavision.talktv2.http.listener.eshop.OnVirtualGoodDetailListener;

/**
 * 商城相关请求:商城、兑换、用户礼品
 * 
 * @author suma-hpb
 * 
 */
public class VolleyEshopRequest extends VolleyRequest {


	/**
	 * 兑换物品秘钥
	 * 
	 * @param errorListener
	 * @param listener
	 */
	public static void GetExchangeKey(OnHttpErrorListener errorListener,
			OnExchangeKeyListener listener) {
		BaseRequest request = new BaseRequest(url, new ExchangeKeyCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 物品兑换
	 * 
	 * @param errorListener
	 * @param mExchangeGood
	 * @param key
	 *            秘钥
	 * @param mListener
	 */
	public static void exchageGood(OnHttpErrorListener errorListener,
			ExchangeGood mExchangeGood, String key,
			OnExchangeGoodsListener mListener) {
		BaseStringReuqest request = new BaseStringReuqest(url,
				new ExchangeGoodCallback(errorListener, mExchangeGood, key,
						mListener));
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 过期物品详情
	 * 
	 * @param errorListener
	 * @param userGoodsId
	 * @param mListener
	 */
	public static void pastDueGoodsDetail(OnHttpErrorListener errorListener,
			long userGoodsId, OnPastDueGoodsDetailListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new PastDueGoodsDetailCallback(errorListener, userGoodsId,
						mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 虚拟物品详情
	 * 
	 * @param errorListener
	 * @param userGoodsId
	 * @param mListener
	 */
	public static void virtualGoodDetail(OnHttpErrorListener errorListener,
			ExchangeGood goods, OnVirtualGoodDetailListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new VirtualGoodDetailCallback(errorListener, goods, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 实体物品详情
	 * 
	 * @param errorListener
	 * @param goods
	 * @param mListener
	 */
	public static void entityGoodDetail(OnHttpErrorListener errorListener,
			ExchangeGood goods, OnEntityGoodDetailListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new EntityGoodDetailCallback(errorListener, goods, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 提交实体礼品领取信息
	 * 
	 * @param errorListener
	 * @param info
	 * @param userGoodsId
	 * @param mListener
	 */
	public static void CommitReceiverInfo(OnHttpErrorListener errorListener,
			ReceiverInfo info, long userGoodsId,
			OnCommitReceiverInfoListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new CommitReceiverInfoCallback(errorListener, info,
						userGoodsId, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 用户礼品列表
	 * 
	 * @param errorListener
	 * @param mListener
	 */
	public static void getUserGoods(OnHttpErrorListener errorListener,
			OnUserGoodsListener mListener) {
		BaseRequest request = new BaseRequest(url, new UserGoodsCallback(
				errorListener, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 推荐物品列表-商城
	 * 
	 * @param errorListener
	 * @param mListener
	 */
	public static void GetRecommendGoodsList(OnHttpErrorListener errorListener,
			OnRecommendGoodsListListener mListener) {
		BaseRequest request = new BaseRequest(url,
				new RecommendGoodsListCallback(errorListener, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

}
