package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.actionprovider.bean.CustomChooserTextView.OnItemChooserListener;
import com.sumavision.talktv2.activity.help.ProgramActionProvider;
import com.sumavision.talktv2.annotation.ViewInject;
import com.sumavision.talktv2.annotation.ViewUtils;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.FeedbackData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.fragment.ProgramBaseFragment;
import com.sumavision.talktv2.fragment.ProgramGridSubFragment;
import com.sumavision.talktv2.fragment.ProgramListSubFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.HttpEvent;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.CacheRequest;
import com.sumavision.talktv2.http.json.ChaseDeleteRequest;
import com.sumavision.talktv2.http.json.ProgramParser;
import com.sumavision.talktv2.http.json.ProgramRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShareRequest;
import com.sumavision.talktv2.http.listener.OnChaseProgramListener;
import com.sumavision.talktv2.http.listener.OnFeedbackListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author hpb
 * @description 节目页
 * 
 */
public class ProgramActivity extends BaseActivity implements
		OnHttpErrorListener, OnClickListener, OnChaseProgramListener,
		OnFeedbackListener, OnSharedPreferenceChangeListener {
	public ProgramData programData;

	ProgramListSubFragment listFragment;
	ProgramGridSubFragment gridFragment;
	@ViewInject(R.id.tv_comment)
	TextView commentTxt;
	@ViewInject(R.id.tv_share)
	TextView shareTxt;
	@ViewInject(R.id.tv_fav)
	TextView favTxt;
	@ViewInject(R.id.llayout_bottom)
	LinearLayout bottomLayout;

	SharedPreferences favSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("节目");
		setContentView(R.layout.activity_program);
		initLoadingLayout();
		ViewUtils.inject(this);
		// bottomLayout = (LinearLayout) findViewById(R.id.llayout_bottom);
		// commentTxt = (TextView) findViewById(R.id.tv_comment);
		// shareTxt = (TextView) findViewById(R.id.tv_share);
		// favTxt = (TextView) findViewById(R.id.tv_fav);
		shareTxt.setOnClickListener(this);
		favTxt.setOnClickListener(this);
		commentTxt.setOnClickListener(this);
		programData = new ProgramData();
		getExtra();
		getProgramInfo();
		problems = getResources().getStringArray(R.array.play_err);
		favSp = getSharedPreferences(PreferencesUtils.PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		favSp.registerOnSharedPreferenceChangeListener(this);
		EventBus.getDefault().register(this);
	}

	public void onEvent(HttpEvent e) {
		if (e.getParser() instanceof ProgramParser) {
			hideLoadingLayout();
			if (programParser.errCode == JSONMessageType.SERVER_CODE_OK) {
				bottomLayout.setVisibility(View.VISIBLE);
				getProgramHeader(JSONMessageType.SERVER_CODE_OK,
						programParser.program);
			} else {
				bottomLayout.setVisibility(View.GONE);
				showErrorLayout();
			}
		}
	}

	ProgramParser programParser = new ProgramParser();

	public void getProgramInfo() {
		showLoadingLayout();
		VolleyHelper.post(
				new ProgramRequest(programData.programId, this).make(),
				programParser);
	}
	
	public void onEvent(UserInfoEvent event) {
		getProgramInfo();
	}
	
	@Override
	protected void reloadData() {
		getProgramInfo();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		programData.programId = intent.getLongExtra("programId", 0L);
		getProgramInfo();
	};

	String[] problems;
	ProgramActionProvider pActionProvider;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.program, menu);
		pActionProvider = (ProgramActionProvider) menu.findItem(
				R.id.action_report).getActionProvider();
		pActionProvider.setOnItemChooserListener(new OnItemChooserListener() {

			@Override
			public void onItemChooser(int position) {
				if (programData.programId > 0) {
					FeedbackData feedBack = new FeedbackData();
					feedBack.programId = (int) programData.programId;
					feedBack.programName = programData.name;
					feedBack.content = problems[position];
					feedBack.source = CommentData.COMMENT_SOURCE;
					VolleyUserRequest.feedback(feedBack, ProgramActivity.this,
							null);
				}

			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("programActivity");
		super.onResume();
		boolean fav = PreferencesUtils.getBoolean(this, null,
				programData.programId + "_fav");

		/**
		 * 处理播放器页收藏流程
		 */
		if (fav) {
			programData.isChased = true;
			favTxt.setSelected(true);
			favTxt.setText(R.string.faved);
			PreferencesUtils.remove(this, null, programData.programId + "_fav");
		}
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("programActivity");
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_comment:
			if (programData.programId > 0) {
				openCommentActivity();
			}
			break;
		case R.id.tv_share:
			Intent shareIntent = new Intent(this, ShareActivity.class);
			int subId = 0;
			if (gridFragment != null) {
				subId = gridFragment.subid;
			}
			if (listFragment != null) {
				subId = listFragment.subid;
			}
			if (subId == 0 && programData.platformList != null && programData.platformList.size() > 0) {
				if (programData.platformList.get(0).jishuList != null
						&& programData.platformList.get(0).jishuList.size() > 0) {
					subId = programData.platformList.get(0).jishuList.get(0).id;
				}
			}
			shareIntent.putExtra("id", subId);
			shareIntent.putExtra("programName", programData.name);
			shareIntent.putExtra("activityPic", programData.pic);
			startActivityForResult(shareIntent, SHARE);
			break;
		case R.id.tv_fav:
			if (programData.programId > 0) {
				onFavClick();
			}
			break;

		default:
			break;
		}

	}

	private void onFavClick() {
		if (UserNow.current().userID != 0) {
			if (!programData.isChased) {
				VolleyUserRequest.chaseProgram((int) programData.programId,
						this, this);
			} else {
				programData.isChased = false;
				ResultParser parser = new ResultParser();
				VolleyHelper.post(new ChaseDeleteRequest(""
						+ programData.programId).make(), new ParseListener(
						parser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						onChaseDeleteResult(parser.errCode, parser.errMsg);
					}
				}, this);
			}
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, LOGIN);
		}
	}

	private void onChaseDeleteResult(int errCode, String msg) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			programData.isChased = false;
			setFavStatus();
			DialogUtil.alertToast(this, "取消成功");
			int c = UserNow.current().chaseCount - 1;
			if (c >= 0) {
				UserNow.current().chaseCount = c;
				SharedPreferences sp = getSharedPreferences("userInfo", 0);
				Editor spEd = sp.edit();
				spEd.putInt("chaseCount", c);
				spEd.commit();
			} else {
				UserNow.current().chaseCount = 0;
			}

		} else {
			DialogUtil.alertToast(this, msg);
		}

	}

	@Override
	public void chaseResult(int errCode) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			programData.isChased = true;
			setFavStatus();
			StringBuffer itemKey = new StringBuffer(UserNow.current().userID);
			itemKey.append("_").append(programData.programId);
			// PreferencesUtils.putInt(this, Constants.SP_FAV,
			// itemKey.toString(), value)
			DialogUtil.alertToast(this, getString(R.string.zhuijuu));
		} else {
			DialogUtil.alertToast(this, getString(R.string.chase_failed));
		}

	}

	protected void openCommentActivity() {
		Intent intent = new Intent(this, CommentActivity.class);
		intent.putExtra("programId", programData.programId);
		intent.putExtra("topicId", programData.topicId);
		intent.putExtra("cpId", programData.cpId);
		intent.putExtra("programName", TextUtils.isEmpty(programData.name) ? ""
				: programData.name);
		startActivity(intent);
	}

	private void getExtra() {
		Intent intent = getIntent();
		programData.programId = intent.getLongExtra("programId", 0L);
		programData.cpId = intent.getLongExtra("cpId", 0L);
		programData.topicId = intent.getLongExtra("topicId", 0L);
		programData.pType = intent.getIntExtra("type", 0);
		if (intent.hasExtra("fromNotification")) {
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			int notificationId = (int) programData.programId;
			manager.cancel(notificationId);
		}

	}

	public void play(ArrayList<JiShuData> jiShuDatas, int arg2) {
		gridFragment.play(jiShuDatas, arg2);
	}

	public int getSelectedPlayItemPos() {
		return gridFragment.getSelectedPlayItemPos();
	}

	public void setFavStatus() {
		if (programData.isChased) {
			favTxt.setSelected(true);
			favTxt.setText(R.string.faved);
		} else {
			favTxt.setSelected(false);
			favTxt.setText(R.string.fav);
		}
		PreferencesUtils.putBoolean(this, null, UserNow.current().userID + "_"
				+ programData.programId + "_fav", programData.isChased);
	}

	public void getProgramHeader(int errCode, ProgramData program) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_OK://
			if (isFinishing()) {
				return;
			}
			programData = program;
			setFavStatus();
			Fragment oldFragment = getSupportFragmentManager()
					.findFragmentByTag("content");
			if (oldFragment != null) {
				getSupportFragmentManager().beginTransaction()
						.remove(oldFragment).commitAllowingStateLoss();
			}
			switch (programData.pType) {
			case ProgramData.TYPE_TV:
			case ProgramData.TYPE_DONGMAN:
				gridFragment = ProgramGridSubFragment.newInstance();
				gridFragment.getArguments().putInt("skipWeb",
						programParser.skipWeb);
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_content, gridFragment, "content")
						.commitAllowingStateLoss();
				break;
			default:
				listFragment = ProgramListSubFragment.newInstance();
				listFragment.getArguments().putInt("skipWeb",
						programParser.skipWeb);
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_content, listFragment, "content")
						.commitAllowingStateLoss();
				break;
			}

			break;
		default:
			break;
		}
		// finish();
	}

	private static final int LOGIN = 1;
	private static final int SHARE = 2;
	public boolean loginLoadInfo;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (UserNow.current().userID > 0) {
			if (requestCode == LOGIN) {
				loginLoadInfo = true;
				getProgramInfo();
			} else if (requestCode == SHARE && resultCode == RESULT_OK) {
				shareRequest();
			} else if (requestCode == ProgramBaseFragment.CAHCE
					&& resultCode == RESULT_OK) {
				cacheRequest();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	ResultParser shareParser = new ResultParser();

	private void shareRequest() {
		VolleyHelper.post(new ShareRequest(ShareRequest.TYPE_PROGRAM,
				(int) programData.programId).make(), new ParseListener(
				shareParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
//				if (shareParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//					if (parser.userInfo.point > 0) {
//						UserNow.current().setTotalPoint(
//								parser.userInfo.totalPoint);
//					}
//				}
			}
		}, null);
	}

	ResultParser cacheParser = new ResultParser();

	private void cacheRequest() {
		VolleyHelper.post(new CacheRequest().make(), new ParseListener(
				cacheParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
//				if (cacheParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//					if (parser.userInfo.point > 0) {
//						UserNow.current().setTotalPoint(
//								parser.userInfo.totalPoint);
//					}
//				}
			}
		}, null);
	}

	@Override
	public void onError(int code) {
		showErrorLayout();
	}

	@Override
	public void feedbackResult(int errCode) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			Toast.makeText(this, R.string.feedback_succeed, Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.programDetail);
		VolleyHelper.cancelRequest(Constants.feedbackAdd);
		VolleyHelper.cancelRequest(Constants.chaseAdd);
		VolleyHelper.cancelRequest(Constants.chaseDelete);
		VolleyHelper.cancelRequest(Constants.shareAnything);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(UserNow.current().userID + "_" + programData.programId
				+ "_fav")) {
			programData.isChased = PreferencesUtils.getBoolean(this, null,
					UserNow.current().userID + "_" + programData.programId
							+ "_fav");
			setFavStatus();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		favSp.unregisterOnSharedPreferenceChangeListener(this);
	}
}
