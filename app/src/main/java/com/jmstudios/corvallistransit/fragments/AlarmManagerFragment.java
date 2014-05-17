package com.jmstudios.corvallistransit.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.models.Alert;
import com.jmstudios.corvallistransit.utils.NotificationReceiver;
import com.jmstudios.corvallistransit.utils.SystemUtils;

import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Bfriedman on 5/15/14.
 */
public class AlarmManagerFragment extends DialogFragment implements View.OnClickListener {
    private Alert primaryAlert;
    private TextView alert_time_text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.alarm_manager_fragment, null);
        builder.setView(v);

        Button setAlert = (Button) v.findViewById(R.id.set_alert);
        setAlert.setOnClickListener(this);

        Button resetAlert = (Button) v.findViewById(R.id.reset_alert);
        resetAlert.setOnClickListener(this);

        alert_time_text = (TextView) v.findViewById(R.id.current_alert);

        updateArrayAdapter();

        return builder.create();
    }

    private Alert retrieveAllItems() {
        Alert foundAlert = null;
        Locale locale = Locale.getDefault();

        SharedPreferences sp = getActivity().getSharedPreferences("alerts", Context.MODE_PRIVATE);
        for (int x = 0; x < 1; x++) {
            String timeKey = "time" + x;
            String intentKey = "intent" + x;

            String time = sp.getString(timeKey, "");
            String intentString = sp.getString(intentKey, "");
            if (!intentString.equals("") && !time.equals("")) {
                Intent intent;
                try {
                    intent = Intent.getIntentOld(intentString);
                } catch (Exception e) {
                    continue;
                }

                Calendar c = Calendar.getInstance();

                int calendarHour = c.get(Calendar.HOUR_OF_DAY);
                int calendarMinute = c.get(Calendar.MINUTE);

                LocalTime localtime = LocalTime.parse(time);
                String localTimeString = localtime.toString("h:mm a", locale);

                //need to add support for month,day,year stuff as well, one fat string we'll parse of course
                boolean isGoodToGo = true;
                if (calendarHour >= localtime.getHourOfDay()) {
                    if (calendarMinute >= localtime.getMinuteOfHour()) {
                        //this notification has expired, remove it
                        isGoodToGo = false;

                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString(timeKey, "");
                        editor.putString(intentKey, "");

                        editor.commit();
                    }
                }

                if (isGoodToGo) {
                    Alert alert = new Alert();
                    alert.alert_time = localTimeString;
                    Intent powerIntent = new Intent(getActivity(), NotificationReceiver.class);
                    powerIntent.putExtra("intent" + x, 1);
                    alert.alert_intent = powerIntent;
                    alert.alert_timeId = timeKey;
                    alert.alert_intentId = intentKey;

                    foundAlert = alert;
                }
            }
        }

        return foundAlert;
    }

    private void updateArrayAdapter() {
        primaryAlert = retrieveAllItems();
        if (primaryAlert != null)
            alert_time_text.setText(primaryAlert.alert_time);
        else
            alert_time_text.setText("00:00");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_alert:
                new TimePickerFragment() {
                    @Override
                    public void onTimeReceived(int hour, int minute) {
                        if (hour + minute > 0) {
                            boolean result = SystemUtils.doNotificationBusiness(hour, minute, hour * 60 + minute, getActivity());
                            if (result) {
                                String pluralOrNot = getString(R.string.time_set_2);
                                if (hour * 60 + minute == 1)
                                    pluralOrNot = getString(R.string.non_plural_set);
                                Toast.makeText(getActivity(), getString(R.string.timer_set_1) + " " + Integer.toString(hour * 60 + minute) + " " + pluralOrNot, Toast.LENGTH_SHORT).show();
                            }

                            updateArrayAdapter();
                        }
                    }
                }.show(getFragmentManager(), "timePicker");
                break;

            case R.id.reset_alert:
                Alert alert = primaryAlert;
                if (alert != null) {
                    Intent intent = alert.alert_intent;
                    intent.putExtra(alert.alert_intentId, 1);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(alarmIntent);

                    SharedPreferences sp = getActivity().getSharedPreferences("alerts", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(alert.alert_timeId, "");
                    editor.putString(alert.alert_timeId, "");
                    editor.commit();

                    updateArrayAdapter();

                    Toast.makeText(getActivity(), "You have deleted your alert for " + alert.alert_time, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    static class Holder {
        View view;
        TextView textView;
    }
}
