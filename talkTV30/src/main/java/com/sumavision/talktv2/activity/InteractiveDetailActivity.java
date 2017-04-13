package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.InteractiveUtils;
import com.sumavision.talktv2.adapter.FragmentAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.fragment.InteractiveCommentFragment;
import com.sumavision.talktv2.fragment.InteractiveGuessFragment;
import com.sumavision.talktv2.fragment.InteractiveProgramFragment;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnSendCommentListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveActivityDetailListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveSupportListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.TimeUtils;
import com.sumavision.talktv2.utils.WebpUtils;

/**
 * 互动详情
 * 
 * @author suma-hpb
 * @version
 * @description
 */
@SuppressLint("NewApi")
public class InteractiveDetailActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener,
		OnInteractiveActivityDetailListener, OnSendCommentListener,
		OnInteractiveSupportListener {

	private InteractiveActivity mInteractiveActivity = new InteractiveActivity();

	private FrameLayout headerView;

	private TextView commentTxt, guessTxt, videoTxt;

	private RelativeLayout sendCommentLayout;

	private EditText commentContent;

	private Button sendComment;

	private ViewPager mViewPager;

	private RelativeLayout errLayout;

	private TextView errTxt;

	// private ProgressBar mprogressBar;

	private InteractiveUtils interactiveHandler;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_guessing_detail);
		getSupportActionBar().setTitle(R.string.guessing_detail);
		getExtras();
		initView();
		interactiveHandler = new InteractiveUtils(this,
				mInteractiveActivity.id, mInteractiveActivity.interactStatus,
				true);
		if (mInteractiveActivity.type == InteractiveActivity.TYPE_SPORTS) {
			View sportView = getLayoutInflater().inflate(
					R.layout.header_guessing_detail_sport, null);
			headerView.addView(sportView);
			initSportHeader();
		} else {
			View programView = getLayoutInflater().inflate(
					R.layout.header_guessing_detail_program, null);
			headerView.addView(programView);
			initProgramHeader();
		}
		interactiveHandler.showInteractionLayout();
		getDetailTask();
		VolleyUserRequest.queryInfo(this, this, null);
	}

	private void getDetailTask() {
		interHandler.sendEmptyMessage(REFRESH_RATIO);
	}

	@Override
	protected void onResume() {
		super.onResume();
		acquireWakeLock();
		interactiveHandler.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseWakeLock();
		interactiveHandler.onPause();
	}

	private void getExtras() {
		mInteractiveActivity = (InteractiveActivity) getIntent()
				.getSerializableExtra("interactive");
	}

	private TextView mProgramIntro;

	private TextView mProgramTime;

	private ImageView mProgromPic;

	private void initProgramHeader() {
		mProgramIntro = (TextView) headerView.findViewById(R.id.intro);
		mProgramTime = (TextView) headerView.findViewById(R.id.time);
		mProgromPic = (ImageView) headerView.findViewById(R.id.pic);
	}

	private TextView scoreTxt;

	private TextView leftSupportTxt;

	private TextView rightSupportTxt;

	private ImageView rightSupportImg, leftSupportImg;

	private SeekBar ratioBar;

	private void initSportHeader() {
		((TextView) headerView.findViewById(R.id.title))
				.setText(mInteractiveActivity.title);
		((TextView) headerView.findViewById(R.id.left_team_name))
				.setText(mInteractiveActivity.leftTeamName);
		leftSupportImg = (ImageView) headerView.findViewById(R.id.img_left_sup);
		rightSupportImg = (ImageView) headerView
				.findViewById(R.id.img_right_sup);
		headerView.findViewById(R.id.join_left).setOnClickListener(this);
		headerView.findViewById(R.id.join_right).setOnClickListener(this);
		loadImage((ImageView) headerView.findViewById(R.id.left_team_logo),
				Constants.picUrlFor + mInteractiveActivity.leftTeamLogo
						+ Constants.PIC_SMALL, R.drawable.aadefault);
		scoreTxt = (TextView) headerView.findViewById(R.id.score);
		scoreTxt.setText(mInteractiveActivity.leftPoint + ":"
				+ mInteractiveActivity.rightPoint);
		((TextView) headerView.findViewById(R.id.right_team_name))
				.setText(mInteractiveActivity.rightTeamName);
		loadImage((ImageView) headerView.findViewById(R.id.right_team_logo),
				Constants.picUrlFor + mInteractiveActivity.rightTeamLogo
						+ Constants.PIC_SMALL, R.drawable.aadefault);
		ratioBar = ((SeekBar) headerView.findViewById(R.id.ratio));
		ratioBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		float progress = mInteractiveActivity.leftSupportCount
				* 1.0f
				/ (mInteractiveActivity.leftSupportCount + mInteractiveActivity.rightSupportCount);
		progress *= 100;
		ratioBar.setProgress((int) progress);
		leftSupportTxt = ((TextView) headerView.findViewById(R.id.left_support));
		leftSupportTxt.setText(String
				.valueOf(mInteractiveActivity.leftSupportCount));
		rightSupportTxt = ((TextView) headerView
				.findViewById(R.id.right_support));
		rightSupportTxt.setText(String
				.valueOf(mInteractiveActivity.rightSupportCount));
	}

	private InteractiveCommentFragment commentFragment;

	private InteractiveGuessFragment guessFragment;

	private InteractiveProgramFragment programFragment;

	private void initView() {
		ImageView interView = (ImageView) findViewById(R.id.inter_pic);
		interView.setImageBitmap(WebpUtils.getAssetBitmap(this,
				"webp/interaction_pic_wait.webp"));
		headerView = (FrameLayout) findViewById(R.id.header);
		commentTxt = (TextView) findViewById(R.id.comment);
		guessTxt = (TextView) findViewById(R.id.guessing);
		videoTxt = (TextView) findViewById(R.id.video);
		sendCommentLayout = (RelativeLayout) findViewById(R.id.send_comment_layout);
		commentContent = (EditText) findViewById(R.id.comment_content);
		sendComment = (Button) findViewById(R.id.send);

		sendComment.setOnClickListener(this);
		commentTxt.setOnClickListener(this);
		guessTxt.setOnClickListener(this);
		videoTxt.setOnClickListener(this);

		errLayout = (RelativeLayout) findViewById(R.id.errLayout);
		errTxt = (TextView) findViewById(R.id.err_text);
		errTxt.setOnClickListener(this);
		// mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
		errLayout.setVisibility(View.GONE);
		initViewPager();
	}

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.viewPage);
		commentFragment = InteractiveCommentFragment
				.newInstance(mInteractiveActivity.topicId);
		guessFragment = InteractiveGuessFragment
				.newInstance(mInteractiveActivity.id);
		programFragment = InteractiveProgramFragment.newInstance(
				mInteractiveActivity.id, mInteractiveActivity.interactStatus);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(commentFragment);
		fragments.add(guessFragment);
		fragments.add(programFragment);
		mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),
				fragments));
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.guessing_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_history) {
			startActivity(new Intent(this, MyGuessingActivity.class));
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send:
			String input = commentContent.getText().toString().trim();
			if (input.isEmpty()) {
				Toast.makeText(this, R.string.no_input_tip, Toast.LENGTH_SHORT)
						.show();
				return;
			}
			CommentData.current().content = input;
			sendComment();
			break;
		case R.id.comment:
			if (mViewPager.getCurrentItem() != 0) {
				mViewPager.setCurrentItem(0);
			}
			break;
		case R.id.guessing:
			if (mViewPager.getCurrentItem() != 1) {
				mViewPager.setCurrentItem(1);
			}
			break;
		case R.id.video:
			if (mViewPager.getCurrentItem() != 2) {
				mViewPager.setCurrentItem(2);
			}
			break;
		case R.id.comment_content:
			break;
		case R.id.join_left:
		case R.id.join_right:
			if (mInteractiveActivity.userSupport == 0) {
				support = (v.getId() == R.id.join_left) ? 1 : 2;
				joinSupport(support);
			} else {
				String tip = "";
				if (mInteractiveActivity.userSupport == 1) {
					tip = mInteractiveActivity.leftTeamName;
				} else {
					tip = mInteractiveActivity.rightTeamName;
				}
				Toast.makeText(this,
						getString(R.string.interactive_support, tip),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.err_text:
			getDetailTask();
			break;
		default:
			break;
		}
	}

	private int support;

	private void joinSupport(int support) {
		errLayout.setVisibility(View.VISIBLE);
		VolleyInteractionRequest.joinInteractiveSupport(this,
				mInteractiveActivity.id, support, this);
	}

	private void getActivityDetail() {
		VolleyInteractionRequest.interactiveActivityDetail(this,
				mInteractiveActivity, this);
	}

	private void sendComment() {
		commentFragment.showLoadingLayout();
		VolleyCommentRequest.sendComment(this,
				String.valueOf(mInteractiveActivity.topicId), this);
	}

	private static final int REFRESH_RATIO = 2;

	private static final int REFRESH_TIME = 300000;

	private Handler interHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_RATIO:
				getActivityDetail();
				break;
			default:
				break;
			}

		};
	};

	public long delayInteractiveOver;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		interHandler.removeMessages(REFRESH_RATIO);
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
			commentFragment.loadData();
			commentTxt.setSelected(true);
			guessTxt.setSelected(false);
			videoTxt.setSelected(false);
			sendCommentLayout.setVisibility(View.VISIBLE);
		} else if (arg0 == 1) {
			commentTxt.setSelected(false);
			guessTxt.setSelected(true);
			videoTxt.setSelected(false);
			sendCommentLayout.setVisibility(View.GONE);
		} else if (arg0 == 2) {
			programFragment.loadData();
			commentTxt.setSelected(false);
			guessTxt.setSelected(false);
			videoTxt.setSelected(true);
			sendCommentLayout.setVisibility(View.GONE);
		}
	}

	public void refreshGuessList() {
		guessFragment.refresh();
	}

	private WakeLock mWakeLock;

	@SuppressWarnings("deprecation")
	private void acquireWakeLock() {
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "interactive");
			mWakeLock.acquire();
		}

	}

	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	public void sendCommentResult(int errCode) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		commentContent.setText("");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			Toast.makeText(this, "发表成功", Toast.LENGTH_SHORT).show();
			CommentData.current().content = "";
			commentFragment.refresh();
		} else {
			Toast.makeText(this, "发表失败", Toast.LENGTH_SHORT).show();
		}

	}

	private static final int SUPPORT_LEFT = 1;
	private static final int SUPPORT_RIGHT = 2;

	@Override
	public void onInteractiveActivityDetail(int errCode) {
		errLayout.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (mInteractiveActivity.type == InteractiveActivity.TYPE_SPORTS) {
				updateSportProgramInfo();
			} else {
				// 节目类
				mProgramIntro.setText(mInteractiveActivity.programIntro);
				mProgramTime.setText(mInteractiveActivity.showTime);
				loadImage(mProgromPic, mInteractiveActivity.programPhoto,
						R.drawable.aadefault);
			}
			if (mInteractiveActivity.interactStatus == InteractiveActivity.INTERACTION_STATUS_INTIME) {
				long end = TimeUtils
						.StringToMillSeconds(mInteractiveActivity.endTime);
				long now = TimeUtils
						.StringToMillSeconds(mInteractiveActivity.currentTime);
				if (end > now) {
					delayInteractiveOver = end - now;
				}
				if (delayInteractiveOver > 0) {
					interactiveHandler.startOverHandler(delayInteractiveOver);
				}
			}
		}
		interHandler.removeMessages(REFRESH_RATIO);
		if (mInteractiveActivity.type == InteractiveActivity.TYPE_SPORTS
				&& mInteractiveActivity.interactStatus == InteractiveActivity.INTERACTION_STATUS_INTIME) {
			interHandler.sendEmptyMessageDelayed(REFRESH_RATIO, REFRESH_TIME);
		}

	}

	private void updateSportProgramInfo() {
		if (mInteractiveActivity.userSupport == SUPPORT_LEFT) {
			leftSupportImg.setImageResource(R.drawable.agree_icon_choice);
		} else if (mInteractiveActivity.userSupport == SUPPORT_RIGHT) {
			rightSupportImg.setImageResource(R.drawable.agree_icon_choice);
		}
		// 刷新比分
		scoreTxt.setText(mInteractiveActivity.leftPoint + ":"
				+ mInteractiveActivity.rightPoint);
		leftSupportTxt.setText(String
				.valueOf(mInteractiveActivity.leftSupportCount));
		rightSupportTxt.setText(String
				.valueOf(mInteractiveActivity.rightSupportCount));
		float progress = mInteractiveActivity.leftSupportCount
				* 1.0f
				/ (mInteractiveActivity.leftSupportCount + mInteractiveActivity.rightSupportCount);
		progress *= 100;
		ratioBar.setProgress((int) progress);
	}

	@Override
	public void onInteractiveSupport(int errCode) {
		errLayout.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (support == SUPPORT_LEFT) {
				leftSupportImg.setImageResource(R.drawable.agree_icon_choice);
			} else if (mInteractiveActivity.userSupport == SUPPORT_RIGHT) {
				rightSupportImg.setImageResource(R.drawable.agree_icon_choice);
			}
			interHandler.removeMessages(REFRESH_RATIO);
			interHandler.sendEmptyMessage(REFRESH_RATIO);
			Toast.makeText(this, R.string.join_success, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, R.string.join_failed, Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.userInfo);
		VolleyHelper.cancelRequest(Constants.interactiveUserSupport);
		VolleyHelper.cancelRequest(Constants.interactiveActivityDetail);
		VolleyHelper.cancelRequest(Constants.talkAdd);
	}
}
