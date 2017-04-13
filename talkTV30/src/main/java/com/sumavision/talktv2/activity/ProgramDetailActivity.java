package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumavision.talktv.videoplayer.activity.DLNAControllActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.ProgramDetailChannelAdapter;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PhotoData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.ProgramDetailInfoData;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.bean.StarData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnAddRemindListener;
import com.sumavision.talktv2.http.listener.OnDeleteRemindListener;
import com.sumavision.talktv2.http.listener.OnProgramDetailListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 节目详情信息页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ProgramDetailActivity extends LiveBaseActivity implements
		OnClickListener, OnItemClickListener, OnProgramDetailListener,
		OnAddRemindListener, OnDeleteRemindListener {
	ProgramData programData;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		programData = new ProgramData();
		getExtra();
		getSupportActionBar().setTitle("详情");
		setContentView(R.layout.activity_program_detail);
		initViews();
		getProgramDetailInfo(programData, programData.programId);
	}

	private void getExtra() {
		Intent intent = getIntent();
		programData.programId = intent.getLongExtra("programId", 0L);
		programData.isChased = intent.getBooleanExtra("isChased", false);
		programData.topicId = intent.getLongExtra("topicId", 0L);
	}

	static final int comment_id = 1;
	static final int zhuiju_id = 2;

	private void openLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private ImageView headPic;
	private TextView introView;

	private ListView channelListView;
	private LinearLayout starGallery, titBitGallery;
	private TextView channelsTip, starTips, titBitTips;
	private ScrollView scollView;

	private void initViews() {
		initLoadingLayout();
		scollView = (ScrollView) findViewById(R.id.scollView);
		headPic = (ImageView) findViewById(R.id.head_pic);
		introView = (TextView) findViewById(R.id.detail);
		channelListView = (ListView) findViewById(R.id.pd_video_tvlist);
		starGallery = (LinearLayout) findViewById(R.id.starGallery);
		titBitGallery = (LinearLayout) findViewById(R.id.titbitGallery);
		channelsTip = (TextView) findViewById(R.id.pd_video_title);
		starTips = (TextView) findViewById(R.id.pd_detail_star);
		titBitTips = (TextView) findViewById(R.id.pd_detail_juzhao);
	}

	private void getProgramDetailInfo(ProgramData programData, long programId) {
		showLoadingLayout();
		VolleyProgramRequest.getProgramDetail(programId, this, this);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ProgramDetailActivity");
	};

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ProgramDetailActivity");
	};

	ProgramDetailChannelAdapter channelAdapter;
	boolean isIntroClose = true;

	@SuppressLint("InlinedApi")
	private void updateViews() {
		ProgramDetailInfoData programDetailInfoData = programData.programDetailInfoData;
		String programPath = programDetailInfoData.pic;
		if (programPath != null) {
			String picUrl = CommonUtils.getUrl(this, programPath);
			loadImage(headPic, picUrl, R.drawable.emergency_pic_bg_detail);
		}
		String intro = programDetailInfoData.intro;
		if (StringUtils.isNotEmpty(intro)) {
			introView.setText(intro);
			introView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isIntroClose) {
						introView.setMaxLines(100);
						introView.setEllipsize(null);
					} else {
						introView.setMaxLines(3);
						introView.setEllipsize(TextUtils.TruncateAt.END);
					}
					isIntroClose = !isIntroClose;
				}
			});
		} else {
			findViewById(R.id.intro).setVisibility(View.GONE);
		}
		ArrayList<ChannelData> channels = programDetailInfoData.channels;
		boolean liveModule = PreferencesUtils.getBoolean(this, null, "liveModule", true);
		if (channels != null && channels.size() != 0 && liveModule) {
			channelAdapter = new ProgramDetailChannelAdapter(this, channels);
			channelListView.setAdapter(channelAdapter);
			channelListView.setOnItemClickListener(this);
			int totalHeight = 0;
			for (int i = 0; i < channelAdapter.getCount(); i++) {
				View listItem = channelAdapter
						.getView(i, null, channelListView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, totalHeight
							+ 20
							+ CommonUtils.dip2px(this,
									10 * (channelAdapter.getCount() - 1)));
			channelListView.setLayoutParams(params);
		} else {
			channelsTip.setVisibility(View.GONE);
			channelListView.setVisibility(View.GONE);
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		ArrayList<StarData> stars = programDetailInfoData.stars;
		if (stars != null && stars.size() != 0) {
			for (int i = 0; i < stars.size(); i++) {
				View rowView = inflater.inflate(
						R.layout.programdetail_gallery_item, null);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.p_gallery_img_small);
				loadImage(imageView, stars.get(i).photoBig_V,
						R.drawable.list_star_default);
				imageView.setTag(i);
				imageView.setOnClickListener(this);
				starGallery.addView(rowView);
			}
		} else {
			// 无明星，隐藏布局
			starGallery.setVisibility(View.GONE);
			starTips.setVisibility(View.GONE);
		}
		ArrayList<PhotoData> urls = programDetailInfoData.photos;
		if (urls != null && urls.size() != 0) {
			for (int i = 0; i < urls.size(); i++) {
				View rowView = inflater.inflate(
						R.layout.programdetail_gallery_item, null);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.p_gallery_img_small);
				loadImage(imageView, urls.get(i).url,
						R.drawable.list_star_default);
				imageView.setTag(i);
				imageView.setOnClickListener(titOnClickListener);
				titBitGallery.addView(rowView);
			}
		} else {
			// 无剧照，隐藏布局
			titBitTips.setVisibility(View.GONE);
			titBitGallery.setVisibility(View.GONE);
		}
	}

	private void openStarActivity(int id) {
		MobclickAgent.onEvent(this, "mingxing");
		Intent intent = new Intent(this, StarDetailActivity.class);
		intent.putExtra("starId", id);
		startActivity(intent);
	}

	OnClickListener titOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			ArrayList<PhotoData> photos = programData.programDetailInfoData.photos;
			String url = photos.get(position).url;
			url = url.replace("s.", "b.");
			String[] temp = new String[photos.size()];
			for (int i = 0; i < photos.size(); ++i) {
				temp[i] = photos.get(i).url;
			}
			openShowImageActivity(url,temp,position);
		}
	};

	private void openShowImageActivity(String url, String[] pics, int pos) {
		MobclickAgent.onEvent(this, "juzhaohuaxu");
		Intent intent = new Intent(this, ShowImageActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("pics", pics);
		intent.putExtra("pos", pos);
		startActivity(intent);
	}

	@Override
	protected void reloadData() {
		getProgramDetailInfo(programData, programData.programId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dlna_resume:
			Intent intent = new Intent(this, DLNAControllActivity.class);
			intent.putExtra("isResume", true);
			startActivity(intent);
			break;
		case R.id.p_gallery_img_small:
			int position = (Integer) v.getTag();
			ArrayList<StarData> stars = programData.programDetailInfoData.stars;
			int starId = stars.get(position).stagerID;
			openStarActivity(starId);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ArrayList<ChannelData> lc = programData.programDetailInfoData.channels;
		if (lc.get(arg2).now.isPlaying == 1) {
			MobclickAgent.onEvent(ProgramDetailActivity.this, "xqnbofang");
			ShortChannelData shortChannelData = new ShortChannelData();
			shortChannelData.channelName = lc.get(arg2).channelName;
			shortChannelData.netPlayDatas = lc.get(arg2).netPlayDatas;
			liveThroughNet(shortChannelData.netPlayDatas, lc.get(arg2).now.id,
					Integer.parseInt(lc.get(arg2).channelId), null);
		} else {
			MobclickAgent.onEvent(ProgramDetailActivity.this, "xqnyuyue");
			int userId = UserNow.current().userID;
			if (userId == 0) {
				openLoginActivity();
			} else {
				remindPosition = arg2;
				if (lc.get(arg2).now.order == 0) {
					addRemind(userId, lc.get(arg2).now.id);
				} else {
					deleteRemind(userId, (int)lc.get(arg2).now.remindId);
				}
			}
		}
	}

	private int remindPosition;

	private void addRemind(int userId, int programId) {
		showLoadingLayout();
		VolleyUserRequest.addRemind(userId, programId, this, this);
	}

	private void deleteRemind(int userId, int cpId) {
		showLoadingLayout();
		VolleyUserRequest
				.deleteRemind(userId, String.valueOf(cpId), this, this);
	}

	private void onRemindAdded(int position, int remindId) {
		ArrayList<ChannelData> lc = programData.programDetailInfoData.channels;
		lc.get(position).now.order = 1;
		lc.get(position).now.remindId = remindId;
		channelAdapter.notifyDataSetChanged();
	}

	private void onRemindDeleted(int position) {
		ArrayList<ChannelData> lc = programData.programDetailInfoData.channels;
		lc.get(position).now.order = 0;
		channelAdapter.notifyDataSetChanged();
	}

	@Override
	public void getProgramDetail(int errCode,
			ProgramDetailInfoData programDetail) {
		if (JSONMessageType.SERVER_CODE_OK == errCode) {
			programData.programDetailInfoData = programDetail;
			scollView.setVisibility(View.VISIBLE);
			hideLoadingLayout();
			updateViews();
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void deleteRemindResult(int errCode) {
		hideLoadingLayout();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			DialogUtil.alertToast(getApplicationContext(), "取消预约失败");
			break;
		case JSONMessageType.SERVER_CODE_OK:
			DialogUtil.alertToast(getApplicationContext(), "取消预约成功");
			onRemindDeleted(remindPosition);
			remindPosition = -1;
			break;
		default:
			break;
		}

	}

	@Override
	public void addRemindResult(int errCode, String msg, int remindId) {
		hideLoadingLayout();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_OK:
			DialogUtil.alertToast(getApplicationContext(), "预约成功");
			onRemindAdded(remindPosition, remindId);
			remindPosition = -1;
			break;
		default:
			DialogUtil.alertToast(getApplicationContext(), msg);
			break;
		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.chaseAdd);
		VolleyHelper.cancelRequest(Constants.chaseDelete);
		VolleyHelper.cancelRequest(Constants.programDetail);
		VolleyHelper.cancelRequest(Constants.remindAdd);
		VolleyHelper.cancelRequest(Constants.remindDelete);
	}

}
