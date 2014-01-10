package com.jmstudios.corvallistransit.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

public class BusRoute {
    public String routeNumber;
    public String name;
    public List<BusRouteStop> stopList;
    public int subRouteOrder;
    public boolean routeTimeWarning;
    public boolean updatedSuccessfully;
    public DateTime lastUpdated;

    public String LastUpdatedDisplay() {
        DateTimeZone dtz = DateTimeZone.forID("America/Los_Angeles");
        return lastUpdated.withZone(dtz).toString("HH:mm");
    }
}
