package com.jmstudios.corvallistransit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.jmstudios.corvallistransit.R;

/**
 * Created by Bfriedman on 5/15/14.
 */
public class AlarmManagerFragment extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.alarm_manager_fragment, null);
        builder.setView(v);

        return builder.create();
    }
}
