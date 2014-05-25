package com.jmstudios.corvallistransit.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.maps.android.PolyUtil;
import com.jmstudios.corvallistransit.interfaces.RouteTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoutesTask extends AsyncTask<Void, Void, List<Route>> {
    private static final String logTag = "RoutesTask";
    private static final String routesUrl = "http://www.corvallis-bus.appspot.com/routes?stops=true";
    private static boolean mFromeSwipe;
    private static boolean timedout = false;
    private RouteTaskCompleted listener;
    private ProgressDialog progressDialog;

    public RoutesTask(RouteTaskCompleted listener, Context context, boolean fromSwipe) {
        mFromeSwipe = fromSwipe;
        this.listener = listener;

        if (!fromSwipe) {
            progressDialog = new ProgressDialog(context);
        }
    }

    @Override
    protected void onPreExecute() {
        if (!mFromeSwipe) {
            progressDialog.setMessage("Fetching routes...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    protected List<Route> doInBackground(Void... voids) {
        String jsonResult = getRouteData();

        List<Route> routes = new ArrayList<Route>();
        if (jsonResult != null && !jsonResult.equals("")) {
            routes = parseRoutes(jsonResult);
        }

        return routes;
    }

    /**
     * Overridden to remove the progressdialog, if it's up,
     * and make the callback to the UI.
     */
    @Override
    protected void onPostExecute(List<Route> routes) {
        if (!mFromeSwipe && progressDialog.isShowing()) {
            progressDialog.hide();
        }

        if (!timedout) {
            listener.onRoutesTaskCompleted(routes);
        } else {
            listener.onRoutesTaskTimeout();
        }
    }

    /**
     * Gets Route data (Name, PolyLine for maps, stop list, etc)
     * as a JSON string.
     */
    private String getRouteData() {
        String json = "";

        try {
            json = WebUtils.downloadUrl(routesUrl);
        } catch (IOException e) {
            timedout = true;
        }

        return json;
    }

    /**
     * Parses the route information JSON string into a list of routes.
     */
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
                route.polyLinePositions = PolyUtil.decode(route.polyLine);
                route.color = jobj.getString("Color");
                route.stopList = parseStops(jobj);
                routes.add(route);
            }
        } catch (JSONException e) {
            Log.d(logTag, "JSONException occurred when getting routes as an array!", e);
        }

        return routes;
    }

    /**
     * For each route, parses the JSON array of stops into a list.
     */
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
            stop.id = jobj.getInt("ID");

            stops.add(stop);
        }

        return stops;
    }
}
