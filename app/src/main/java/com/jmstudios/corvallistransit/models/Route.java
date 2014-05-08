package com.jmstudios.corvallistransit.models;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

public class Route {
    public String name;
    public List<Stop> stopList;
    public List<LatLng> polyLinePositions;
    public DateTime lastStopTimeUpdated;
    public String polyLine;
    public String color;

    public boolean stopsUpToDate() {
        return lastStopTimeUpdated != null &&
                new Period(DateTime.now(), lastStopTimeUpdated).getMinutes() > 1;
    }
}
