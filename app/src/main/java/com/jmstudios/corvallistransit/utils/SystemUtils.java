package com.jmstudios.corvallistransit.utils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.activities.MainActivity;

import java.util.Calendar;

/**
 * Container for various system utilities such as Notifications, Timers, Location, etc.
 */
public class SystemUtils {
    private static final int millisecondMultiplierForMinutes = 60000;

    public static boolean doArrivalsErrorDialogSetup(final Context context) {
        if (context == null) {
            return false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.error_msg_title);
        builder.setMessage(R.string.error_msg);
        builder.setNeutralButton(R.string.error_msg_okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

        return true;
    }

    /**
     * Based on the user's selection, sets an Alarm to wake up their device
     * after a certain time.
     */
    public static void doNotificationBusiness(int hour, int minute, int id, final Context context) {
        int delay = id * millisecondMultiplierForMinutes;

        SharedPreferences sp = context.getSharedPreferences("alerts", Context.MODE_PRIVATE);
        final Calendar c = Calendar.getInstance();
        int calendarHour = c.get(Calendar.HOUR_OF_DAY);
        int calendarMinute = c.get(Calendar.MINUTE);
        int calendarSeconds = c.get(Calendar.SECOND);

        calendarHour += hour % 24;
        calendarMinute += minute % 60;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("reminder_intent", 1);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("time", calendarHour + ":" + calendarMinute);
        editor.putString("reminder_intent", intent.toUri(Intent.URI_INTENT_SCHEME));
        editor.apply();

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delay - (calendarSeconds * 1000), alarmIntent);
    }

    /**
     * Vibrates a user's phone for 1 second 5 times.
     */
    public static void doVibrate(Context context) {
        if (context != null) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            long[] pattern = {0, 1000, 200, 1000, 200, 1000, 200, 1000, 200, 1000, 200};

            // don't repeat the alarm yo
            v.vibrate(pattern, -1);
        }
    }

    /**
     * Builds a local notification and performs the notify() operation.
     *
     * @param context The Application Contex.
     */
    public static void notifyPhone(Context context) {
        if (context != null) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(context.getString(R.string.notif_title))
                            .setContentText(context.getString(R.string.notif_content_text));

            Intent resultIntent = new Intent(context, MainActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Set the notification to clear once the user uses it to go to the main activity.
            Notification n = mBuilder.build();
            n.flags = Notification.FLAG_AUTO_CANCEL;

            mNotificationManager.notify(0, n);
        }
    }
}
