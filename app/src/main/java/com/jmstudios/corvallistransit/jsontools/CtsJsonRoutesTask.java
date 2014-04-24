package com.jmstudios.corvallistransit.jsontools;

import android.os.AsyncTask;
import android.util.Log;

import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.ConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CtsJsonRoutesTask extends AsyncTask<Void, Void, Void> {
    private static final String logTag = "RoutesTask";
    private static final String routesUrl = "http://www.corvallis-bus.appspot.com/routes?stops=true";

    @Override
    protected Void doInBackground(Void... voids) {
        String jsonResult = getRouteData();

        if (jsonResult != null && !jsonResult.equals("")) {
            List<Route> routes = parseRoutes(jsonResult);
            onRoutesReceived(routes);
        } else {
            // We'll just pass an empty list for now.
            // Error-handling can be done in the MainActivity
            onRoutesReceived(new ArrayList<Route>());
        }

        return null;
    }

    private String getRouteData() {
        String json = "";

        try {
            json = ConnectionUtils.downloadUrl(routesUrl);
        } catch (IOException e) {
            Log.d(logTag, "IOException occured when downloading routes!", e);
        }

        return json;
    }

    private List<Route> parseRoutes(String json) {
        List<Route> routes = new ArrayList<Route>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jRoutes = jsonObject.getJSONArray("routes");

            int len = jRoutes.length();
            for (int i = 0; i < len; i++) {
                JSONObject jobj = jRoutes.getJSONObject(i);
                Route route = new Route();

                route.name = jobj.getString("Name");
                route.polyLine = jobj.getString("Polyline");
                route.color = jobj.getString("Color");

                route.stopList = parseStops(jobj);

                routes.add(route);
            }
        } catch (JSONException e) {
            Log.d(logTag, "JSONException occured when getting routes as an array!", e);
        }

        return routes;
    }

    private List<Stop> parseStops(JSONObject jobj) throws JSONException {
        List<Stop> stops = new ArrayList<Stop>();

        JSONArray jsonArray = jobj.getJSONArray("Path");

        int len = jsonArray.length();
        for (int i = 0; i < len; i++) {
            jobj = jsonArray.getJSONObject(i);
            Stop stop = new Stop();

            stop.name = jobj.getString("Name").replace("\u0026", "&");
            stop.road = jobj.getString("Road").replace("\u0026", "&");
            stop.bearing = jobj.getDouble("Bearing");
            stop.adherehancePoint = jobj.getBoolean("AdherancePoint");
            stop.latitude = jobj.getDouble("Lat");
            stop.longitude = jobj.getDouble("Long");

            stops.add(stop);
        }

        return stops;
    }

    /**
     * Our callback method.  MainActivity implements this.
     *
     * @param routes the list of routes to display!
     */
    public abstract void onRoutesReceived(List<Route> routes);
}
