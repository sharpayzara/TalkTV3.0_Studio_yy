package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.EmergencyDetailData;
import com.sumavision.talktv2.bean.EmergencyDetailPlayData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.AddRemindEvent;
import com.sumavision.talktv2.http.listener.OnEmergencyDetailListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 急诊室节目详情
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class EmergencyDetailActivity extends BaseActivity implements
		OnClickListener, OnEmergencyDetailListener {
	private TextView rimDetailTxt;
	private TextView rimTitleTxt;
	private TextView rimTitleBelowTxt;
	private LinearLayout rimBottomFrame;
	private TextView rimFrameTxt;
	private ImageView rimPic;
	private EmergencyDetailData emergencyDetailData;

	private RelativeLayout playLayout;
	private ImageView playBtn;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_emergency_detail);
		getExtra();
		if (type == 1) {
			getSupportActionBar().setTitle("周边新闻");
		} else {
			getSupportActionBar().setTitle("视频花絮");
		}
		initView();
		getEmergencyDetailData();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("EmergencyDetailActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("EmergencyDetailActivity");
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEvent(AddRemindEvent event) {
		getEmergencyDetailData();
	}

	ScrollView scrollView;

	private void initView() {
		scrollView = (ScrollView) findViewById(R.id.scroll);
		scrollView.setVisibility(View.GONE);
		rimBottomFrame = (LinearLayout) findViewById(R.id.emergency_rim_bottom_frame);
		rimBottomFrame.setOnClickListener(this);
		rimBottomFrame.setVisibility(View.GONE);
		rimFrameTxt = (TextView) findViewById(R.id.emergency_rim_frame_txt);
		rimTitleTxt = (TextView) findViewById(R.id.emergency_rim_top_txt);
		rimTitleBelowTxt = (TextView) findViewById(R.id.emergency_rim_below_txt);
		rimDetailTxt = (TextView) findViewById(R.id.emergency_rim_detail_txt);
		rimDetailTxt.setLineSpacing(3.5f, 1.5f);

		String text = "";
		rimDetailTxt.setText(text);
		rimPic = (ImageView) findViewById(R.id.rim_pic_iv);
		initLoadingLayout();

		playLayout = (RelativeLayout) findViewById(R.id.play_layout);
		playBtn = (ImageView) findViewById(R.id.play_btn);
		playBtn.setOnClickListener(this);
		if (type == 1) {
			playLayout.setVisibility(View.GONE);
		}
	}

	private int type;
	private int way;
	private long objectId;
	private long zoneId;

	private void getExtra() {
		Intent intent = getIntent();
		if (intent.hasExtra("objectId"))
			objectId = intent.getLongExtra("objectId", 0L);
		if (intent.hasExtra("zoneId"))
			zoneId = intent.getLongExtra("zoneId", 0L);
		if (intent.hasExtra("type"))
			type = intent.getIntExtra("type", 0);
		if (intent.hasExtra("way"))
			way = intent.getIntExtra("way", 0);
	}

	/***
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	private void updateEmergencyList() {
		TextPaint tp = rimTitleTxt.getPaint();
		tp.setFakeBoldText(true);
		rimTitleTxt.setText(emergencyDetailData.emDetailname);
		rimTitleBelowTxt.setText(emergencyDetailData.emDetailTime);
		String detaiTxt = "";
		if (emergencyDetailData.emDetailPicList.size() > 0) {
			detaiTxt = ToDBC(emergencyDetailData.emDetailPicList.get(0).emDetailPicContent);
		}
		rimDetailTxt.setText(detaiTxt);
		rimFrameTxt.setText(" 评论  ( " + emergencyDetailData.emDetailTalkCount
				+ " )");
		String detailPic = "";
		if (emergencyDetailData.emDetailPicList != null && emergencyDetailData.emDetailPicList.size() > 0) {
			detailPic = emergencyDetailData.emDetailPicList.get(0).emDetailPicPic;
		}
		if (StringUtils.isNotEmpty(detailPic)) {
			String picUrl = "";
			if (!detailPic.contains(".jpg")) {
				picUrl = Constants.picUrlFor + detailPic + "b.jpg";
			} else {
				picUrl = Constants.picUrlFor
						+ emergencyDetailData.emDetailPicList.get(0).emDetailPicPic;
			}
			loadImage(rimPic, picUrl, R.drawable.emergency_pic_bg_detail);
		}

	}

	public void getEmergencyDetailData() {
		showLoadingLayout();
		VolleyProgramRequest.emergencyDetail(this, this, objectId, zoneId,
				type, way);
	}

	@Override
	protected void reloadData() {
		getEmergencyDetailData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emergency_rim_bottom_frame:
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("topicId", emergencyDetailData.emDetailTopicId);
			intent.putExtra("programName", "急诊室故事");
			startActivity(intent);
			break;
		case R.id.play_btn:
			openPlayerActivity();
			break;
		default:
			break;
		}

	}

	private String picUrl = "";

	private void openPlayerActivity() {
		EmergencyDetailPlayData list = emergencyDetailData.emDetaiPlayList
				.get(0);
		String videoPath = list.emPlayVideo;
		if (!list.emPlayPic.contains(".jpg")) {
			picUrl = Constants.picUrlFor + list.emPlayPic + "s.jpg";
		}
		// 本地源，直接本地播放
		if (!TextUtils.isEmpty(videoPath)) {
			Intent intent = new Intent();
			intent = new Intent(this, WebAvoidPicActivity.class);
			setIntentExtra(intent, videoPath, "", 2,
					emergencyDetailData.emDetailname);
			startActivity(intent);
		} else if (!TextUtils.isEmpty(list.emPlayUrl)) {
			Intent intent = getIntent();
			intent.setClass(this, WebAvoidActivity.class);
			setIntentExtra(intent, list.emPlayUrl, list.emPlayUrl, 2,
					emergencyDetailData.emDetailname);
			startActivity(intent);
		}
	}

	private void setIntentExtra(Intent intent, String videoPath, String url,
			int playType, String title) {
		intent.putExtra("programPic", picUrl);
		intent.putExtra("url", url);
		intent.putExtra("path", videoPath);
		intent.putExtra("id",
				String.valueOf(emergencyDetailData.emDetailProgramId));
		if (videoPath.contains("sdcard"))
			intent.putExtra("playType", 3);
		else
			intent.putExtra("playType", playType);
		intent.putExtra("title", title);
		intent.putExtra("hideFav", true);
	}

	@Override
	public void getEmergencyDetail(int errCode, EmergencyDetailData eDetaildata) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			emergencyDetailData = eDetaildata;
			rimBottomFrame.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.VISIBLE);
			hideLoadingLayout();
			updateEmergencyList();
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.newsDetail);
	}
}
