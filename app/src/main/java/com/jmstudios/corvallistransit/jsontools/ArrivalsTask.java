package com.jmstudios.corvallistransit.jsontools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jmstudios.corvallistransit.interfaces.ArrivalsSliceParsed;
import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.models.BusStopComparer;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.ArrivalsRunnable;
import com.jmstudios.corvallistransit.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ArrivalsTask extends AsyncTask<List<Stop>, Void, List<Stop>>
        implements ArrivalsSliceParsed {
    private static String mRouteName;
    private ConcurrentLinkedQueue<Stop> mCclq = new ConcurrentLinkedQueue<Stop>();
    private ArrivalsTaskCompleted listener;
    private ProgressDialog progressDialog;
    private boolean mIsFromSwipeOrLoad;

    public ArrivalsTask(Context context, String routeName,
                        ArrivalsTaskCompleted listener, boolean fromSwipeOrLoad) {
        mRouteName = routeName;
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

        List<List<Stop>> slices = new ArrayList<List<Stop>>();

        for (int i = 0; i < stupidSyntaxStops[0].size(); i += 10) {
            slices.add(Utils.getStopRange(stupidSyntaxStops[0], i, i + 10));
        }

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (List<Stop> slice : slices) {
            Runnable worker = new ArrivalsRunnable(this, slice, mRouteName);
            executorService.execute(worker);
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //whatever
        }

        return new ArrayList<Stop>(mCclq);
    }

    @Override
    protected void onPostExecute(List<Stop> stopsWithArrival) {
        if (!mIsFromSwipeOrLoad && progressDialog.isShowing()) {
            progressDialog.hide();
        }

        // Sort by ETA first; we want to limit computation on the UI thread,
        // hence we do it here rather than there.
        Collections.sort(stopsWithArrival, new BusStopComparer());

        listener.onArrivalsTaskCompleted(stopsWithArrival);
    }

    @Override
    public void onSliceParsed(List<Stop> slice) {
        if (mCclq != null) {
            mCclq.addAll(slice);
        }
    }
}
