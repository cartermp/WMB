package com.jmstudios.corvallistransit.models;

import java.util.Comparator;

/**
 * Created by Phillip.Carter on 4/24/2014.
 */
public class StopIdComparer implements Comparator<Stop> {
    @Override
    public int compare(Stop stop, Stop stop2) {
        int diff = stop.id = stop2.id;
        return (diff > 0) ? 1 : ((diff < 0) ? -1 : 0);
    }
}
