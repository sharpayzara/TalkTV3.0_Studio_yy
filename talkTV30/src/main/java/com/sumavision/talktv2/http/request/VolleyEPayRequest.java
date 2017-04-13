package com.sumavision.talktv2.http.request;

import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.BaseStringReuqest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.epay.ExchangeVirtualCallback;
import com.sumavision.talktv2.http.callback.epay.OrderPaySearchCallback;
import com.sumavision.talktv2.http.callback.epay.PayHistoryCallback;
import com.sumavision.talktv2.http.callback.epay.PayRuleListCallback;
import com.sumavision.talktv2.http.callback.epay.SubmitOrderCallback;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.epay.OnExchangeVirtualListener;
import com.sumavision.talktv2.http.listener.epay.OnOrderPaySearchListener;
import com.sumavision.talktv2.http.listener.epay.OnPayHistoryListener;
import com.sumavision.talktv2.http.listener.epay.OnPayRuleListListener;
import com.sumavision.talktv2.http.listener.epay.OnSubmitOrderListener;

/**
 * 支付请求
 * 
 * @author suma-hpb
 * 
 */
public class VolleyEPayRequest extends VolleyRequest {

	/**
	 * 充值规则列表
	 * 
	 * @param errorListener
	 * @param mListener
	 */
	public static void getPayRuleList(OnHttpErrorListener errorListener,
			OnPayRuleListListener mListener) {
		BaseRequest request = new BaseRequest(url, new PayRuleListCallback(
				errorListener, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 提交订单
	 * 
	 * @param errorListener
	 * @param mPayRuleId
	 * @param mListener
	 */
	public static void submitOrder(OnHttpErrorListener errorListener,
			String mPayRuleId, OnSubmitOrderListener mListener) {
		BaseRequest request = new BaseRequest(url, new SubmitOrderCallback(
				errorListener, mPayRuleId, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 兑换虚拟货币
	 * 
	 * @param errorListener
	 * @param type
	 * @param diamondCount
	 * @param key
	 * @param mListener
	 */
	public static void exchangeVirtualMoney(OnHttpErrorListener errorListener,
			int type, int diamondCount, String key, String comm,
			OnExchangeVirtualListener mListener) {
		BaseStringReuqest request = new BaseStringReuqest(url,
				new ExchangeVirtualCallback(errorListener, type, diamondCount,
						key, comm, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 查询支付订单结果
	 * 
	 * @param errorListener
	 * @param orderId
	 * @param mListener
	 */
	public static void searchOrderPay(OnHttpErrorListener errorListener,
			long orderId, OnOrderPaySearchListener mListener) {
		BaseRequest request = new BaseRequest(url, new OrderPaySearchCallback(
				errorListener, orderId, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 充值记录
	 * 
	 * @param errorListener
	 * @param mListener
	 */
	public static void getPayHistory(OnHttpErrorListener errorListener,
			OnPayHistoryListener mListener) {
		BaseRequest request = new BaseRequest(url, new PayHistoryCallback(
				errorListener, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}
}
