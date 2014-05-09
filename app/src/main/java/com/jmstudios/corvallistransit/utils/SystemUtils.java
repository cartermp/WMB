package com.jmstudios.corvallistransit.utils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.activities.MainActivity;

/**
 * Container for various system utilities such as Notifications, Timers, Location, etc.
 */
public class SystemUtils {
    private static final int millisecondMultiplierForMinutes = 60000;

    /**
     * Sets up a Dialog to allow the user to set a timer to remind them
     * to catch their bus.
     *
     * @param context Activity to have this pop up on.
     */
    public static boolean doAlertDialogTimerSetup(final Context context) {
        if (context == null) {
            return false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.timer);
        builder.setItems(R.array.timer_options,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doNotificationBusiness(id, context);
                        dialog.cancel();
                    }
                }
        );

        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

        return true;
    }

    /**
     * Based on the user's selection, sets an Alarm to wake up their device
     * after a certain time (5, 10, 15, or 20 minutes).
     */
    private static void doNotificationBusiness(int id, final Context context) {
        int delay;

        switch (id) {
            case 0:
                delay = millisecondMultiplierForMinutes;
                break;
            case 1:
                delay = 5 * millisecondMultiplierForMinutes;
                break;
            case 2:
                delay = 10 * millisecondMultiplierForMinutes;
                break;
            case 3:
                delay = 15 * millisecondMultiplierForMinutes;
                break;
            default:
                delay = 0;
                break;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delay, alarmIntent);
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
                            .setContentTitle("Corvallis Transit")
                            .setContentText("Get to your bus stop!");

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, MainActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
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
