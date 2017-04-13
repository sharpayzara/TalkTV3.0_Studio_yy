package com.sumavision.talktv.videoplayer.activity;

import java.util.List;

import org.cybergarage.upnp.Device;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.itv.lib.dlna.model.DeviceProxy;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv2.dlna.services.DlnaControlService;
import com.sumavision.talktv2.dlna.services.OnDLNAEventListener;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * dlna操控页面:播放、暂停、seek、音量控制
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DLNAControllActivity extends CompatBaseActivity implements
		OnClickListener, OnSeekBarChangeListener, OnDLNAEventListener {

	public static DLNAControllActivity instance;
	public static boolean needShowResumeBtn;
	public static String currentTime;
	public static String totalTime;
	private Device device;

	public boolean isLivePlay = false;
	private boolean isResume;
	private boolean hasPlayingOnTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlna_controll);
		if (getSupportActionBar() != null){
			getSupportActionBar().hide();
		}
		instance = this;
		initViews();
		hasPlayingOnTV = PreferencesUtils.getBoolean(this, null, "isPlaying",
				false);
		getExtras();
		if (device == null)
			return;
		if (device.getFriendlyName() != null) {
			((TextView) findViewById(R.id.device_name)).setText(namePre
					+ device.getFriendlyName() + nameSuf);
		}
		if (titleName != null) {
			((TextView) findViewById(R.id.title)).setText(titleName);
		}
		if (dlnaControlService == null) {
			bindService(new Intent(this, DlnaControlService.class), connection,
					Context.BIND_AUTO_CREATE);
		}

	}

	private DlnaControlService dlnaControlService;
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			dlnaControlService = ((DlnaControlService.MyBinder) service)
					.getService();
			dlnaControlService.setEventListener(DLNAControllActivity.this);
			getVolume();
			if (!isResume) {
				resumeState();
				initViewState();
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (hasPlayingOnTV) {
			if (isLivePlay) {
				handler.removeMessages(GET_POSITION_INFO_REQUEST);
				totalTimeView.setText("99:99:99");
				currentTimeView.setText("00:00:00");
				progressSeekBar.setProgress(0);
				progressSeekBar.setEnabled(false);
				DLNAControllActivity.totalTime = "99:99:99";
				DLNAControllActivity.currentTime = "00:00:00";
			} else {
				handler.sendEmptyMessage(GET_POSITION_INFO_REQUEST);
			}
		}
	}

	private boolean isNeedSetTransportUrl = false;
	private boolean isNeedShowSameVideoDialog = false;

	private void resumeState() {
		currentUrl = PreferencesUtils.getString(this, null, "url", null);
		if (playUrl != null) {
			if (currentUrl != null) {
				// 有节目在播放
				if (playUrl.equals(currentUrl)) {
					// 同一节目
					if (hasPlayingOnTV)
						isNeedSetTransportUrl = false;
					else
						isNeedSetTransportUrl = true;
				} else {
					// 不同节目
					isNeedShowSameVideoDialog = false;
					isNeedSetTransportUrl = true;
					currentUrl = playUrl;
					currentPosition = 0;
					handler.removeMessages(GET_POSITION_INFO_REQUEST);
					handler.removeMessages(AUTO_INCRESE);
					currentTimeView.setText("00:00:00");
					progressSeekBar.setProgress(currentPosition);
				}
			} else {
				// 表示无播放状态
				currentUrl = playUrl;
				isNeedSetTransportUrl = true;
			}
		} else {
			finish();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveDlnaShareprefereces();
		closeTimeThread();
		handler.removeMessages(GET_POSITION_INFO_REQUEST);
		handler.removeMessages(AUTO_INCRESE);
		handler.removeMessages(DELAY_CLOSE_ME);
		unbindService(connection);

	}

	private void saveDlnaShareprefereces() {
		PreferencesUtils.putString(this, null, "url", currentUrl);
		PreferencesUtils.putBoolean(this, null, "isPlaying", hasPlayingOnTV);
		PreferencesUtils.putBoolean(this, null, "hasSetTranportUrl",
				hasSetTranportUrl);
		PreferencesUtils.putBoolean(this, null, "isLivePlay", isLivePlay);
		PreferencesUtils.putString(this, null, "titleName", titleName);
	}

	// 传过来的播放地址
	private String playUrl;
	private String titleName;
	private String currentUrl;
	private int breakPoint;
	private long videoDuration;

	private void getExtras() {
		Intent intent = getIntent();
		isResume = intent.getBooleanExtra("isResume", false);
		if (!isResume) {
			titleName = intent.getStringExtra("titleName");
			// ShortDevice deviceDataInSearchList = (ShortDevice) intent
			// .getSerializableExtra("selectedDevice");
			device = getDeviceByShortData(titleName);
			playUrl = intent.getStringExtra("playAddress");
		} else {
			device = DeviceProxy.getInstance().getSelectedDevice();
			currentUrl = PreferencesUtils.getString(this, null, "url", null);
			playUrl = currentUrl;
			titleName = PreferencesUtils.getString(this, null, "titleName",
					null);
		}
		if (intent.hasExtra("isLivePlay")) {
			isLivePlay = intent.getBooleanExtra("isLivePlay", false);
		} else {
			isLivePlay = PreferencesUtils.getBoolean(this, null, "isLivePlay",
					false);
		}
		breakPoint = intent.getIntExtra("breakPoint", 0);
		videoDuration = intent.getLongExtra("videoDuration", 0);
	}

	private Device getDeviceByShortData(String name) {
		List<Device> list = DeviceProxy.getInstance().getDeviceList();
		for (int i = 0; i < list.size(); i++) {
			Device device = list.get(i);
			if (name.equals(device.getFriendlyName())) {
				return device;
			}
		}
		return null;
	}

	// 界面按钮 文本
	private TextView currentTimeView, totalTimeView;
	private ImageButton playBtn, advanceBtn, goBackBtn, volBtn;
	private Button stopPlayBtn;
	private DLNAVerticalSeekBar volSeekBar;
	private SeekBar progressSeekBar;
	private static final String namePre = "此节目正在\"";
	private static final String nameSuf = "\"上播放";

	private void initViews() {
		currentTimeView = (TextView) findViewById(R.id.currentTimeView);
		totalTimeView = (TextView) findViewById(R.id.totalTimeView);
		volSeekBar = (DLNAVerticalSeekBar) findViewById(R.id.vol);
		playBtn = (ImageButton) findViewById(R.id.tool_play);
		advanceBtn = (ImageButton) findViewById(R.id.tool_advance);
		goBackBtn = (ImageButton) findViewById(R.id.tool_back);
		volBtn = (ImageButton) findViewById(R.id.tool_vol);
		progressSeekBar = (SeekBar) findViewById(R.id.tool_progress);
		stopPlayBtn = (Button) findViewById(R.id.tv_back);
		playBtn.setOnClickListener(this);
		advanceBtn.setOnClickListener(this);
		goBackBtn.setOnClickListener(this);
		volBtn.setOnClickListener(this);
		stopPlayBtn.setOnClickListener(this);
		progressSeekBar.setOnSeekBarChangeListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		volSeekBar.setOnSeekBarChangeListener(volListener);
	}

	private void getVolume() {
		try {
			dlnaControlService.getVlume();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getPositionInfo() {
		dlnaControlService.getPosition();
	}

	private int currentPosition;
	private int totalPosition = Integer.MAX_VALUE;
	private TimeThread timeThread;

	void syncPosition() {
		synchronized (this) {
			String totalTime = DLNAControllActivity.totalTime;
			if (totalTime.equals("00:00:00") && videoDuration > 0) {
				totalTime = getTimeString((int) videoDuration);
			}
			if (totalTimeView.getText().equals(getString(R.string.dlna_time))
					&& !totalTime.equals("00:00:00")
					&& !totalTime.equals("0:00:00.000")) {
				String[] my = totalTime.split(":");
				int hour = Integer.parseInt(my[0]);
				int min = Integer.parseInt(my[1]);
				int sec = Integer.parseInt(my[2]);
				int totalSec = hour * 3600 + min * 60 + sec;
				totalPosition = totalSec;
				totalTimeView.setText(totalTime);
				progressSeekBar.setMax(totalPosition);
			}

			String tmp = DLNAControllActivity.currentTime;
			if (!tmp.equals("00:00:00") && !tmp.equals("0:00:00.000")) {
				String[] my = tmp.split(":");
				int hour = Integer.parseInt(my[0]);
				int min = Integer.parseInt(my[1]);
				int sec = Integer.parseInt(my[2]);
				int totalSec = hour * 3600 + min * 60 + sec;
				currentPosition = totalSec;
				if (currentPosition < totalPosition) {
					currentTimeView.setText(tmp);
					progressSeekBar.setProgress(currentPosition);
					progressSeekBar.setEnabled(true);
				}

			}

			if (currentPosition != 0 && totalPosition != 0) {
				handler.removeMessages(AUTO_INCRESE);
				handler.sendEmptyMessageDelayed(AUTO_INCRESE, 1000);
			}
		}
	}

	private void updateTimeViews() {
		synchronized (this) {
			currentPosition += 1;
			if (currentPosition >= totalPosition) {
				Log.e("DLNAControllActivity", "@播放结束");
				handler.sendEmptyMessageDelayed(DELAY_CLOSE_ME, 60000);
				return;
			}
			String nowTimeValue = getTimeString(currentPosition);
			currentTimeView.setText(nowTimeValue);
			progressSeekBar.setProgress(currentPosition);

		}
	}

	private String getTimeString(int allSecond) {
		int minute, hour, second;
		hour = allSecond / 3600;
		minute = (allSecond - hour * 3600) / 60;
		second = (allSecond - hour * 3600 - minute * 60);
		String currentTime = "";
		if (hour < 10) {
			currentTime += "0" + hour + ":";
		} else {
			currentTime += hour + ":";
		}
		if (minute < 10) {
			currentTime += "0" + minute + ":";
		} else {
			currentTime += minute + ":";
		}
		if (second < 10) {
			currentTime += "0" + second;
		} else {
			currentTime += second;
		}
		return currentTime;
	}

	private void closeTimeThread() {
		if (timeThread != null) {
			timeThread.kill();
			timeThread = null;
		}
	}

	private boolean isInit = false;
	private static final int GET_POSITION_INTERVAL = 10000;
	private final int GET_POSITION_INFO_REQUEST = 22;
	private final int AUTO_INCRESE = 122;
	private final int INIT_OVER = 123;
	private final int DELAY_CLOSE_ME = 127;
	private final int PLAY_SUCCEED = 128;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_POSITION_INFO_REQUEST:
				if (!isLivePlay)
					getPositionInfo();
				break;
			case AUTO_INCRESE:
				handler.sendEmptyMessageDelayed(AUTO_INCRESE, 1000);
				updateTimeViews();
				break;
			case INIT_OVER:
				isInit = true;
				if (isResume) {
					if (!isLivePlay)
						getPositionInfo();
					executeSendPlay();
				} else {
					if (!hasSetTranportUrl && isNeedSetTransportUrl) {
						executeSetTransportUrl(currentUrl);
					}
				}
				break;
			case DELAY_CLOSE_ME:
				finish();
				break;
			case PLAY_SUCCEED:
				if (!isLivePlay) {
					enableBtns();
					handler.removeMessages(GET_POSITION_INFO_REQUEST);
					handler.sendEmptyMessage(GET_POSITION_INFO_REQUEST);
				}
				break;
			default:
				break;
			}
		};
	};

	private boolean hasSetTranportUrl = false;

	private void processPlayPause() {
		if (isNeedShowSameVideoDialog) {
			showChangeVideoDialog();
		} else {
			if (!hasSetTranportUrl && isNeedSetTransportUrl) {
				handler.removeMessages(AUTO_INCRESE);
				executeSetTransportUrl(currentUrl);
			} else {
				if (hasPlayingOnTV) {
					if (isPlaying) {
						executeSendPause();
						handler.removeMessages(AUTO_INCRESE);
					} else {
						executeSendPlay();
						handler.removeMessages(AUTO_INCRESE);
						handler.sendEmptyMessageDelayed(AUTO_INCRESE, 1000);
					}
				} else {
					executeSendPlay();
				}
			}
		}
	}

	private void showChangeVideoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("电视上可能有其他节目在播放，是否切换?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentUrl = playUrl;
				isNeedSetTransportUrl = false;
				isNeedShowSameVideoDialog = false;
				hasSetTranportUrl = false;
				executeSetTransportUrl(currentUrl);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tool_play) {
			if (isInit) {
				processPlayPause();
			} else {
				Toast.makeText(getApplicationContext(), "请等待初始化",
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.back) {
			if (hasPlayingOnTV) {
				needShowResumeBtn = true;
			} else {
				needShowResumeBtn = false;
			}
			finish();
		} else if (v.getId() == R.id.tv_back) {
			executeSendStopPlay();

		} else if (v.getId() == R.id.tool_advance) {
			if (!isLivePlay) {
				int jindu = currentPosition + 30000;
				if (jindu >= totalPosition) {
					return;
				} else {
					seekPosition = jindu;
					String seek = getTimeString(jindu);
					executeSendSeek(seek);
				}
			}
		} else if (v.getId() == R.id.tool_back) {
			if (!isLivePlay) {
				int position = currentPosition - 30000;
				if (position <= 0) {
					return;
				} else {
					seekPosition = position;
					String seek = getTimeString(position);
					executeSendSeek(seek);
				}
			}
		} else if (v.getId() == R.id.tool_vol) {
			if (volSeekBar.isShown()) {
				volSeekBar.setVisibility(View.GONE);
			} else {
				volSeekBar.setVisibility(View.VISIBLE);
			}
		}
	}

	private boolean needSeek;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			needSeek = true;
			String nowTimeValue = getTimeString(progress);
			currentTimeView.setText(nowTimeValue);
		} else {
			needSeek = false;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (needSeek) {
			seekPosition = seekBar.getProgress();
			String seek = getTimeString(seekPosition);
			if (!isLivePlay)
				executeSendSeek(seek);
			else {
				totalTimeView.setText("99:99:99");
				currentTimeView.setText("00:00:00");
				progressSeekBar.setProgress(0);
				progressSeekBar.setEnabled(false);
			}
		}
	}

	private int seekPosition;

	private void executeSetTransportUrl(String playUrl) {
		showTipsDialog();
		dlnaControlService.setUrl(playUrl);
	}

	private void executeSendPlay() {
		showTipsDialog();
		dlnaControlService.play();
	}

	private void executeSendPause() {
		showTipsDialog();
		dlnaControlService.pause();
	}

	private void executeSendStopPlay() {
		showTipsDialog();
		dlnaControlService.stopDlna();
	}

	private void executeSetVol(int vol) {
		dlnaControlService.setVolume(vol);
	}

	private void executeSendSeek(String seek) {
		showTipsDialog();
		handler.removeMessages(AUTO_INCRESE);
		handler.removeMessages(GET_POSITION_INFO_REQUEST);
		dlnaControlService.seek(seek, !isLivePlay);
	}

	public static boolean isPlaying;
	public boolean isGetVol;
	public boolean isNeedInit = true;

	public void initSeekViews() {
		// totalTimeView.setText();
	}

	/*
	 * 设置view的初始状态
	 */
	private void initViewState() {
		goBackBtn.setEnabled(false);
		advanceBtn.setEnabled(false);
		progressSeekBar.setEnabled(false);
	}

	private void enableBtns() {
		goBackBtn.setEnabled(true);
		advanceBtn.setEnabled(true);
		volBtn.setEnabled(true);
	}

	/**
	 * 快进快退可按
	 */
	private void setPlayBtnState() {
		if (isPlaying) {
			playBtn.setImageResource(R.drawable.cp_play_tool_pause_bg);
		} else {
			playBtn.setImageResource(R.drawable.cp_play_tool_play_bg);
		}
	}

	private ProgressDialog tipsDialog;

	private void showTipsDialog() {
		if (tipsDialog != null) {
			tipsDialog.dismiss();
			tipsDialog = null;
		}
		tipsDialog = ProgressDialog.show(DLNAControllActivity.this, "",
				"处理中，请稍后...", true, true,
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
	}

	private void hideTipsDialog() {
		if (tipsDialog != null) {
			tipsDialog.dismiss();
			tipsDialog = null;
		}
	}

	private int volumeProgress = 0;

	private DLNAVerticalSeekBar.OnSeekBarChangeListener volListener = new DLNAVerticalSeekBar.OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(DLNAVerticalSeekBar vBar) {
			executeSetVol(volumeProgress);
			setVolBtnImage();
		}

		@Override
		public void onStartTrackingTouch(DLNAVerticalSeekBar vBar) {
		}

		@Override
		public void onProgressChanged(DLNAVerticalSeekBar vBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				volumeProgress = progress;
			}

		}
	};

	private void setVolBtnImage() {
		if (volumeProgress <= 30 && volumeProgress > 0) {
			volBtn.setImageResource(R.drawable.player_sound_min);
		} else if (volumeProgress > 70) {
			volBtn.setImageResource(R.drawable.player_sound_max);
		} else if (volumeProgress <= 70 && volumeProgress > 30) {
			volBtn.setImageResource(R.drawable.player_sound_middle);
		} else {
			volBtn.setImageResource(R.drawable.player_sound_disable);
		}
	}

	class TimeThread extends Thread {
		public boolean isPause;
		public boolean needKill;

		@Override
		public void run() {
			while (!isPause) {
				if (needKill) {
					break;
				}
				handler.sendEmptyMessage(AUTO_INCRESE);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void pause() {
			isPause = true;
		}

		public void restart() {
			isPause = false;
		}

		public void kill() {
			needKill = true;
		}
	}

	// private boolean afterResetDevice = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (hasPlayingOnTV) {
				needShowResumeBtn = true;
			} else {
				needShowResumeBtn = false;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void eventSetUrl(boolean isOk) {
		hideTipsDialog();
		if (isOk) {
			hasSetTranportUrl = true;
			executeSendPlay();
		} else {
			Toast.makeText(getApplicationContext(), "初始化播放失败!",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void eventPlay(boolean isOk) {
		hideTipsDialog();
		if (isOk) {
			isPlaying = true;
			setPlayBtnState();
			hasPlayingOnTV = true;
			saveDlnaShareprefereces();
			if (isNeedInit) {
				isNeedInit = false;
				handler.sendEmptyMessage(PLAY_SUCCEED);
			}
		} else {
			Toast.makeText(getApplicationContext(), "播放失败!", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void eventPause(boolean isOk) {
		hideTipsDialog();
		if (isOk) {
			isPlaying = false;
			setPlayBtnState();
		} else {
			Toast.makeText(getApplicationContext(), "暂停失败!", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void eventSeek(boolean isOk) {
		hideTipsDialog();
		if (isOk) {
			currentPosition = seekPosition;
			progressSeekBar.setProgress(currentPosition);
		}
		handler.sendEmptyMessage(AUTO_INCRESE);
		handler.sendEmptyMessageDelayed(GET_POSITION_INFO_REQUEST,
				GET_POSITION_INTERVAL);
	}

	@Override
	public void eventStop(boolean isOk) {
		hideTipsDialog();
		if (isOk) {
			isPlaying = false;
			hasPlayingOnTV = false;
			needShowResumeBtn = false;
			currentUrl = null;
			hasSetTranportUrl = false;
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "关闭失败!", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void eventSetVolume(boolean isOk) {
	}

	@Override
	public void eventGetVolume(boolean isOk, String vol) {
		if (isOk) {
			handler.sendEmptyMessage(INIT_OVER);
			String volumeString = vol;
			if (StringUtils.isNotEmpty(volumeString)) {
				Integer volValue = Integer.parseInt(volumeString);
				volSeekBar.setProgress(volValue);
				volumeProgress = volValue;
				setVolBtnImage();
			}
		}

	}

	@Override
	public void getPosition(boolean isOk, String absTime, String realTime,
			String trackDuration) {
		DLNAControllActivity.totalTime = trackDuration;
		DLNAControllActivity.currentTime = realTime;
		syncPosition();
		if (breakPoint > 10) {
			seekPosition = breakPoint;
			executeSendSeek(getTimeString(breakPoint));
			breakPoint = 0;
		}
		handler.removeMessages(GET_POSITION_INFO_REQUEST);
		handler.sendEmptyMessageDelayed(GET_POSITION_INFO_REQUEST,
				GET_POSITION_INTERVAL);

	}
}
