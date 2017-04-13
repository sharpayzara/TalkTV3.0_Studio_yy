package com.sumavision.talktv2.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * 播放声音服务
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class PlayAudioService extends Service implements OnPreparedListener,
		OnCompletionListener, OnErrorListener {
	MediaPlayer mediaPlayer;

	private String playUrl;
	int rawId;

	@Override
	public IBinder onBind(Intent intent) {
		return mAudioBinder;
	}

	private AudioBinder mAudioBinder = new AudioBinder();

	public class AudioBinder extends Binder {
		public PlayAudioService getService() {
			return PlayAudioService.this;
		}
	}

	private OnPlayAudioListener mOnPlayAudioListener;

	public void setOnPlayAudioListener(OnPlayAudioListener mOnPlayAudioListener) {
		this.mOnPlayAudioListener = mOnPlayAudioListener;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startPlay(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	public void startPlay(Intent intent) {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			rawId = intent.getIntExtra("raw", 0);
			playUrl = intent.getStringExtra("url");
			if (rawId == 0 && TextUtils.isEmpty(playUrl)) {
				return;
			}
			initPlayer();
		} else {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
			if ((rawId > 0 && rawId != intent.getIntExtra("raw", 0))
					|| playUrl != intent.getStringExtra("url")) {
				rawId = intent.getIntExtra("raw", 0);
				playUrl = intent.getStringExtra("url");
				initPlayer();
			}
		}
	}

	public void stopPlay() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}

		}
	}

	private void initPlayer() {
		try {
			if (rawId > 0) {
				AssetFileDescriptor afd = getResources().openRawResourceFd(
						rawId);
				mediaPlayer.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getLength());
			} else if (!TextUtils.isEmpty(playUrl)) {
				mediaPlayer.setDataSource(playUrl);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.prepareAsync();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (mediaPlayer != null)
			mediaPlayer.release();
		mediaPlayer = null;
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mediaPlayer != null)
			mediaPlayer.release();
		mediaPlayer = null;
		if (mOnPlayAudioListener != null) {
			mOnPlayAudioListener.onCompletion();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (mOnPlayAudioListener != null) {
			mOnPlayAudioListener.onPrepared();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public interface OnPlayAudioListener {
		public void onPrepared();

		public void onCompletion();
	}
}
