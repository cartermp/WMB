package com.jmstudios.corvallistransit;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jmstudios.corvallistransit.models.BusRouteStop;

import java.util.List;

/**
 * Adapter for displaying route info.
 */
public class RouteAdapter extends ArrayAdapter<BusRouteStop> {
    private final Activity mContext;
    private final List<BusRouteStop> mRoutes;

    public RouteAdapter(Activity context, List<BusRouteStop> routes) {
        super(context, R.layout.fragment_main, routes);
        mContext = context;
        mRoutes = routes;
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

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        BusRouteStop stop = mRoutes.get(position);
        holder.stopView.setText(stop.name);
        holder.etaView.setText(stop.eta + "m");

        return rowView;
    }

    static class ViewHolder {
        public TextView stopView;
        public TextView etaView;
    }
}
