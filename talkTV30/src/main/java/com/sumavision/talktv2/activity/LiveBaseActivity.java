package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.activity.MainWebPlayActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.utils.Constants;

import java.util.ArrayList;

/**
 * 带直播地址列表选项基类
 * 
 * @author suma-hpb
 * 
 */
public class LiveBaseActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initNetLiveLayout() {
	}

	public void liveThroughNet(ArrayList<NetPlayData> netPlayDatas,
			int programId, int channelId, ChannelData channel) {
		if (netPlayDatas.size() == 0) {
			Toast.makeText(this, "暂无可用视频源", Toast.LENGTH_SHORT).show();
			return;
		}
		NetPlayData temp = null;
		ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
		for (NetPlayData play : netPlayDatas) {
			if (!TextUtils.isEmpty(play.channelIdStr) || !TextUtils.isEmpty(play.videoPath) || !TextUtils.isEmpty(play.url)) {
				playList.add(play);
				if (null == temp) {
					temp = play;
				}
			}
		}
		if (null == temp) {
			temp = netPlayDatas.get(0);
		}
		String url = temp.url;
		String videoPath = temp.videoPath;
		String channelName = temp.channelName;// 分享内容
		if (TextUtils.isEmpty(channelName) && channel != null) {
			channelName = channel.channelName;
		}

		if (!TextUtils.isEmpty(temp.channelIdStr) || !TextUtils.isEmpty(videoPath) || !TextUtils.isEmpty(url)) {
			Intent intent = new Intent();
			intent.putExtra(PlayerActivity.INTENT_NEEDAVOID,
					Constants.NEEDAVOID_LIVE);
			if (!TextUtils.isEmpty(temp.webPage)){
				intent.putExtra("path", videoPath);
				intent.putExtra("url", videoPath);
				intent.setClass(this, WebAvoidPicActivity.class);
			} else if (!TextUtils.isEmpty(temp.showUrl) || !TextUtils.isEmpty(url)) {
				intent.putExtra("url", url);
				intent.setClass(this, WebAvoidActivity.class);
			} else {
				intent.putExtra("path", videoPath);
				intent.putExtra("url", videoPath);
				intent.setClass(this, WebAvoidPicActivity.class);
			}
			intent.putExtra("id", programId);
			intent.putExtra("channelId", channelId);
			intent.putExtra("toWeb",getIntent().getBooleanExtra("toWeb",false));
			intent.putExtra("p2pChannel", TextUtils.isEmpty(temp.channelIdStr)?"":temp.channelIdStr);
			intent.putExtra("playType", 1);
			intent.putExtra("title", TextUtils.isEmpty(channelName) ? "电视直播"
					: channelName);
			intent.putExtra("share_program",channel.shareProgramName);
			intent.putExtra("channelName", TextUtils.isEmpty(channelName) ? "电视直播"
					: channelName);
			Bundle bundle = new Bundle();
			bundle.putSerializable("NetPlayData", playList);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			openNetLiveActivity(temp, programId, channelId);
		}
	}

	private void openNetLiveActivity(NetPlayData data, int programId,
			int channelId) {
		Intent intent2 = new Intent(this, MainWebPlayActivity.class);
		intent2.putExtra("url", data.url);
		intent2.putExtra("videoPath", data.videoPath);
		intent2.putExtra("playType", 1);
		intent2.putExtra("title", data.channelName);
		intent2.putExtra("share_program",data.name);
		intent2.putExtra("toWeb",getIntent().getBooleanExtra("toWeb",false));
		intent2.putExtra("channelId", channelId);
		intent2.putExtra("id", programId);
		intent2.putExtra("p2pChannel",TextUtils.isEmpty(data.channelIdStr)?"":data.channelIdStr);
		startActivity(intent2);
	}
}
