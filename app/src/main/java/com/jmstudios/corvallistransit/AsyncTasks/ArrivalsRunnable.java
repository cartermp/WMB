package com.jmstudios.corvallistransit.AsyncTasks;

import com.jmstudios.corvallistransit.interfaces.ArrivalsSliceParsed;
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

public class ArrivalsRunnable implements Runnable {
    private static final String arrivalsUrl =
            "http://www.corvallis-bus.appspot.com/arrivals?stops=";
    List<Stop> mStopSlice;
    private String mRouteName;
    private ArrivalsSliceParsed mListener;

    public ArrivalsRunnable(ArrivalsSliceParsed listener,
                            List<Stop> stopSlice, String routeName) {
        mStopSlice = stopSlice;
        mRouteName = routeName;
        mListener = listener;
    }

    @Override
    public void run() {
        String url = arrivalsUrl + WebUtils.stopsToIdCsv(mStopSlice);

        mStopSlice = getArrivalsData(url, mStopSlice);

        Collections.sort(mStopSlice, new BusStopComparer());

        mListener.onSliceParsed(mStopSlice);
    }

    private List<Stop> getArrivalsData(String url, List<Stop> stopsWithoutArrival) {
        if (stopsWithoutArrival == null) {
            return null;
        }

        List<Stop> stopsWithArrival = new ArrayList<Stop>();

        try {
            String json = WebUtils.downloadUrl(url);
            stopsWithArrival = parseStopArrivals(json, stopsWithoutArrival);
        } catch (IOException e) {
            // do some error-handling here eventually
        }

        return stopsWithArrival;
    }

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
            // do some error-handling here eventually
        }

        return stopsWithoutArrival;
    }
}
