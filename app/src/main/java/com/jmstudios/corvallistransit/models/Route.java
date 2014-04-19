package com.jmstudios.corvallistransit.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

public class Route {
    public String name;
    public List<Stop> stopList;
    public DateTime lastUpdated;

    public String LastUpdatedDisplay() {
        DateTimeZone dtz = DateTimeZone.forID("America/Los_Angeles");
        return lastUpdated.withZone(dtz).toString("HH:mm");
    }
}
