package com.jmstudios.corvallistransit.models;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

public class Route {
    public String name;
    public List<Stop> stopList;
    public List<LatLng> polyLinePositions;
    public DateTime lastUpdated;
    public String polyLine;
    public String color;

    public String LastUpdatedDisplay() {
        DateTimeZone dtz = DateTimeZone.forID("America/Los_Angeles");
        return lastUpdated.withZone(dtz).toString("HH:mm");
    }
}
