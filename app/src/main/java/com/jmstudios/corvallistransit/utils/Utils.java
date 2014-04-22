package com.jmstudios.corvallistransit.utils;

import org.joda.time.DateTime;

import java.util.HashMap;

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
}
