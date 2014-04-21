package com.jmstudios.corvallistransit.models;

import android.widget.TextView;

import com.jmstudios.corvallistransit.MainActivity;
import com.jmstudios.corvallistransit.jsontools.RetrieveJson;
import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;

public class Stop {
    public String name;
    public String road;
    public double bearing;
    public boolean adherehancePoint;
    public double latitude;
    public double longitude;
    public int id;
    //public double distance;
    //public Route route;
    public DateTime expectedTime;
    //private DateTime scheduledTime;

    public int eta()
    {
        Period period = new Period(DateTime.now(), this.expectedTime);
        int eta =  period.getMinutes();
        return (eta >= 1) ? eta : 1;
    }
    /*
     * Overridden equals() and hashCode() here for comparison purposes.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof Stop)
                && ((Stop) obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public DateTime getScheduledTime(CardView tv)
    {
        if(expectedTime == null)
        {
            final CardView innerView = tv;
            RetrieveJson rt = new RetrieveJson( MainActivity.context,new String[]{"Expected"},Integer.toString(id), null, null)
            {
                @Override
                public void onResponseReceived(Set s)
                {
                    Iterator i = s.iterator();
                    while(i.hasNext())
                    {
                        HashMap<String, String> hm = (HashMap)i.next();
                        expectedTime = Utils.convertToDateTime(hm.get("Expected"));

                        Card card = innerView.getCard();
                        if (card.hasHeader()) {
                            CardHeader header = card.getCardHeader();
                            header.setTitle(eta() + "m");
                        }

                        break;
                    }
                }
            };
            rt.execute("http://www.corvallis-bus.appspot.com/arrivals?stops=" + Integer.toString(id));
        }
        return expectedTime;
    }
}
