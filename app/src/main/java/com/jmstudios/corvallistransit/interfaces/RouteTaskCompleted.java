package com.jmstudios.corvallistransit.interfaces;

import com.jmstudios.corvallistransit.models.Route;

import java.util.List;

/**
 * Callback Interface for Route retrieval.
 */
public interface RouteTaskCompleted {
    public void onRoutesTaskCompleted(List<Route> routes);

    public void onRoutesTaskTimeout();
}
