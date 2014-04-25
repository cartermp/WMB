package com.jmstudios.corvallistransit.jsontools;

import com.jmstudios.corvallistransit.models.Route;

import java.util.List;

public interface RouteTaskCompleted {
    public void onRoutesTaskCompleted(List<Route> routes);
}
