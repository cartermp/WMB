package com.jmstudios.corvallistransit.models;

import org.joda.time.Period;

import java.util.Comparator;

public class BusStopComparer implements Comparator<Stop> {
    @Override
    public int compare(Stop b1, Stop b2) {
        int diff;

        if (b1.eta() == b2.eta()) {
            Period period = new Period(b1.expectedTime, b2.expectedTime);
            diff = period.getSeconds();
        } else {
            diff = b1.eta() - b2.eta();
        }

        return (diff > 0) ? 1 : ((diff == 0) ? 0 : -1);
    }
}
