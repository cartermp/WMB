package com.jmstudios.corvallistransit.routeTools;

import android.util.Log;

import com.jmstudios.corvallistransit.models.BusRoute;
import com.jmstudios.corvallistransit.models.BusRouteStop;
import com.jmstudios.corvallistransit.models.BusStopComparer;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Thread class for managing route updating.
 */
public class RouteUpdater implements Runnable {
    private static final String TAG = "RouteUpdater";

    private BusRoute mRoute;
    private String mEtaUrl;

    public RouteUpdater(BusRoute route, String etaUrl) {
        mRoute = route;
        mEtaUrl = etaUrl;
    }

    /**
     * Given a route, spawns a thread for each Bus Stop, which will get
     * the ETA for that stop.  Once threading is finished, the bus stops
     * are sorted by ETA.
     */
    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (BusRouteStop stopClosure : mRoute.stopList) {
            Runnable etaUpdater = new EtaUpdater(stopClosure, mEtaUrl);
            executor.execute(etaUpdater);
        }

        executor.shutdown();

        try {
            mRoute.updatedSuccessfully = executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }

        mRoute.lastUpdated = DateTime.now();

        Collections.sort(mRoute.stopList, new BusStopComparer());

        // Filter down to only valid stops (those with an ETA > 0)
        // There are no stops with ETA > 30m
        int start = indexOfFirstEta(mRoute.stopList);
        if (start == -1) {
            mRoute.stopList = new ArrayList<BusRouteStop>();
        } else {
            int end = mRoute.stopList.size() - 1;
            mRoute.stopList = mRoute.stopList.subList(start, end);
        }
    }

    /**
     * Returns the index of the first stop whose eta is not zero.
     *
     * @param stops list of sorted Bus Route Stops
     * @return The index of the first valid ETA; otherwise, -1.
     */
    private static int indexOfFirstEta(List<BusRouteStop> stops) {
        if (stops == null || stops.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i) != null && stops.get(i).eta > 0) {
                return i;
            }
        }

        return -1;
    }
}
