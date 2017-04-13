package com.sumavision.talktv2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * Created by Administrator on 2015/5/13.
 */
public class DateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
            PreferencesUtils.remove(context,null,"curDate");
            PreferencesUtils.remove(context,null,"curDate_found_award_date");
            PreferencesUtils.remove(context,null,"curDate_found_award_ids");
        }
    }
}
