package com.sumavision.talktv.videoplayer.ui;

import com.sumavision.talktv.videoplayer.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.BatteryManager;
import android.view.View;
import android.widget.ImageView;

/**
 * 电量展示广播
 * 
 * @author suma-hpb
 * 
 */
public class BatteryReceiver extends BroadcastReceiver {
	ImageView batteryImageView;
	AnimationDrawable anim;
	public BatteryReceiver(ImageView batteryImageView) {
		this.batteryImageView = batteryImageView;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		int level = intent.getIntExtra("level", 0);
		int status = intent.getIntExtra("status",
				BatteryManager.BATTERY_HEALTH_UNKNOWN);
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			batteryImageView.setBackgroundResource(R.drawable.charging);
			anim = (AnimationDrawable) batteryImageView.getBackground();
			anim.start();
		}else{
			if(anim != null && anim.isRunning()){
				anim.stop();
			}
			if (level >= 90) {
				batteryImageView.setBackgroundResource(R.drawable.battery_90);
			} else if (level >= 70 && level < 90) {
				batteryImageView.setBackgroundResource(R.drawable.battery_70);
			} else if (level >= 40 && level < 70) {
				batteryImageView.setBackgroundResource(R.drawable.battery_40);
			} else if (level >= 10 && level < 40) {
				batteryImageView.setBackgroundResource(R.drawable.battery_10);
			} else if (level < 10 && level > 3) {
				batteryImageView.setBackgroundResource(R.drawable.battery_0);
			} else if (level < 3) {
				batteryImageView.setBackgroundResource(R.drawable.battery_00);
			}
		}
//		if (!batteryImageView.isShown()) {
//			batteryImageView.setVisibility(View.VISIBLE);
//		}
		
	}
}
