package com.jmstudios.corvallistransit.routeTools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class which holds static methods for connecting to some Internet Service.
 */
public class ConnectionsUtils {
    /**
     * Given a URL string, sets up a connection and gets an input stream.
     *
     * @param urlString the URL to connect to.
     * @return A stream to the connection.
     * @throws java.io.IOException
     */
    public static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(20000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        return conn.getInputStream();
    }
}
