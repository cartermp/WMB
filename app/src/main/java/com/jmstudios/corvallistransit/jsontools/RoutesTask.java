package com.jmstudios.corvallistransit.jsontools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    private RouteTaskCompleted listener;
    private ProgressDialog progressDialog;

    public RoutesTask(RouteTaskCompleted listener, Context context) {
        this.listener = listener;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Fetching routes...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
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

    @Override
    protected void onPostExecute(List<Route> routes) {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }

        listener.onRoutesTaskCompleted(routes);
    }

    private String getRouteData() {
        String json = "";

        try {
            json = WebUtils.downloadUrl(routesUrl);
        } catch (IOException e) {
            Log.d(logTag, e.getMessage());
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
            stop.id = jobj.getInt("ID");

            stops.add(stop);
        }

        return stops;
    }
}
