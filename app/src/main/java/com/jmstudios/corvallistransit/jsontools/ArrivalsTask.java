package com.jmstudios.corvallistransit.jsontools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.models.BusStopComparer;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.WebUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrivalsTask extends AsyncTask<List<Stop>, Void, List<Stop>> {
    private static final String arrivalsUrl = "http://www.corvallis-bus.appspot.com/arrivals?stops=";
    private static final String arrivalsLogTag = "ArrivalsTask";
    private static String mRouteName;
    private ArrivalsTaskCompleted listener;
    private ProgressDialog progressDialog;
    private boolean mIsFromSwipeDown;

    public ArrivalsTask(Context context, String routeName,
                        ArrivalsTaskCompleted listener, boolean fromSwipe) {
        mRouteName = routeName;
        this.listener = listener;
        if (!mIsFromSwipeDown) {
            progressDialog = new ProgressDialog(context);
        }
        mIsFromSwipeDown = fromSwipe;
    }

    @Override
    protected void onPreExecute() {
        if (!mIsFromSwipeDown) {
            progressDialog.setMessage("Getting Eta info...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    protected List<Stop> doInBackground(List<Stop>... stupidSyntaxStops) {
        if (stupidSyntaxStops == null || stupidSyntaxStops[0] == null
                || stupidSyntaxStops[0].isEmpty()) {
            return null;
        }

        List<Stop> stops = stupidSyntaxStops[0];

        String url = arrivalsUrl + WebUtils.stopsToIdCsv(stops);

        return getArrivalsData(url, stops);
    }

    @Override
    protected void onPostExecute(List<Stop> stopsWithArrival) {
        if (!mIsFromSwipeDown && progressDialog.isShowing()) {
            progressDialog.hide();
        }

        // Sort by ETA first; we want to limit computation on the UI thread,
        // hence we do it here rather than there.
        Collections.sort(stopsWithArrival, new BusStopComparer());

        // Send the data off to the receiver - in this case,
        // it's the RouteViewFragment.
        listener.onArrivalsTaskCompleted(stopsWithArrival);
    }

    private List<Stop> getArrivalsData(String url, List<Stop> stopsWithoutArrival) {
        List<Stop> stopsWithArrival = new ArrayList<Stop>();
        try {
            String json = WebUtils.downloadUrl(url);
            stopsWithArrival = parseStopArrivals(json, stopsWithoutArrival);
        } catch (IOException e) {
            if (e.getMessage() != null) {
                Log.d(arrivalsLogTag, e.getMessage());
            }
        }

        return stopsWithArrival;
    }

    /**
     * TODO - this has a bug or bugs in that it is not getting all the info we need.
     */
    private List<Stop> parseStopArrivals(String json, List<Stop> stopsWithoutArrival) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            boolean foundStop;

            for (Stop s : stopsWithoutArrival) {
                foundStop = false;

                String id = String.valueOf(s.id);

                if (jsonObject.has(id)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(id);
                    int len = jsonArray.length();

                    if (len > 0) {
                        for (int i = 0; i < len && !foundStop; i++) {
                            JSONObject jobj2 = jsonArray.getJSONObject(i);

                            String jsonRouteName = jobj2.getString("Route");
                            if (mRouteName.equals(jsonRouteName.trim())) {
                                s.expectedTimeString = jobj2.getString("Expected");
                                s.expectedTime = s.getScheduledTime();

                                foundStop = true;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.d(arrivalsLogTag, e.getMessage());
        }

        return stopsWithoutArrival;
    }
}
