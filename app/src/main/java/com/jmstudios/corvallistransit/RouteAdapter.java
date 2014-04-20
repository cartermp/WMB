package com.jmstudios.corvallistransit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
                        Toast.makeText(mContext, "HEY MAN", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
        );

        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    static class ViewHolder {
        public TextView stopView;
        public TextView etaView;
        public TextView timerView;
    }
}
