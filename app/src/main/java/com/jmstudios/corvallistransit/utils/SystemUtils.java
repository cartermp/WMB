package com.jmstudios.corvallistransit.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
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
public class SystemUtils{
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

        /*
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
        */

        return true;
    }

    /**
     * Based on the user's selection, sets an Alarm to wake up their device
     * after a certain time (5, 10, 15, or 20 minutes).
     */
    public static byte doNotificationBusiness(int hour, int minute,int id, final Context context) {
        int delay = id * millisecondMultiplierForMinutes;

        SharedPreferences sp = context.getSharedPreferences("alerts", Context.MODE_PRIVATE);
        byte hasSaved = 0;
        final Calendar c = Calendar.getInstance();
        int calendarHour = c.get(Calendar.HOUR_OF_DAY);
        int calendarMinute = c.get(Calendar.MINUTE);
        int calendarSeconds = c.get(Calendar.SECOND);
        for(int x = 0; x < 1; x++)
        {

            //if(sp.getString("time"+x, "").equals(""))
            //{

                if(calendarHour+hour > 24)
                {
                    calendarHour+=(hour-24);
                }
                else
                {
                    calendarHour+=hour;
                }

                if(calendarMinute+minute>60)
                {
                    calendarMinute+=(minute-60);
                }
                else
                {
                    calendarMinute+=minute;
                }

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("intent"+x, 1);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                //SAVE THIS INTENT
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("time"+x,calendarHour+":"+calendarMinute);
                editor.putString("intent"+x,intent.toUri(Intent.URI_INTENT_SCHEME));
                editor.commit();

                hasSaved = 1;
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + delay - (calendarSeconds * 1000), alarmIntent);
                break;
        }

        return hasSaved;
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
