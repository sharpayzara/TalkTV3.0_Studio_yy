package com.sumavision.talktv2.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.dao.AccessProgram;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.ClearActionProvider;
import com.sumavision.talktv2.adapter.PlayHistoryAdapter;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.fragment.CommonDialogFragment.OnCommonDialogListener;
import com.sumavision.talktv2.fragment.DeleteDialogFragment;
import com.sumavision.talktv2.fragment.DeleteDialogFragment.OnClickDeleteListener;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史<br>
 * 
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class PlayHistoryActivity extends BaseActivity implements
		OnItemClickListener, OnCommonDialogListener, OnItemLongClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_playhistory);
		setContentView(R.layout.activity_play_history);
		emptyLayout = (RelativeLayout) findViewById(R.id.rlayout_empty);
		playHistoryListView = (ListView) findViewById(R.id.listView);
		playHistoryListView.setOnItemClickListener(this);
		playHistoryListView.setOnItemLongClickListener(this);
		setPlayHistoryList();
		if (list.size() == 0) {
			emptyLayout.setVisibility(View.VISIBLE);
			showActionHandler.removeMessages(0);
			showActionHandler.sendEmptyMessageAtTime(0, 1000);
		}
		receiver = new DataChangeReceiver();
		registerReceiver(receiver, new IntentFilter(
				AccessProgram.DATABASE_UPDATE));
	}

	DataChangeReceiver receiver;

	class DataChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			setPlayHistoryList();
			if (list.size() == 0) {
				emptyLayout.setVisibility(View.VISIBLE);
				showActionHandler.removeMessages(0);
				showActionHandler.sendEmptyMessageAtTime(0, 1000);
			}

		}

	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("PlayhistoryActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("PlayhistoryActivity");
		super.onResume();
	}

	private ListView playHistoryListView;
	private List<NetPlayData> list = new ArrayList<NetPlayData>();
	private PlayHistoryAdapter adapter;
	private RelativeLayout emptyLayout;

	private void setPlayHistoryList() {
		list = AccessProgram.getInstance(this).findAll();
		if(list != null && list.size()>15){
			list = list.subList(0,15);
		}
		adapter = new PlayHistoryAdapter(this, list);
		playHistoryListView.setAdapter(adapter);
	}

	private boolean showAction;
	ClearActionProvider clearActionProvider;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (list.size() > 0) {
			getSupportMenuInflater().inflate(R.menu.menu_clear, menu);
			clearActionProvider = (ClearActionProvider) menu.findItem(
					R.id.action_clear).getActionProvider();
			if (clearActionProvider != null){
				clearActionProvider.setDialogTitle("清空播放历史");
				if (showAction) {
					clearActionProvider.showOption();
				}
				clearActionProvider.setOnClickListener(this);
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	Handler showActionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			showAction = true;
			if (clearActionProvider != null) {
				clearActionProvider.showOption();
			}
		};
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (list.size() > 0) {
			showActionHandler.removeMessages(0);
			showActionHandler.sendEmptyMessageAtTime(0, 1000);
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NetPlayData temp = list.get(position);
		if (temp.isdownload.equals("1") || temp.subid == 0) {
			openPlayerActivity(temp);
		} else {
			if (temp.subid != 0) {
				PreferencesUtils.putInt(this, Constants.SP_PLAY_RECORD, temp.id + "", temp.subid);
			}
			openProgramActivity(temp);
		}
	}
	
	private void openProgramActivity(NetPlayData program) {
		String programId = String.valueOf(program.id);
		openProgramDetailActivity(programId,program.subid, program.subid!=0);
	}
	
	public void openProgramDetailActivity(String id,int subId, boolean flag) {
		Intent intent = new Intent(this, PlayerActivity.class);
		long programId = Long.valueOf(id);
		intent.putExtra("id", programId);
		intent.putExtra("subid",subId);
		intent.putExtra("isHalf", flag);
		intent.putExtra("point", 1);
		intent.putExtra("where",5);
		startActivity(intent);
	}

	private void openPlayerActivity(NetPlayData data) {
		Intent intent = new Intent();
		if (TextUtils.isEmpty(data.url) || data.url.endsWith(".png")
				|| data.url.endsWith(".jpg")) {
			intent.setClass(this, WebAvoidPicActivity.class);
			intent.putExtra("path", data.videoPath);
			intent.putExtra("url", data.videoPath);
			intent.putExtra("where",5);
		} else {
			intent.setClass(this, WebAvoidActivity.class);
			intent.putExtra("url", data.url);
			intent.putExtra("path", data.url);
			intent.putExtra("where",5);
		}
		intent.putExtra("hideFav", true);
		if (data.isdownload.equals("1")) {
			intent.putExtra(PlayerActivity.INTENT_NEEDAVOID, false);
			intent.putExtra("path", data.videoPath);
			intent.putExtra("playType", PlayerActivity.CACHE_PLAY);
		} else {
			intent.putExtra("playType", PlayerActivity.VOD_PLAY);
		}
		long topicId = 0;
		if (!TextUtils.isEmpty(data.topicId)) {
			topicId = Long.parseLong(data.topicId);
		}
		intent.putExtra("topicId", topicId);
		intent.putExtra("title", data.name);
		intent.putExtra("id", data.id);
		intent.putExtra("subid", data.subid);
		intent.putExtra("isHalf", false);
		startActivity(intent);
	}

	@Override
	public void onPositiveButtonClick() {
		emptyLayout.setVisibility(View.VISIBLE);
		list.clear();
		adapter.notifyDataSetChanged();
		AccessProgram.getInstance(this).clear();
		clearActionProvider.hideOption();
		PreferencesUtils.clearAll(PlayHistoryActivity.this, Constants.SP_PLAY_RECORD);
	}

	@Override
	public void onNeutralButtonClick() {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, final long id) {
		DeleteDialogFragment fragment = DeleteDialogFragment
				.newInstance(getString(R.string.delete_this_history));
		fragment.setOnClickDeleteListener(new OnClickDeleteListener() {

			@Override
			public void onDeleteClick() {
				AccessProgram.getInstance(PlayHistoryActivity.this).delete(
						list.get((int) id));
				NetPlayData temp = list.get((int) id);
				list.remove((int) id);
				adapter.notifyDataSetChanged();
				if (list.size() == 0) {
					clearActionProvider.hideOption();
				}
				PreferencesUtils.remove(PlayHistoryActivity.this, Constants.SP_PLAY_RECORD, temp.id+"");
			}
		});
		fragment.show(getSupportFragmentManager(), "delete");
		return true;
	}

	@Override
	public void onNegativeButtonClick() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
