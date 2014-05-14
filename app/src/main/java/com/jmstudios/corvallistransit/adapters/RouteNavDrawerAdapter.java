package com.jmstudios.corvallistransit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.utils.Utils;

public class RouteNavDrawerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;
    private int mTextViewResourceId;
    private String[] mRouteNames;

    public RouteNavDrawerAdapter(Context context, int resource,
                                 int textViewResourceId, String[] routeNames) {
        super(context, resource, textViewResourceId, routeNames);

        mContext = context;
        mResource = resource;
        mTextViewResourceId = textViewResourceId;
        mRouteNames = routeNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        String routeName = mRouteNames[position];

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, null);

            ViewHolder holder = new ViewHolder();

            holder.routeNameView = (TextView) rowView.findViewById(mTextViewResourceId);
            holder.routeColorStrip = rowView.findViewById(R.id.route_color_strip);

            rowView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.routeNameView.setText(routeName);
        holder.routeColorStrip.setBackgroundColor(Color.parseColor(Utils.routeColors[position]));

        return rowView;
    }

    static class ViewHolder {
        public TextView routeNameView;
        public View routeColorStrip;
    }
}
