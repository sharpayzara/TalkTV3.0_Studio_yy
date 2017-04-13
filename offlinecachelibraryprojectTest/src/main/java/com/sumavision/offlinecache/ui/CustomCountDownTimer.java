package com.sumavision.offlinecache.ui;

import android.os.CountDownTimer;

public abstract class CustomCountDownTimer extends CountDownTimer {

	public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onTick(long millisUntilFinished) {

	}

}
