package com.sumavision.talktv2.http.callback;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.PlayCountRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnPlayCountListener;
import com.sumavision.talktv2.utils.DialogUtil;

import org.json.JSONObject;

/**
 * 播放次数统计回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class PlayCountCallback extends BaseCallback {

	private int programId;
	private int channelId;
	private int subId;
	private OnPlayCountListener listener;
	private Context context;
	private String version;

	public PlayCountCallback(Context context, OnHttpErrorListener errorListener, int programId, int subId,
			int channelId,String version, OnPlayCountListener listener) {
		super(errorListener);
		this.programId = programId;
		this.channelId = channelId;
		this.subId = subId;
		this.listener = listener;
		this.context = context;
		this.version = version;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
//			if (parser.userInfo.point>0) {
//				DialogUtil.updateScoreToast("播放视频 +"+parser.userInfo.point+"积分");
//			}
		}
		if (listener != null) {
			listener.playCountResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new PlayCountRequest(context,programId, subId, channelId,version).make();
	}

}
