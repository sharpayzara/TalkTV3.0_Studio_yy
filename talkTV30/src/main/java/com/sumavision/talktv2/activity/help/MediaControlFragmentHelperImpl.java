package com.sumavision.talktv2.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv.videoplayer.ui.MediaFragmentHelper;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.FeedbackData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.fragment.VideoControllerFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.DisableVodParser;
import com.sumavision.talktv2.http.json.DisableVodRequest;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;

public class MediaControlFragmentHelperImpl extends MediaFragmentHelper {

	static {
		registerImpl(MediaControlFragmentHelperImpl.class);
	}

	@Override
	public Fragment getFragment(Context context, Bundle bundle) {
		return Fragment.instantiate(context,
				VideoControllerFragment.class.getName(), bundle);
	}

	@Override
	public void feedbackReport(String programId, String channelId, String subId,
			String titleName, String problem) {
		FeedbackData feedBack = new FeedbackData();
		feedBack.programId = Integer.parseInt(programId);
		feedBack.programSubId = TextUtils.isEmpty(subId) ? 0 : Integer
				.parseInt(subId);
		feedBack.channelId = Integer.parseInt(channelId);
		feedBack.programName = titleName;
		feedBack.content = problem;
		feedBack.source = CommentData.COMMENT_SOURCE;
		VolleyUserRequest.feedback(feedBack, null, null);
	}

	@Override
	public void playCount(Context context, int programId,int subId, int channelId) {
		VolleyProgramRequest.playCount(context,null, programId,subId,
				channelId,"", null);
	}

	
	@Override
	public void disablePlayVod(int subid, int type) {
		final DisableVodParser vparser = new DisableVodParser();
		VolleyHelper.post(new DisableVodRequest(subid,type).make(), new ParseListener(vparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (vparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					Log.e("PlayerActivity", "点播源过滤："+vparser.result);
				} else {
					Log.e("PlayerActivity", "点播源过滤错误："+vparser.errMsg);
				}
			}
		}, null);
	}

}
