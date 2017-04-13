package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.fragment.CommentFragment;
import com.sumavision.talktv2.http.eventbus.SendCommenEvent;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 评论列表页<br>
 * 通过传递参数userId区分节目评论页和用户评论页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentActivity extends CommentBaseActivity implements
		OnClickListener {
	private long programId;
	private long topicId;
	private int userId;
	private String programName;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (userId == 0) {
			getSupportMenuInflater().inflate(R.menu.comment, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	CommentFragment fragment;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_comment) {
			openSendCommentActivity();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("CommentActivity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("CommentActivity");
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getExtra();
		if (userId == 0) {
			getSupportActionBar().setTitle("评论");
		} else if (userId == UserNow.current().userID) {
			getSupportActionBar().setTitle("我的评论");
		} else {
			getSupportActionBar().setTitle("他(她)的评论");
		}
		fragment = CommentFragment.newInstance((int) programId, userId,
				(int) topicId);
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
	}

	private void getExtra() {
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		programId = intent.getLongExtra("programId", 0L);
		topicId = intent.getLongExtra("topicId", 0L);
		// cpId = intent.getLongExtra("cpId", 0L);
		programName = intent.getStringExtra("programName");
	}

	private void openSendCommentActivity() {
		Intent intent = new Intent(this, SendCommentActivity.class);
		intent.putExtra("topicId", String.valueOf(topicId));
		intent.putExtra("programId", String.valueOf(programId));
		intent.putExtra("programName", programName);
		startActivityForResult(intent, SendCommentActivity.NORMAL);

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// 成功发表评论返回刷新列表
		if (arg0 == SendCommentActivity.NORMAL && arg1 == RESULT_OK) {
			fragment.getDefaultComment();
			EventBus.getDefault().post(new SendCommenEvent());
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.comment_item_content_audio_pic
				|| v.getId() == R.id.comment_item_content_audio_pic_root) {
			String voiceUrl = (String) v.getTag(R.id.item_url);
			ProgressBar progressBar = (ProgressBar) v.getTag(R.id.item_obj);
			playVoice(voiceUrl, (ImageView) v, progressBar);
		}
	}
}
