package com.jmstudios.corvallistransit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Tuple;
import com.jmstudios.corvallistransit.routeTools.ConnectionsUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask class which handles getting all route information for the app.
 */
public class ReadRouteInfo extends AsyncTask<List<Route>, Void, Tuple<List<Route>, Boolean>> {

    private static final String TAG = MainActivity.class.getName();
    public static final String CTS_URI = "http://www.corvallistransit.com/rtt/public/utility/file.aspx";
    public static final String PLATFORMS_PARAM = "?contenttype=SQLXML&Name=Platform.rxml&PlatformTag={0}";
    public static final String ROUTES_PARAM = "?contenttype=SQLXML&Name=RoutePattern.rxml";
    public static final String PLATFORM_ETA_PARAM = "?contenttype=SQLXML&Name=RoutePositionET.xml&PlatformTag=";

    private static final int timeout = 20000; // 20 seconds

    private ProgressDialog dialog;

    private Context context;

    public ReadRouteInfo(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    /**
     * Sets up the modal progress dialog so the dreaded ANR does not show!
     */
    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Retrieving route info...");
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
    }

    /**
     * Fills the given list of Bus Routes with route and ETA information.
     *
     * @param lists The list of Bus Routes to populate.
     * @return The new list of Bus Routes with updated ETA and route info.
     */
    @Override
    protected Tuple<List<Route>, Boolean> doInBackground(List<Route>... lists) {
        // If this happens, something is seriously wrong...
        if (lists == null || lists.length == 0) {
            return new Tuple(null, false);
        } else if (lists[0] == null) {
            return new Tuple(lists[0], false);
        }

        boolean updateSucceeded;
        try {
            // Since the list of Bus Routes is stored in phone memory,
            // we won't update route information unless it's empty.
            if (lists[0].isEmpty()) {
                lists[0] = updateRoutes(CTS_URI + ROUTES_PARAM);
            }

            // Because ETA update is supported on-demand, update ETA info every time.
            updateSucceeded = updateRouteEtas(lists[0]);
        } catch (IOException e) {
            return new Tuple(lists[0], false);
        } catch (XmlPullParserException e) {
            return new Tuple(lists[0], false);
        }

        return new Tuple(lists[0], updateSucceeded);
    }

    @Override
    protected void onPostExecute(Tuple<List<Route>, Boolean> updatedTuple) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }

        /*
         * "rhs" is the boolean type here.
         * "lhs" is the list of Bus Routes.
         */
        if (!updatedTuple.rhs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("Error retrieving route info")
                    .setMessage("Check your connection and try again later.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog failedDialog = builder.create();
            failedDialog.show();
        } else {
            // Need to update the UI with the routes
        }
    }

    /**
     * Populates Route Stops with eta info for each Bus Route.
     *
     * @param routes The list of Bus Routes whose stop ETAs will be updated.
     * @return The updated list of Routes.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private boolean updateRouteEtas(List<Route> routes) throws XmlPullParserException, IOException {
        List<Thread> routeUpdaterThreads = new ArrayList<Thread>();
        boolean updatedSuccessfully = true;

//        /*
//         * Threading is done such that each route gets a thread.
//         * This thread handles connecting to the XML feed for each Bus Stop,
//         * and sorts the list of stops by ETA before finishing.
//         */
//        for (Route route : routes) {
//            Thread t = new Thread(new RouteUpdater(route, CTS_URI + PLATFORM_ETA_PARAM));
//            t.start();
//            routeUpdaterThreads.add(t);
//        }
//
//        /*
//         * Wait 20 seconds for each thread to finish; return failure if it couldn't finish in time.
//         */
//        for (Thread t : routeUpdaterThreads) {
//            try {
//                //t.join(20000);
//                t.join();
//            } catch (InterruptedException e) {
//                Log.d(TAG, e.toString());
//                updatedSuccessfully = false;
//            }
//        }

        return updatedSuccessfully;
    }

    /**
     * Generates a list of Bus Routes, each with a list of stops.
     *
     * @param urlString the URL to connect to.
     * @return A list of Bus Routes.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Route> updateRoutes(String urlString) throws XmlPullParserException, IOException {
        InputStream stream;
        List<Route> tempRoutes = new ArrayList<Route>();
//
//        stream = ConnectionsUtils.downloadUrl(urlString);
//        if (stream != null) {
//            tempRoutes.addAll(parser.parseRouteInfo(stream));
//        }

        return tempRoutes;
    }
}
