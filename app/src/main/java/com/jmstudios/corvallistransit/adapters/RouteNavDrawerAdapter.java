package com.jmstudios.corvallistransit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RouteNavDrawerAdapter extends ArrayAdapter<String> {
    private static String[] routeColors = new String[]{
            "#00ADEF",
            "#88279F",
            "#F2652F",
            "#8CC530",
            "#BD5590",
            "#034DAF",
            "#f14a43",
            "#00854F",
            "#3cb50c",
            "#FFAA0F",
            "#005BEF",
            "#61463F",
            "#0076AF",
            "#bb0a58",
            "#3F288F",
    };
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

            holder.routeNameView.setText(routeName);

            holder.routeNameView.setBackgroundColor(Color.parseColor(routeColors[position]));

            rowView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.routeNameView.setText(routeName);

        holder.routeNameView.setBackgroundColor(Color.parseColor(routeColors[position]));

        return rowView;
    }

    static class ViewHolder {
        public TextView routeNameView;
    }
}
