package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.Context;
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
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MailWithOtherAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnMailListListener;
import com.sumavision.talktv2.http.listener.OnSendMailListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * @author hpb
 * @description 私信对话界面
 * @changeLog
 */

public class UserMailActivity extends BaseActivity implements OnClickListener,
		OnMailListListener, OnSendMailListener,
		OnSharedPreferenceChangeListener {

	private EditText input;
	private ListView list;
	private MailWithOtherAdapter pmla;
	private Animation a;
	private String otherUserName;
	private String otherUserIconURL;
	private int otherUserId;
	StringBuffer keyBuf;
	SharedPreferences pushSp;
	RelativeLayout sendLayout;
	private boolean isVip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		if (!TextUtils.isEmpty(otherUserName)) {
			getSupportActionBar().setTitle("与" + otherUserName + "的私信");
		}
		setContentView(R.layout.activity_send_mail);
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

		list = (ListView) findViewById(R.id.privatem_list);
		list.setSelector(R.drawable.list_transe_selector);

		a = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		getMail();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UserMailActivity");
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra("otherUserName"))
			otherUserName = intent.getStringExtra("otherUserName");
		if (intent.hasExtra("otherUserId"))
			otherUserId = intent.getIntExtra("otherUserId", 0);
		if (!TextUtils.isEmpty(otherUserName)) {
			getSupportActionBar().setTitle("与" + otherUserName + "的私信");
		}
		getMail();
	}

	private void getIntentData() {
		Intent i = getIntent();

		if (i.hasExtra("otherUserName"))
			otherUserName = i.getStringExtra("otherUserName");
		if (i.hasExtra("otherUserIconURL"))
			otherUserIconURL = i.getStringExtra("otherUserIconURL");
		if (i.hasExtra("otherUserId"))
			otherUserId = i.getIntExtra("otherUserId", 0);
		isVip = i.getBooleanExtra("isVip",false);
	}

	private void getMail() {
		showLoadingLayout();
		VolleyUserRequest.mail(this, this, otherUserId, 0, 20);
	}

	private void sendMail(String content, String pic) {
		showLoadingLayout();
		VolleyUserRequest.sendMail(this, otherUserId, this, content, pic);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			if (input.getText().toString().trim().length() <= 0) {
				Toast.makeText(this, "请先说点什么...", Toast.LENGTH_SHORT).show();
				input.startAnimation(a);
			} else {
				String content = input.getText().toString().trim();
				hideSoftPad();
				sendMail(content, "");
			}
			break;

		default:
			break;
		}
	}

	private void hideSoftPad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(UserMailActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void updateList(ArrayList<MailData> mailList) {
		if (mailList != null) {
			if (TextUtils.isEmpty(otherUserName) && mailList.size() > 0) {
				otherUserName = mailList.get(0).sUserName;
				getSupportActionBar().setTitle("与" + otherUserName + "的私信");
			}
			setItemVip(mailList);
			pmla = new MailWithOtherAdapter(this, otherUserIconURL, mailList);
			int s = mailList.size();
			list.setAdapter(pmla);
			list.setSelection(s - 1);
		} else {

		}
	}
	private void setItemVip(ArrayList<MailData> mailList){
		if (mailList != null){
			MailData temp;
			for (int i=0;i<mailList.size();i++){
				temp = mailList.get(i);
				if (temp.isFromSelf){
					temp.isVip = UserNow.current().isVip;
				}else {
					temp.isVip = isVip;
				}
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
		MobclickAgent.onPageStart("UserMailActivity");
		super.onResume();
		pushSp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pushSp.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void reloadData() {
		getMail();
	}

	@Override
	public void getMail(int errCode, int mailCount, ArrayList<MailData> mailList) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			sendLayout.setVisibility(View.VISIBLE);
			updateList(mailList);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void sendMailResult(int errCode, int mailCount,
			ArrayList<MailData> mailList) {
		hideLoadingLayout();
		input.setText("");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			updateList(mailList);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (keyBuf != null && key.equals(keyBuf.toString())
				&& sharedPreferences.getBoolean(key, false)) {
			getMail();
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
		VolleyHelper.cancelRequest(Constants.mailDetail);
		VolleyHelper.cancelRequest(Constants.mailAdd);
	}
}
