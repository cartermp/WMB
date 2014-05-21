package com.jmstudios.corvallistransit.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.models.Stop;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Adapter for displaying route info.
 */
public class RouteAdapter extends ArrayAdapter<Stop> {
    private final Activity mContext;
    private final List<Stop> mStops;
    private MapListenerCallbacks mCallbacks;

    public RouteAdapter(Activity context, MapListenerCallbacks listener,
                        List<Stop> stops) {
        super(context, R.layout.fragment_main, stops);
        mContext = context;
        mStops = stops;
        mCallbacks = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        Stop stop = mStops.get(position);

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.fragment_main, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.stopView = (TextView) rowView.findViewById(R.id.stop_text);

            Card card = setUpCard(stop);

            viewHolder.cardView = (CardView) rowView.findViewById(R.id.stop_card);
            viewHolder.cardView.setCard(card);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Card card = holder.cardView.getCard();
        CardHeader header = card.getCardHeader();

        if (!setEta(stop, header)) {
            holder.cardView.refreshCard(card);
            holder.stopView.setText(stop.name);
            holder.stopView.setTextColor(Color.WHITE);
        } else {
            holder.cardView.setVisibility(View.GONE);
            holder.stopView.setVisibility(View.GONE);
        }

        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        // Since we don't want the list items themselves clickable, always return false
        return false;
    }

    private Card setUpCard(final Stop s) {
        CardHeader ch = new CardHeader(mContext);
        Card card = new Card(mContext);
        card.addCardHeader(ch);

        card.setTitle("Press for Map");

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                if (mCallbacks != null) {
                    mCallbacks.onEtaCardClick(s.latitude, s.longitude);
                }
            }
        });

        return card;
    }

    private boolean setEta(Stop stop, CardHeader header) {
        boolean pastDue = false;
        int eta = stop.eta();

        header.setTitle(stop.etaText());

        if (eta < 0) {
            pastDue = true;
        }

        return pastDue;
    }


    /**
     * Used as a callback to the map view
     */
    public static interface MapListenerCallbacks {
        /**
         * When invoked, moves the camera to the Stop the user clicked on.
         */
        void onEtaCardClick(double lat, double lng);
    }

    static class ViewHolder {
        public TextView stopView;
        public CardView cardView;
    }
}
