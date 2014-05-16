package com.jmstudios.corvallistransit.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jmstudios.corvallistransit.AsyncTasks.ArrivalsTask;
import com.jmstudios.corvallistransit.AsyncTasks.RoutesTask;
import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.interfaces.RouteTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;

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
        if (activity == null) {
            Log.d("RouteViewFrag", "mParentActivity null from getEtasForRoute!");
        } else {
            Log.d("RouteViewFrag", "mParentActivity is not null when getting Arrivals!");
        }

        new ArrivalsTask(activity, route, listener, fromSwipe)
                .execute(route.stopList);
    }
}
