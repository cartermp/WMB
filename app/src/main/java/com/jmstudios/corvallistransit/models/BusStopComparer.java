package com.jmstudios.corvallistransit.models;

import org.joda.time.Period;

import java.util.Comparator;

/**
 * Compared two stop arrivals by the ETA in minutes.
 * <p/>
 * If the minutes end up being within a range defined by epsilon,
 * we compare by seconds.
 */
public class BusStopComparer implements Comparator<Stop> {

    @Override
    public int compare(Stop b1, Stop b2) {
        int diff;
        double epsilon = 0.01;

        if (Math.abs(b1.eta() - b2.eta()) < epsilon) {
            Period period = new Period(b1.expectedTime, b2.expectedTime);
            diff = period.getSeconds();
        } else {
            diff = b1.eta() - b2.eta();
        }

        return (diff > 0) ? 1 : ((diff == 0) ? 0 : -1);
    }
}
