package com.jmstudios.corvallistransit.models;

import com.jmstudios.corvallistransit.jsontools.RetrieveJson;
import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Stop {
    public String name;
    public String road;
    public double bearing;
    public boolean adherehancePoint;
    public double latitude;
    public double longitude;
    public int id;
    public double distance;

    public Route route;
    public DateTime expectedTime;
    private DateTime scheduledTime;

    public int eta() {
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

    public DateTime getScheduledTime()
    {
        if(scheduledTime == null)
        {
            RetrieveJson rt = new RetrieveJson(new String[]{"Expected"},Integer.toString(id), null, null)
            {
                @Override
                public void onResponseReceived(Set s)
                {}
            };
            try
            {
                String rtVal = rt.execute("http://www.corvallis-bus.appspot.com/arrivals?stops=" + Integer.toString(id)).get();
                Set ts = rt.fetchResultsManually(rtVal);
                Iterator i = ts.iterator();
                while(i.hasNext())
                {
                    HashMap<String, String> hm = (HashMap)i.next();
                    scheduledTime = Utils.convertToDateTime(hm.get("Expected"));
                    break;
                }
            }
            catch(Exception e)
            {
                System.out.println("Error attempting to fetch the DATE in Stop.java");
                e.printStackTrace();
            }
        }

        return scheduledTime;
    }
}
