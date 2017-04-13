package com.sumavision.talktv2.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MailWithOtherAdapter;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.FeedbackAddRequest;
import com.sumavision.talktv2.http.json.FeedbackListParser;
import com.sumavision.talktv2.http.json.FeedbackListlRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnMailListListener;
import com.sumavision.talktv2.http.listener.OnSendMailListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.service.TvBaiduPushMessageReceiver;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * @description 用户反馈对话界面
 * @changeLog
 */

public class FeedbackMailActivity extends BaseActivity implements OnClickListener,
		OnSharedPreferenceChangeListener , PullToRefreshBase.OnRefreshListener {

	private EditText input;
	private PullToRefreshListView list;
	private MailWithOtherAdapter pmla;
	private Animation a;
	private String otherUserIconURL;
	private int otherUserId;
	StringBuffer keyBuf;
	SharedPreferences pushSp;
	RelativeLayout sendLayout;
	int count = 10;
	int feedbackType = 0;
	public String feedbackDesc = "";
	private RelativeLayout typeLayout;
	private TextView desc;
	private String[] types;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		getSupportActionBar().setTitle("有奖反馈");
		setContentView(R.layout.activity_feedback_mail);
		pushSp = getSharedPreferences(Constants.pushMessage,
				Context.MODE_PRIVATE);

		keyBuf = new StringBuffer(Constants.key_privateMsg);
		keyBuf.append("_").append(otherUserId).append("-")
				.append(UserNow.current().userID);
		if (PreferencesUtils.getBoolean(this, Constants.pushMessage,
				keyBuf.toString())) {
			PreferencesUtils.putBoolean(this, Constants.pushMessage,
					keyBuf.toString(), false);
			PreferencesUtils.putBoolean(this, Constants.pushMessage,
					Constants.key_privateMsg, false);
		}

		initLoadingLayout();
		sendLayout = (RelativeLayout) findViewById(R.id.rlayout_send);
		sendLayout.setVisibility(View.GONE);
		input = (EditText) findViewById(R.id.privatem_input);
		findViewById(R.id.btn_send).setOnClickListener(this);
		desc = (TextView) findViewById(R.id.feed_desc);
		typeLayout = (RelativeLayout) findViewById(R.id.feed_type);
		typeLayout.setOnClickListener(this);

		list = (PullToRefreshListView) findViewById(R.id.privatem_list);
		list.getRefreshableView().setSelector(R.drawable.list_transe_selector);
		list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		list.setOnRefreshListener(this);

		a = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		getMail(0,count);
		types = getResources().getStringArray(R.array.feedback_type);
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("FeedbackMailActivity");
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra("otherUserId"))
			otherUserId = intent.getIntExtra("otherUserId", 0);

		getMail(0,listParser.mailList.size());
	}

	private void getIntentData() {
		Intent i = getIntent();

		if (i.hasExtra("otherUserIconURL"))
			otherUserIconURL = i.getStringExtra("otherUserIconURL");
		if (i.hasExtra("otherUserId"))
			otherUserId = i.getIntExtra("otherUserId", 0);
	}

	private void getMail(final int first,int count) {
		showLoadingLayout();
		if (count<10){
			count = 10;
		}
		if (first == 0){
			listParser.tempList.clear();
		}
		VolleyHelper.post(new FeedbackListlRequest(this,first,count).make(), new ParseListener(listParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				list.onRefreshComplete();
				hideLoadingLayout();
				if (listParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					sendLayout.setVisibility(View.VISIBLE);
					if (first != 0){
						updateList(listParser.mailList,false);
					}else {
						updateList(listParser.mailList,true);
					}
				} else {
					showErrorLayout();
				}
			}
		}, null);
	}
	FeedbackListParser listParser = new FeedbackListParser();
	ResultParser rparser = new ResultParser();
	private void sendMail(final String content, int type) {
		showLoadingLayout();
		VolleyHelper.post(new FeedbackAddRequest(this, type, content).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				input.setText("");
				MailData data = new MailData();
				data.timeStemp = "刚刚";
				data.content = content;
				data.isFromSelf = true;
				listParser.mailList.add(data);
				updateList(listParser.mailList,true);
			}
		}, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			if (input.getText().toString().trim().length() <= 0) {
				Toast.makeText(this, "请先说点什么...", Toast.LENGTH_SHORT).show();
				input.startAnimation(a);
//				EventMessage msg = new EventMessage("FeedbackMailActivity");
//				EventBus.getDefault().post(msg);
			} else {
				String content = input.getText().toString().trim();
				content = feedbackDesc + content;
				hideSoftPad();
				sendMail(content, feedbackType);
			}
			break;
		case R.id.feed_type:
			showTypeDialog();
			break;

		default:
			break;
		}
	}
	private void showTypeDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(types, (feedbackType - 1), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				feedbackType = (i+1);
				feedbackDesc = types[i]+":";
				desc.setText(types[i]);
				dialogInterface.dismiss();
			}
		});
		builder.setTitle("请选择反馈类型");
		builder.show();
	}

	private void hideSoftPad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(FeedbackMailActivity.this
								.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void updateList(ArrayList<MailData> mailList,boolean setSelect) {
		if (mailList != null) {
			pmla = new MailWithOtherAdapter(this, otherUserIconURL, mailList);
			int s = mailList.size();
			list.setAdapter(pmla);
			if (setSelect){
				list.getRefreshableView().setSelection(s - 1);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			return super.onKeyDown(keyCode, event);
		} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
			int cursor = input.getSelectionStart();
			input.getText().insert(cursor, "_  _");
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("FeedbackMailActivity");
		super.onResume();
		pushSp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pushSp.unregisterOnSharedPreferenceChangeListener(this);
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void reloadData() {
		getMail(0,listParser.mailList.size());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (keyBuf != null && key.equals(keyBuf.toString())
				&& sharedPreferences.getBoolean(key, false)) {
			getMail(listParser.mailList.size(),count);
			StringBuffer keyBuf = new StringBuffer(Constants.key_privateMsg);
			keyBuf.append("_").append(otherUserId).append("-")
					.append(UserNow.current().userID);
			Editor edit = pushSp.edit();
			edit.putBoolean(Constants.key_privateMsg, false);
			edit.putBoolean(keyBuf.toString(), false);
			edit.commit();

		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.feedBackList);
		VolleyHelper.cancelRequest(Constants.feedbackAdd);
	}

	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		getMail(listParser.mailList.size(), count);
	}
	public void onEvent(EventMessage msg){
		if (msg.name.equals("FeedbackMailActivity")){
			getMail(0,listParser.mailList.size());
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.cancel(TvBaiduPushMessageReceiver.NOTIFICATION_ID);
		}
	}
}
