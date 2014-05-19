package com.jmstudios.corvallistransit.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;
import com.jmstudios.corvallistransit.R;

/**
 * Created by Bfriedman on 5/13/14.
 */
public abstract class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int hour = 0;
        int minute = 0;

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, hour, minute, true);
        tpd.setMessage(getString(R.string.set_time_from_now));
        return tpd;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        onTimeReceived( hourOfDay, minute);
    }

    public abstract void onTimeReceived(int hour, int minute);
}
