package com.jmstudios.corvallistransit.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.jmstudios.corvallistransit.activities.MainActivity;
import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.interfaces.RefreshTaskCompleted;
import com.jmstudios.corvallistransit.interfaces.RouteTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.utils.DataUtils;

public class RefreshTask extends AsyncTask<Void, Void, Void> {
    private boolean mFromSwipe;
    private Activity mActivity;
    private Route mRoute;
    private RefreshTaskCompleted mRefreshListener;
    private ArrivalsTaskCompleted mArrivalsListener;

    public RefreshTask(Activity activity, Route route,
                       RefreshTaskCompleted refreshListener,
                       ArrivalsTaskCompleted arrivalsListener,
                       boolean fromSwipe) {
        mActivity = activity;
        mRoute = route;
        mRefreshListener = refreshListener;
        mArrivalsListener = arrivalsListener;
        mFromSwipe = fromSwipe;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final Activity activity = mActivity;

        if (mRoute != null && mRoute.stopsUpToDate()) {
            return null;
        }

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.mRoutes == null || MainActivity.mRoutes.isEmpty()) {
                        DataUtils.retrieveAllRoutes(
                                (RouteTaskCompleted) activity, activity, mFromSwipe);
                    }

                    if (mRoute != null) {
                        DataUtils.getEtasForRoute(mActivity,
                                mArrivalsListener, mRoute, mFromSwipe);
                    }
                }
            });
        }


        /*
         * No heuristic here.  Sleep for a second to give users the impression
         * that it's doing something, since quick refreshes make it look like nothing
         * was updated.
         */
        if (mFromSwipe) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (mRefreshListener != null) {
            mRefreshListener.onRefreshTaskComplete();
        }
    }
}
