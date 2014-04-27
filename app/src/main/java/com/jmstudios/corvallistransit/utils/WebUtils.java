package com.jmstudios.corvallistransit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jmstudios.corvallistransit.models.Stop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WebUtils {
    /**
     * Uses the Connectivity Service to check a user's web connection.
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    /**
     * Launches an Alert Dialog to let the user know their connection is bad.
     */
    public static void launchCheckConnectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("No Network Connection!")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public static InputStream getStream(String theUrl) throws IOException {
        InputStream is = null;

        URL url = new URL(theUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(1000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        is = conn.getInputStream();

        return is;
    }

    /**
     * Returns String contents of data downloaded from a URL.
     *
     * @param theUrl
     * @return
     * @throws IOException
     */
    public static String downloadUrl(String theUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(theUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();

            String contentAsString = readIt(is);
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Reads an input stream and converts it to a string.
     *
     * @param stream
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
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
     *
     * @param stops
     * @return
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
