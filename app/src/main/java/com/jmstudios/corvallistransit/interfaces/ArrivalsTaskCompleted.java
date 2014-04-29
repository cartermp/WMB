package com.jmstudios.corvallistransit.interfaces;

import com.jmstudios.corvallistransit.models.Stop;

import java.util.List;

/**
 * Callback interface for Stop Arrivals.
 */
public interface ArrivalsTaskCompleted {
    public void onArrivalsTaskCompleted(List<Stop> stopsWithArrival);
}
