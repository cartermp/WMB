package com.jmstudios.corvallistransit;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String routeColor;

    public RouteAdapter(Activity context, List<Stop> stops, String color) {
        super(context, R.layout.fragment_main, stops);
        mContext = context;
        mStops = stops;
        routeColor = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        Stop stop = mStops.get(position);

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.fragment_main, null);

            //Adds the background color for the current route
            if(routeColor != null && rowView!=null)
                rowView.setBackgroundColor(Color.parseColor("#"+routeColor));
            else if(rowView != null)
                rowView.setBackgroundColor(Color.parseColor("#000000"));

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.stopView = (TextView) rowView.findViewById(R.id.stop_text);

            CardHeader ch = new CardHeader(mContext);
            ch.setTitle(stop.eta() + " mins away");

            Card card = new Card(mContext);
            card.addCardHeader(ch);

            card.setTitle("Click for Map");

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(mContext, "Map goes here!", Toast.LENGTH_LONG).show();
                }
            });

            viewHolder.cardView = (CardView) rowView.findViewById(R.id.stop_card);
            viewHolder.cardView.setCard(card);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.stopView.setText(stop.name);
        holder.stopView.setTextColor(Color.WHITE);

        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        // Since we don't want the list items themselves clickable, always return false
        return false;
    }


    static class ViewHolder {
        public TextView stopView;
        public CardView cardView;
    }
}
