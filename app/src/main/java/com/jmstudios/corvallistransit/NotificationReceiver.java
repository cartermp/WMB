package com.jmstudios.corvallistransit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jmstudios.corvallistransit.utils.SystemUtils;

/**
 * Class used to recieve a notification from an Alarm Manager.
 * <p/>
 * Used to send a local notification and vibrate the user's phone
 * when the time scheduled for their bus reminder is up.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SystemUtils.notifyPhone(context);
        SystemUtils.doVibrate(context);
    }
}
