package com.jmstudios.corvallistransit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jmstudios.corvallistransit.utils.SystemUtils;


public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SystemUtils.buildNotification(context);
        SystemUtils.doVibrate(context);
    }
}
