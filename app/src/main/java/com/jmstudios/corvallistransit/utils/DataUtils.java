package com.jmstudios.corvallistransit.utils;

import android.app.Activity;
import android.content.Context;

import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.interfaces.RouteTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.tasks.ArrivalsTask;
import com.jmstudios.corvallistransit.tasks.RoutesTask;

public class DataUtils {

    /**
     * Gets all routes, with their stops, and associated metadata.
     */
    public static void retrieveAllRoutes(RouteTaskCompleted listener, Context context, boolean fromSwipe) {
        new RoutesTask(listener, context, fromSwipe).execute();
    }

    /**
     * Given a route, gets arrival data for all of its stops.
     */
    public static void getEtasForRoute(Activity activity, ArrivalsTaskCompleted listener,
                                       Route route, boolean fromSwipe) {
        new ArrivalsTask(activity, route, listener, fromSwipe)
                .execute(route.stopList);
    }
}
