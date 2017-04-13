package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.AwesomeAdapter;
import com.sumavision.talktv2.adapter.CommentAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnCommentDetailListener;
import com.sumavision.talktv2.http.listener.OnForwardListListener;
import com.sumavision.talktv2.http.listener.OnReplyListListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.Txt2Image;
import com.umeng.analytics.MobclickAgent;

/**
 * 评论详情页<br>
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentDetailActivity extends CommentBaseActivity implements
		OnClickListener, OnPageChangeListener, OnCommentDetailListener,
		OnForwardListListener, OnReplyListListener {
	private String programId;
	private TextView nameTextView;
	private ImageView headpicImageView;
	private TextView contentTextView;
	private RelativeLayout rootLayout;
	private TextView rootTextView;
	private ImageView rootPicImageView;
	private ImageView contentImageView;
	private ImageView bigpic;
	private RelativeLayout audioLayout;
	private ImageView audioBtn;
	private ProgressBar audioProgressBar;
	private RelativeLayout rootAudioLayout;
	private ImageView rootAudioBtn;
	private ProgressBar rootAudioProgressBar;
	private TextView from;
	private TextView timeText;

	private TextView replayCountView;
	private TextView zhuanfaCountView;

	private ViewPager viewPager;

	private Animation zoomIn;
	private Animation zoomOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_comment_detail);
		setContentView(R.layout.activity_comment_detail);
		programId = getIntent().getStringExtra("programId");
		int page = getIntent().getIntExtra("page", 0);
		if (null == programId)
			programId = "";
		initViews();
		viewPager.setCurrentItem(page);
		setListener();
		VolleyCommentRequest.getCommentDetail(this, this);
	}

	private void setListener() {

		findViewById(R.id.my_comment_layout).setOnClickListener(this);
		findViewById(R.id.headpic).setOnClickListener(this);
		replayCountView.setOnClickListener(this);
		zhuanfaCountView.setOnClickListener(this);
		contentImageView.setOnClickListener(this);
		rootPicImageView.setOnClickListener(this);
		bigpic.setOnClickListener(this);
		findViewById(R.id.llayout_reply).setOnClickListener(this);
		findViewById(R.id.llayout_forward).setOnClickListener(this);
	}

	private void initViews() {
		nameTextView = (TextView) findViewById(R.id.name);
		headpicImageView = (ImageView) findViewById(R.id.headpic);
		contentTextView = (TextView) findViewById(R.id.content);
		contentImageView = (ImageView) findViewById(R.id.pic);
		bigpic = (ImageView) findViewById(R.id.bigpic);
		rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
		rootTextView = (TextView) findViewById(R.id.root_content);
		rootPicImageView = (ImageView) findViewById(R.id.root_pic);
		from = (TextView) findViewById(R.id.from);
		timeText = (TextView) findViewById(R.id.time);
		audioLayout = (RelativeLayout) findViewById(R.id.cd_audio_layout);
		audioBtn = (ImageView) findViewById(R.id.cd_audio_pic);
		audioProgressBar = (ProgressBar) findViewById(R.id.cd_audio_progressBar);

		rootAudioLayout = (RelativeLayout) findViewById(R.id.root_audio_layout);
		rootAudioBtn = (ImageView) findViewById(R.id.root_audio_pic);
		rootAudioProgressBar = (ProgressBar) findViewById(R.id.root_audio_progressBar);

		replayCountView = (TextView) findViewById(R.id.reply_btn);
		replayCountView.setSelected(true);
		zhuanfaCountView = (TextView) findViewById(R.id.forward_btn);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		zoomIn = AnimationUtils.loadAnimation(this, R.anim.scalebig);
		zoomOut = AnimationUtils.loadAnimation(this, R.anim.scalesmall);
		initViewPager();
	}

	private void setMyCommentInfo() {
		CommentData temp = CommentData.current();
		if (temp.userName != null) {
			nameTextView.setText(temp.userName);
		}
		if (temp.source != null) {
			from.setText(temp.source);
		}
		if (temp.commentTime != null) {
			timeText.setText(temp.commentTime);
		}
		if (temp.talkType != 4) {
			audioLayout.setVisibility(View.GONE);
			String content = temp.content;
			if (content != null) {
				contentTextView.setText(content);
				SpannableString contentString = Txt2Image.text2Emotion(
						CommentDetailActivity.this, content);
				contentTextView.setText(contentString);
			}

			if (temp.talkType == 1) {
				String picUrl = temp.contentURL;
				contentImageView.setTag(picUrl);
				loadImage(contentImageView, picUrl,
						R.drawable.list_headpic_default);
				contentImageView.setVisibility(View.VISIBLE);
			} else {
				contentImageView.setVisibility(View.GONE);
			}

			if (temp.hasRootTalk) {
				rootLayout.setVisibility(View.VISIBLE);
				String rootContent = temp.rootTalk.content;
				if (rootContent != null) {
					SpannableString contentString = Txt2Image.text2Emotion(
							CommentDetailActivity.this, rootContent);
					rootTextView.setText(contentString);
				}

				if (temp.rootTalk.talkType != 4) {
					rootAudioLayout.setVisibility(View.GONE);
					if (temp.rootTalk.talkType == 1) {
						String rootheadPicUrl = temp.rootTalk.contentURL;
						rootPicImageView.setTag(rootheadPicUrl);
						loadImage(rootPicImageView, rootheadPicUrl,
								R.drawable.list_headpic_default);
						rootPicImageView.setVisibility(View.VISIBLE);
					} else {
						rootPicImageView.setVisibility(View.GONE);
					}
				} else {
					rootTextView.setVisibility(View.GONE);
					rootPicImageView.setVisibility(View.GONE);
					rootAudioLayout.setVisibility(View.VISIBLE);
					rootAudioBtn.setOnClickListener(this);
				}
			} else {
				rootLayout.setVisibility(View.GONE);
			}
		} else {
			contentTextView.setVisibility(View.GONE);
			contentImageView.setVisibility(View.GONE);
			audioLayout.setVisibility(View.VISIBLE);
			audioBtn.setOnClickListener(this);
		}

		loadImage(headpicImageView, temp.userURL,
				R.drawable.list_headpic_default);
		Resources res = getResources();
		String reply = res.getString(R.string.comment_reply);
		replayCountView.setText(reply + temp.replyCount);
		String forward = res.getString(R.string.comment_zhuanfa);
		zhuanfaCountView.setText(forward + temp.forwardCount);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("CommentDetailActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("CommentDetailActivity");
		super.onPause();
	}

	private void initViewPager() {
		AwesomeAdapter awesomeAdapter;
		ArrayList<View> views;
		views = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.comment_detail_reply_page, null);
		replyErrTxt = (TextView) view.findViewById(R.id.err_text);
		replyProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		replyListView = (ListView) view.findViewById(R.id.listView);
		View detailView = inflater.inflate(
				R.layout.comment_detail_forward_page, null);
		forwardErrTxt = (TextView) detailView.findViewById(R.id.err_text);
		forwardProgressBar = (ProgressBar) detailView
				.findViewById(R.id.progressBar);
		forwardListView = (ListView) detailView.findViewById(R.id.listView);
		views.add(view);
		views.add(detailView);
		awesomeAdapter = new AwesomeAdapter(views);
		viewPager.setAdapter(awesomeAdapter);
		viewPager.setOnPageChangeListener(this);

	}

	private TextView replyErrTxt;
	private ProgressBar replyProgressBar;
	private ListView replyListView;

	private ArrayList<CommentData> replyList = new ArrayList<CommentData>();

	private void updateReplyView(ArrayList<CommentData> commentList) {
		List<CommentData> list;
		if (commentList == null) {
			list = CommentData.current().reply;
		} else {
			list = commentList;
		}
		if ((list != null) && (list.size() > 0)) {
			replyList = (ArrayList<CommentData>) list;
			replyErrTxt.setVisibility(View.GONE);
			CommentAdapter adapter = new CommentAdapter(this, replyList);
			replyListView.setAdapter(adapter);
		} else {
			replyErrTxt.setVisibility(View.VISIBLE);
			if (CommentData.current().replyCount > 0)
				replyErrTxt.setText("加载失败，请重试");
			else
				replyErrTxt.setText("还没有回复");
		}
		Resources res = getResources();
		String reply = res.getString(R.string.comment_reply);
		String replayCount = String.valueOf(CommentData.current().replyCount);
		replayCountView.setText(reply + replayCount);
		replyProgressBar.setVisibility(View.GONE);

	}

	private TextView forwardErrTxt;
	private ProgressBar forwardProgressBar;
	private ListView forwardListView;
	private ArrayList<CommentData> forwardList = new ArrayList<CommentData>();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.reply_btn:
			viewPager.setCurrentItem(0);
			break;
		case R.id.forward_btn:
			viewPager.setCurrentItem(1);
			break;
		case R.id.llayout_forward:
			openSendCommentActivity(false);
			break;
		case R.id.llayout_reply:
			openSendCommentActivity(true);
			break;
		case R.id.cd_audio_pic:
			playVoice(CommentData.current().audioURL, audioBtn,
					audioProgressBar);
			break;
		case R.id.root_audio_pic:
			playVoice(CommentData.current().rootTalk.audioURL, rootAudioBtn,
					rootAudioProgressBar);
			break;
		case R.id.my_comment_layout:
			try {
				if ((CommentData.current().objectId != 0)
						&& (!programId.equals(CommentData.current().objectId
								+ "")))
					openProgramActivity();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			break;
		case R.id.pic:
			zoomInPic(CommentData.current().contentURL);
			break;
		case R.id.bigpic:
			bigpic.startAnimation(zoomOut);
			bigpic.setVisibility(View.GONE);
			break;
		case R.id.root_pic:
			zoomInPic(CommentData.current().rootTalk.contentURL);
			break;
		default:
			break;
		}
	}

	private void zoomInPic(String url) {
		String temp = url.replace("s.jpg", "b.jpg");
		loadImage(bigpic, temp, R.drawable.list_headpic_default);
		bigpic.setVisibility(View.VISIBLE);
		bigpic.startAnimation(zoomIn);
	}

	private void openProgramActivity() {
		Intent intent = new Intent(this, PlayerActivity.class);
		long programId = Long.valueOf(CommentData.current().objectId + "");
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		CommentData.current().forward = (null);
		CommentData.current().reply = (null);
		super.onDestroy();
	}

	private void openSendCommentActivity(boolean reply) {
		if (!reply) {
			CommentData.current().content = contentTextView.getText()
					.toString();
		}
		int fromWhere = reply ? SendCommentActivity.REPLY
				: SendCommentActivity.FORWARD;
		Intent intent = new Intent(this, SendCommentActivity.class);
		intent.putExtra("fromWhere", fromWhere);
		startActivityForResult(intent, fromWhere);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			replayCountView.setSelected(true);
			zhuanfaCountView.setSelected(false);
			if (replyList.size() == 0) {
				VolleyCommentRequest.getReplyList(0, 20, this, this);
			}
		} else if (arg0 == 1) {
			replayCountView.setSelected(false);
			zhuanfaCountView.setSelected(true);
			if (forwardList.size() == 0) {
				VolleyCommentRequest.getForwardList(this, 0, 20, this);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SendCommentActivity.FORWARD:
				updateForwardView();
				break;
			case SendCommentActivity.REPLY:
//				updateReplyView(null);
				VolleyCommentRequest.getCommentDetail(this, this);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateForwardView() {
		ArrayList<CommentData> temp = (ArrayList<CommentData>) CommentData
				.current().forward;
		if ((temp != null) && (temp.size() > 0)) {
			forwardList = temp;
			forwardErrTxt.setVisibility(View.GONE);
			CommentAdapter adapter = new CommentAdapter(this, forwardList);
			forwardListView.setAdapter(adapter);
		} else {
			forwardErrTxt.setVisibility(View.VISIBLE);
			if (CommentData.current().forwardCount > 0)
				forwardErrTxt.setText("加载失败，请重试");
			else
				forwardErrTxt.setText("还没有转发");

		}
		Resources res = getResources();
		String reply = res.getString(R.string.comment_zhuanfa);
		String replayCount = String.valueOf(CommentData.current().forwardCount);
		zhuanfaCountView.setText(reply + replayCount);
		forwardProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void commenetDetailResult(int errCode, int replyCount,
			ArrayList<CommentData> replyList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			setMyCommentInfo();
			updateReplyView(replyList);
		} else {
			Toast.makeText(CommentDetailActivity.this, "网络繁忙，请稍后重试",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void getReplyList(int errCode, int replyCount,
			ArrayList<CommentData> commentList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			updateReplyView(commentList);
		} else {
			replyProgressBar.setVisibility(View.GONE);
		}

	}

	@Override
	public void forwardListResult(int errCode) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			updateForwardView();
		} else {
			forwardProgressBar.setVisibility(View.GONE);
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.talkDetail);
		VolleyHelper.cancelRequest(Constants.replyList);
		VolleyHelper.cancelRequest(Constants.talkForwardList);
	}

}
