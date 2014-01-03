package com.jmstudios.corvallistransit.models;

import java.util.Comparator;

/**
 * Created by Phillip on 12/27/13.
 */
public class BusStopComparer implements Comparator<BusRouteStop> {
    @Override
    public int compare(BusRouteStop b1, BusRouteStop b2) {
        int diff;

        if (b1.eta == b2.eta) {
            diff = b1.stopPosition - b2.stopPosition;
        } else {
            diff = b1.eta - b2.eta;
        }

        return (diff > 0) ? 1 : ((diff == 0) ? 0 : -1);
    }
}
