package com.jmstudios.corvallistransit.utils;

import com.google.android.gms.maps.model.LatLng;
import com.jmstudios.corvallistransit.models.Stop;

import org.joda.time.DateTime;

import java.util.ArrayList;
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
            for (Iterator<Stop> iterator = stops.iterator(); iterator.hasNext(); ) {
                Stop s = iterator.next();
                if (s.eta() < 1 || s.eta() > 30) {
                    iterator.remove();
                }
            }
        }

        LinkedHashSet<Stop> lhs = new LinkedHashSet<Stop>(stops);
        stops.clear();
        stops.addAll(lhs);

        return stops;
    }

    /**
     * Guess what this does.
     */
    public static List<Stop> getStopRange(List<Stop> stops, int start, int end) {
        return stops.subList(start, stops.size() < end ? stops.size() : end);
    }

    /**
     * Decodes a JSON-formatted string of encoded lat/long points.
     * <p/>
     * Solution taken from:
     * http://stackoverflow.com/questions/15924834/decoding-polyline-with-new-google-maps-api
     * <p/>
     * See for more info:
     * https://developers.google.com/maps/documentation/utilities/polylinealgorithm?csw=1
     *
     * @param polyLine A JSON-string encoded set of lat/long points.
     * @return A list of lat/long points to be used for a map.
     */
    public static List<LatLng> decodePolyLine(String polyLine) {
        List<LatLng> points = new ArrayList<LatLng>();

        int index = 0, len = polyLine.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;

            do {
                b = polyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = polyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            points.add(p);
        }

        return points;
    }
}
