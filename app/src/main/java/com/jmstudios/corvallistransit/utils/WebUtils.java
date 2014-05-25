package com.jmstudios.corvallistransit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.models.Stop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WebUtils {
    /**
     * Launches an Alert Dialog to let the user know their connection is bad.
     */
    public static void launchCheckConnectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(context.getString(R.string.no_internet))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing; this will just bring us back to the main view
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    /**
     * Returns String contents of data downloaded from a URL.
     */
    public static String downloadUrl(String theUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(theUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* 10 seconds */);
            conn.setConnectTimeout(15000 /* 15 seconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();

            return readIt(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Reads an input stream and converts it to a string.
     */
    public static String readIt(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bfr = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bfr.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    /**
     * Converts a list of Stops into a CSV of Stop Ids.
     * <p/>
     * Format: "1234,1234,1234,1234"
     */
    public static String stopsToIdCsv(List<Stop> stops) {
        if (stops == null || stops.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Stop stop : stops) {
            if (stop != null) {
                sb.append(stop.id);
                sb.append(",");
            }
        }

        String url = sb.toString();
        return url.substring(0, url.length() - 1);
    }
}
