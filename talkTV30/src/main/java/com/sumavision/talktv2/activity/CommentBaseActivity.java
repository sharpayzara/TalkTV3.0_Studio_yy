package com.sumavision.talktv2.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.service.PlayAudioService;
import com.sumavision.talktv2.service.PlayAudioService.AudioBinder;
import com.sumavision.talktv2.service.PlayAudioService.OnPlayAudioListener;

/**
 * 评论相关基类
 * 
 * @author suma-hpb
 * 
 */
public class CommentBaseActivity extends BaseActivity {
	private String currentUrl;
	private ProgressBar currentProgressBar;
	private ImageView currentImageButton;
	PlayAudioService playService;
	ServiceConnection audioConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			playService = ((AudioBinder) service).getService();
			playService.setOnPlayAudioListener(new OnPlayAudioListener() {

				@Override
				public void onPrepared() {
					currentImageButton
							.setImageResource(R.drawable.pc_switch2audio_big_pressed);
					currentProgressBar.setVisibility(View.GONE);
				}

				@Override
				public void onCompletion() {
					currentImageButton
							.setImageResource(R.drawable.pc_switch2audio_big_normal);
					currentProgressBar.setVisibility(View.GONE);
					currentUrl = null;
				}
			});

		}
	};

	protected void playVoice(String voiceUrl, final ImageView button,
			final ProgressBar progressBar) {
		currentProgressBar = progressBar;
		currentImageButton = button;
		if (currentUrl != null && voiceUrl.equals(currentUrl)) {
			playService.stopPlay();
			currentProgressBar.setVisibility(View.GONE);
			currentImageButton
					.setImageResource(R.drawable.pc_switch2audio_big_normal);
			currentUrl = null;
		} else {
			currentUrl = voiceUrl;
			currentProgressBar.setVisibility(View.VISIBLE);
			Intent intent = new Intent();
			intent.putExtra("url", voiceUrl);
			playService.startPlay(intent);
		}

	}

	private void closeMediaPlayer() {
		unbindService(audioConn);
		if (currentProgressBar != null) {
			currentProgressBar.setVisibility(View.GONE);
			currentProgressBar = null;
		}
		if (currentImageButton != null) {
			currentImageButton
					.setImageResource(R.drawable.pc_switch2audio_big_normal);
			currentImageButton = null;
		}
		currentUrl = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this, PlayAudioService.class), audioConn,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeMediaPlayer();
	}
}
