package com.jmstudios.corvallistransit.models;

import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Stop {
    public String name;
    public String road;
    public double bearing;
    public boolean adherehancePoint;
    public double latitude;
    public double longitude;
    public int id;
    public DateTime expectedTime;
    public String expectedTimeString;

    public int eta() {
        Period period = new Period(DateTime.now(), this.expectedTime);
        int eta = period.getMinutes();
        return (eta >= 1) ? eta : (eta < 1) ? -1 : 1;
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

    public DateTime getScheduledTime() {
        return Utils.convertToDateTime(this.expectedTimeString);
    }
}
