package com.jmstudios.corvallistransit.models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmstudios.corvallistransit.R;

import it.gmariotti.cardslib.library.internal.CardExpand;


public class RouteExpandCard extends CardExpand {
    private Stop stop;
    private TextView latitudeText;
    private TextView longitudeText;

    public RouteExpandCard(Context context, Stop stop) {
        super(context, R.layout.route_card_expand);
        this.stop = stop;
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view != null) {
            latitudeText = (TextView) view.findViewById(R.id.card_latitude);
            longitudeText = (TextView) view.findViewById(R.id.card_longitude);

            setData();
        }
    }

    private void setData() {
        if (stop != null) {
            if (latitudeText != null) {
                latitudeText.setText("Latitude: " + stop.latitude);
            }

            if (longitudeText != null) {
                longitudeText.setText("Longitude: " + stop.longitude);
            }
        }
    }
}
