package com.jmstudios.corvallistransit.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jmstudios.corvallistransit.interfaces.ArrivalsSliceParsed;
import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.models.BusStopComparer;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ArrivalsTask extends AsyncTask<List<Stop>, Void, List<Stop>>
        implements ArrivalsSliceParsed {
    private static Route mRoute;
    private ConcurrentLinkedQueue<Stop> mCclq = new ConcurrentLinkedQueue<Stop>();
    private ArrivalsTaskCompleted listener;
    private ProgressDialog progressDialog;
    private boolean mIsFromSwipeOrLoad;

    public ArrivalsTask(Context context, Route route,
                        ArrivalsTaskCompleted listener, boolean fromSwipeOrLoad) {
        mRoute = route;
        this.listener = listener;
        if (!mIsFromSwipeOrLoad) {
            progressDialog = new ProgressDialog(context);
        }
        mIsFromSwipeOrLoad = fromSwipeOrLoad;
    }

    @Override
    protected void onPreExecute() {
        if (!mIsFromSwipeOrLoad) {
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

        List<List<Stop>> slices = getSlices(stupidSyntaxStops[0]);

        boolean completedInTime = handleSliceThreads(slices);

        if (!completedInTime) {
            return null;
        }

        List<Stop> stopsWithUpdatedTimes = new ArrayList<Stop>(mCclq);

        /*
         * Super fast sorting here.  Each slice is also sorted, so this
         * should take barely any time at all.
         */
        Collections.sort(stopsWithUpdatedTimes, new BusStopComparer());

        /*
         * Need to filter here - can't do it for each thread.  This is
         * because some stops may still end up as duplicates if they exist
         * in different threads.
         */
        stopsWithUpdatedTimes = Utils.filterTimes(stopsWithUpdatedTimes);

        mRoute.lastStopTimeUpdated = DateTime.now();

        return stopsWithUpdatedTimes;
    }

    private boolean handleSliceThreads(List<List<Stop>> slices) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (List<Stop> slice : slices) {
            Runnable worker = new ArrivalsRunnable(this, slice, mRoute.name);
            executorService.execute(worker);
        }

        executorService.shutdown();

        boolean finished = true;

        try {
            // Wait 10 seconds.
            finished = executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // do some error-handling here eventually
        }

        if (!finished) {
            listener.onArrivalsTaskTimeout();
            return false;
        }

        return true;
    }

    private List<List<Stop>> getSlices(List<Stop> stupidSyntaxStop) {
        List<List<Stop>> slices = new ArrayList<List<Stop>>();

        int sliceSize = 10;

        for (int i = 0; i < stupidSyntaxStop.size(); i += sliceSize) {
            slices.add(Utils.getStopRange(stupidSyntaxStop, i, i + sliceSize));
        }
        return slices;
    }

    @Override
    protected void onPostExecute(List<Stop> stopsWithArrival) {
        if (!mIsFromSwipeOrLoad && progressDialog.isShowing()) {
            progressDialog.hide();
        }

        if (stopsWithArrival != null) {
            listener.onArrivalsTaskCompleted(stopsWithArrival);
        }
    }

    @Override
    public void onSliceParsed(List<Stop> slice) {
        if (mCclq != null) {
            mCclq.addAll(slice);
        }
    }

    @Override
    public void onSliceParseFailed() {
        listener.onArrivalsTaskError();
    }
}
