package com.jmstudios.corvallistransit;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class RouteViewFragment extends ListFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PullToRefreshLayout mPullToRefreshLayout;

    public RouteViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RouteViewFragment newInstance(int sectionNumber) {
        RouteViewFragment fragment = new RouteViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int routeIndex = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
        List<Route> routes = MainActivity.mRoutes;
        Route route = (routes != null && routes.size() > routeIndex)
                ? routes.get(routeIndex) : null;

        RouteAdapter adapter = new RouteAdapter(getActivity(),
                (route == null) ? new ArrayList<Stop>() : route.stopList);

        setListAdapter(adapter);
    }

    /**
     * On this override, we replace the system list with our own.  This enables us
     * to call setListShown() and other methods.  This is a necessity due to a bug
     * that currently persists in Android.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = (ListView) layout.findViewById(android.R.id.list);
        ViewGroup parent = (ViewGroup) lv.getParent();

        // Remove ListView and add CustomView  in its place
        int lvIndex = parent.indexOfChild(lv);
        parent.removeViewAt(lvIndex);

        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.route_list, container, false);

        parent.addView(mLinearLayout, lvIndex, lv.getLayoutParams());

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;

        mPullToRefreshLayout = (new PullToRefreshLayout(viewGroup.getContext()));

        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create()
                        .scrollDistance(.50f)
                        .build())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list, android.R.id.empty)
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        doRefresh();
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    /**
     * Performs the refresh via a quick AsyncTask.  This AsyncTask
     * invokes the route/eta refresh on the main thread.
     */
    private void doRefresh() {
        setListShown(false);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                /*
                 * Literally sleep here just so the user thinks it's doing something
                 * in the case where a connection is so fast that the update is instant.
                 */
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.retrieveAllRoutes();
                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                mPullToRefreshLayout.setRefreshComplete();

                if (getView() != null) {
                    setListShown(true);
                }
            }
        }.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
