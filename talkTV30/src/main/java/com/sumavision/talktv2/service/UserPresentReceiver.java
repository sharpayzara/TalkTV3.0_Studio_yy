package com.sumavision.talktv2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class UserPresentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println("action"+intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            if (!PushUtils.hasBind(context)) {
                PushManager.startWork(context.getApplicationContext(),
                        PushConstants.LOGIN_TYPE_API_KEY,
                        PushUtils.getMetaValue(context, "api_key"));
            } else {
                PushManager.resumeWork(context);
            }
        }
    }
}
