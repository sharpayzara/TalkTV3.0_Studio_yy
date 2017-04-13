package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.UserInfoQueryParser;
import com.sumavision.talktv2.http.json.UserInfoQueryRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnUserInfoQueryListener;

/**
 * 用户信息查询回调
 * 
 * @author suma-hpb
 * 
 */
public class UserInfoQueryCallback extends BaseCallback {

	private Context mContext;
	private OnUserInfoQueryListener mListener;

	/**
	 * @param errorListener
	 * @param mContext
	 * @param mListener
	 */
	public UserInfoQueryCallback(OnHttpErrorListener errorListener,
			Context mContext, OnUserInfoQueryListener mListener) {
		super(errorListener);
		this.mContext = mContext;
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new UserInfoQueryRequest().make();
	}

	UserInfoQueryParser mParser = new UserInfoQueryParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mParser.diamond > 0 && mParser.diamond != UserNow.current().diamond) {
			SharedPreferences sp = mContext.getSharedPreferences("userInfo", 0);
			Editor spEd = sp.edit();
			spEd.putInt("totalPoint", UserNow.current().totalPoint);
			spEd.putInt("diamond", UserNow.current().diamond);
			spEd.commit();
		}
		if (mListener != null) {
			mListener.onQueryUserInfo(mParser.errCode);
		}
	}
}
