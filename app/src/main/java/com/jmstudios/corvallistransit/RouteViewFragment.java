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

import com.jmstudios.corvallistransit.jsontools.ArrivalsTask;
import com.jmstudios.corvallistransit.jsontools.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.jsontools.RouteTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class RouteViewFragment extends ListFragment implements ArrivalsTaskCompleted {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public List<Stop> stops = new ArrayList<Stop>();
    private PullToRefreshLayout mPullToRefreshLayout;
    private RouteAdapter mAdapter;

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

        Route route = getRoute();

        if (route != null) {
            doRefresh(false);

            if (stops != null && !stops.isEmpty()) {
                setupTheAdapter();
            } else {
                setEmptyText("Nothing to display here!");
            }
        } else {
            setEmptyText("Nothing to display here!");
        }

        setListShownNoAnimation(true);
    }

    /**
     * On this override, we replace the system list with our own.  This enables us
     * to call setListShown() and other methods.  This is a necessity due to a bug
     * that currently persists in Android.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);

        if (layout != null) {
            ListView lv = (ListView) layout.findViewById(android.R.id.list);
            ViewGroup parent = (ViewGroup) lv.getParent();

            if (parent != null) {
                int lvIndex = parent.indexOfChild(lv);
                parent.removeViewAt(lvIndex);

                LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.route_list, container, false);

                if (mLinearLayout != null) {
                    parent.addView(mLinearLayout, lvIndex, lv.getLayoutParams());
                }
            }
        }

        return layout;
    }

    /**
     * Aside from other setup, sets up the ActionBarPullToRefresh service.
     */
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
                        doRefresh(true);
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    /**
     * Fires off the Arrivals AsyncTask to retrieve ETA info for the given route.
     */
    private void getEtasForRoute(final Route route, boolean fromSwipe) {
        new ArrivalsTask(getActivity(), route.name, this, fromSwipe)
                .execute(route.stopList);
    }

    /**
     * Gets the route the user selected from the Navigation Drawer.
     */
    private Route getRoute() {
        Route route = null;

        Bundle args = getArguments();
        if (args != null) {
            int routeIndex = args.getInt(ARG_SECTION_NUMBER) - 1;
            List<Route> routes = MainActivity.mRoutes;

            if (routes != null && routes.size() > routeIndex) {
                route = routes.get(routeIndex);
            }
        }

        return route;
    }

    private void setupTheAdapter() {
        mAdapter = new RouteAdapter(getActivity(), stops);
        setListAdapter(mAdapter);
    }

    /**
     * Performs the refresh via a quick AsyncTask.  This AsyncTask
     * invokes the route/eta refreshes on the main thread.
     */
    private void doRefresh(boolean fromSwipe) {
        setListShown(false);

        final boolean swipe = fromSwipe;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                final Activity activity = getActivity();

                long start = System.currentTimeMillis();

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.mRoutes == null || MainActivity.mRoutes.isEmpty()) {
                                MainActivity.retrieveAllRoutes(
                                        (RouteTaskCompleted) activity, activity, true);
                            }

                            Route route = getRoute();

                            if (route != null) {
                                getEtasForRoute(route, swipe);
                            }
                        }
                    });
                }

                long end = System.currentTimeMillis();

                if (end - start > 2000) {
                    /*
                     * Literally sleep here just so the user thinks it's doing something
                     * in the case where a connection is so fast that the update is instant.
                     */
                    try {
                        Thread.sleep(2000 - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

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
        Bundle args = getArguments();

        if (args != null) {
            ((MainActivity) activity).onSectionAttached(
                    args.getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * Does a bit of work.  De-duplicates and sorts the list of stops with arrivals.
     * TODO This method is going to change as parsing should eventually not do duplicates.
     */
    public void onArrivalsTaskCompleted(List<Stop> stopsWithArrival) {
        stops.clear();

        for (Stop s : stopsWithArrival) {
            stops.add(s);
        }

        stops = Utils.deDuplicateStops(stops);

        // TODO - we may not need to sort here if Arrival info is prased properly...
        // Collections.sort(stops, new BusStopComparer());

        if (mAdapter == null) {
            setupTheAdapter();
        }

        mAdapter.notifyDataSetChanged();
    }
}
