package com.jmstudios.corvallistransit.utils;

import com.google.android.gms.maps.model.LatLng;
import com.jmstudios.corvallistransit.models.Stop;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class Utils {
    private static final HashMap<String, Integer> monthPairs;

    static {
        monthPairs = new HashMap<String, Integer>();
        monthPairs.put("Jan", 1);
        monthPairs.put("Feb", 2);
        monthPairs.put("Mar", 3);
        monthPairs.put("Apr", 4);
        monthPairs.put("May", 5);
        monthPairs.put("Jun", 6);
        monthPairs.put("Jul", 7);
        monthPairs.put("Aug", 8);
        monthPairs.put("Sep", 9);
        monthPairs.put("Oct", 10);
        monthPairs.put("Nov", 11);
        monthPairs.put("Dec", 12);
    }

    public static String[] routeColors = new String[]{
            "#00ADEF",
            "#88279F",
            "#F2652F",
            "#8CC530",
            "#BD5590",
            "#034DAF",
            "#f14a43",
            "#00854F",
            "#3cb50c",
            "#FFAA0F",
            "#005BEF",
            "#61463F",
            "#0076AF",
            "#bb0a58",
            "#3F288F",
    };

    /**
     * Given a Stringified Date in RFC822Z format,
     * converts it to a NodaTime DateTime object.
     *
     * @param ctsDateString RFC822Z format date string.
     */
    public static DateTime convertToDateTime(String ctsDateString) {
        String[] data = ctsDateString.split("\\s+");
        String[] timeData = data[3].split(":");

        /* Example: "15 Apr 14 16:57 -0700" */
        int year = Integer.parseInt("20" + data[2]);
        int monthOfYear = monthPairs.get(data[1]);
        int dayOfMonth = Integer.parseInt(data[0]);
        int hourOfDay = Integer.parseInt(timeData[0]);
        int minuteOfHour = Integer.parseInt(timeData[1]);

        return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
    }

    public static List<Stop> filterTimes(List<Stop> stops) {
        if (stops != null) {
            LinkedHashSet<Stop> lhs = new LinkedHashSet<Stop>(stops);

            for (Iterator<Stop> iterator = lhs.iterator(); iterator.hasNext(); ) {
                Stop s = iterator.next();
                if (s.eta() < 1 || s.eta() > 30) {
                    iterator.remove();
                }
            }

            stops.clear();
            stops.addAll(lhs);
        }

        return stops;
    }

    /**
     * Guess what this does.
     */
    public static List<Stop> getStopRange(List<Stop> stops, int start, int end) {
        if (stops == null || stops.isEmpty()) {
            return stops;
        }

        return stops.subList(start, stops.size() < end ? stops.size() : end);
    }

    /**
     * Finds the index in the list of stops whose location
     */
    public static int findStopByLocation(List<Stop> stops, LatLng location) {
        if (stops == null || stops.isEmpty()) {
            return -1;
        }

        int i;
        int size = stops.size();

        for (i = 0; i < size; i++) {
            Stop s = stops.get(i);
            if (s != null && locationsEqual(s.latitude, location.latitude,
                    s.longitude, location.longitude)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean locationsEqual(double lat1, double lat2, double long1, double long2) {
        if (Math.abs(lat1 - lat2) >= 0.000001) {
            return false;
        }

        if (Math.abs(long1 - long2) >= 0.000001) {
            return false;
        }

        return true;
    }

    public static int getCurrentDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK);
    }
}
