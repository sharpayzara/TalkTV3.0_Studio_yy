package com.sumavision.talktv2.http.callback.eshop;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.eshop.CommitReceiverInfoParser;
import com.sumavision.talktv2.http.json.eshop.CommitReceiverInfoRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnCommitReceiverInfoListener;

/**
 * 实体物品领取信息提交回调
 * 
 * @author suma-hpb
 * 
 */
public class CommitReceiverInfoCallback extends BaseCallback {

	private ReceiverInfo info;
	private long userGoodsId;
	private OnCommitReceiverInfoListener mListener;

	/**
	 * @param errorListener
	 * @param info
	 * @param userGoodsId
	 * @param mListener
	 */
	public CommitReceiverInfoCallback(OnHttpErrorListener errorListener,
			ReceiverInfo info, long userGoodsId,
			OnCommitReceiverInfoListener mListener) {
		super(errorListener);
		this.info = info;
		this.userGoodsId = userGoodsId;
		this.mListener = mListener;
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.OnCommitReceiverInfo(mParser.errCode);
		}
	};

	CommitReceiverInfoParser mParser = new CommitReceiverInfoParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new CommitReceiverInfoRequest(userGoodsId, info).make();
	}
}
