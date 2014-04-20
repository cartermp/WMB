package com.jmstudios.corvallistransit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jmstudios.corvallistransit.models.Stop;

import java.util.List;

/**
 * Adapter for displaying route info.
 */
public class RouteAdapter extends ArrayAdapter<Stop> {
    private final Activity mContext;
    private final List<Stop> mStops;

    private static final int milisecondMultiplierForMinutes = 60000;

    public RouteAdapter(Activity context, List<Stop> stops) {
        super(context, R.layout.fragment_main, stops);
        mContext = context;
        mStops = stops;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.fragment_main, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.stopView = (TextView) rowView.findViewById(R.id.stop_text);
            viewHolder.etaView = (TextView) rowView.findViewById(R.id.eta_text);
            viewHolder.timerView = (TextView) rowView.findViewById(R.id.timer);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Stop stop = mStops.get(position);

        holder.stopView.setText(stop.name);
        holder.etaView.setText(stop.eta() + "m");
        holder.timerView.setText(R.string.timer);

        stop.getScheduledTime(holder.etaView);

        holder.timerView.setClickable(true);
        holder.timerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTimerSetup();
            }
        });

        return rowView;
    }

    private void doTimerSetup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(R.string.timer);
        builder.setItems(R.array.timer_options,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doNotificationBusiness(id);
                        dialog.cancel();
                    }
                }
        );

        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void doNotificationBusiness(int id) {
        int delay;

        switch (id) {
            case 0:
                delay = milisecondMultiplierForMinutes;
                break;
            case 1:
                delay = 5 * milisecondMultiplierForMinutes;
                break;
            case 2:
                delay = 10 * milisecondMultiplierForMinutes;
                break;
            case 3:
                delay = 15 * milisecondMultiplierForMinutes;
                break;
            default:
                delay = 0;
                break;
        }

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                buildNotification();
                doVibrate();
            }
        }, delay);
    }

    private void doVibrate() {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        long[] pattern = { 0, 1000, 200, 1000, 200, 1000, 200, 1000, 200, 1000 };

        // -1 as the second parameter allows it to follow the pattern once
        v.vibrate(pattern, -1);
    }

    private void buildNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Wheres My Bus")
                        .setContentText("Get to your bus stop!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

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
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Set the notification to clear once the user uses it to go to the main activity.
        Notification n = mBuilder.build();
        n.flags = Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, n);
    }

    static class ViewHolder {
        public TextView stopView;
        public TextView etaView;
        public TextView timerView;
    }
}
