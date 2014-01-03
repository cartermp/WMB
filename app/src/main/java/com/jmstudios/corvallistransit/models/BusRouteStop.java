package com.jmstudios.corvallistransit.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by Phillip on 12/27/13.
 */
public class BusRouteStop {
    public String name;
    public String stopTag;
    public BusStop stopModel;
    public String routeNo;
    public int eta;
    public int stopPosition;
    public boolean suspiciousTime;
    public DateTime lastUpdated;

    public String LastUpdatedDisplay() {
        DateTimeZone dtz = DateTimeZone.forID("America/Los_Angeles");
        return lastUpdated.withZone(dtz).toString("HH:mm");

    }

    /*
     * Overridden equals() and hashCode() here for comparison purposes.
     */

    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof BusStop)
                && ((BusStop) obj).address.equals(this.stopModel.address);
    }

    @Override
    public int hashCode() {
        return stopModel.address.hashCode();
    }
}
