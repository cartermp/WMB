package com.jmstudios.corvallistransit.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.activities.MainActivity;
import com.jmstudios.corvallistransit.adapters.RouteAdapter;
import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.interfaces.RouteTaskCompleted;
import com.jmstudios.corvallistransit.jsontools.ArrivalsTask;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.ArrivalsListener;
import com.jmstudios.corvallistransit.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class RouteViewFragment extends ListFragment
        implements ArrivalsTaskCompleted {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Used for on-demand fragment refresh
     */
    public static int mSectionNumber;

    /**
     * Since we can only load 20 at a time, set the initial relative end of the list to that.
     */
    private static int relativeEnd = 20;
    public List<Stop> stops = new ArrayList<Stop>();
    private PullToRefreshLayout mPullToRefreshLayout;
    private RouteAdapter mAdapter;
    private String routeColor;

    public RouteViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RouteViewFragment newInstance(int sectionNumber) {
        mSectionNumber = sectionNumber;
        RouteViewFragment fragment = new RouteViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (MainActivity.dayOfWeek == Calendar.SUNDAY) {
            setEmptyText(getResources().getString(R.string.sunday_message));
        } else {
            Route route = getRoute();

            if (route != null) {
                doRefresh(false);

                routeColor = route.color;

                if (stops != null && !stops.isEmpty()) {
                    setupTheAdapter(routeColor);
                } else {
                    setEmptyText(getResources().getString(R.string.no_route_info));
                }
            } else {
                setEmptyText(getResources().getString(R.string.no_route_info));
            }
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
        ListView lv = (ListView) layout.findViewById(android.R.id.list);
        ViewGroup parent = (ViewGroup) lv.getParent();

        // Remove ListView and add CustomView  in its place
        int lvIndex = parent.indexOfChild(lv);
        parent.removeViewAt(lvIndex);

        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.route_list, container, false);

        parent.addView(mLinearLayout, lvIndex, lv.getLayoutParams());

        lv.setOnScrollListener(new ArrivalsListener() {
            @Override
            public void onLoadMore(int index, int total) {
                ListView listView = getListView();

                View loadingView = getView().findViewById(R.id.loadingBar);
                listView.addFooterView(loadingView);

                relativeEnd += 20;
                doRefresh(false);
            }
        });

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
                        doRefresh(true);
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    private void getEtasForRoute(final Route route, boolean fromSwipe, int start, int end) {
        new ArrivalsTask(getActivity(), route.name, this, fromSwipe)
                .execute(Utils.getStopRange(route.stopList, start, end));
    }

    private Route getRoute() {
        Route route = null;

        int routeIndex = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
        List<Route> routes = MainActivity.mRoutes;

        if (routes != null && routes.size() > routeIndex) {
            route = routes.get(routeIndex);
        }

        return route;
    }

    private void setupTheAdapter(String routeColor) {
        mAdapter = new RouteAdapter(getActivity(), stops, routeColor);
        setListAdapter(mAdapter);
    }

    /**
     * Performs the refresh via a quick AsyncTask.  This AsyncTask
     * invokes the route/eta refresh on the main thread.
     */
    private void doRefresh(final boolean fromSwipe) {
        setListShown(false);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final Activity activity = getActivity();

                /*
                 * No heuristic here.  Sleep for a second to give users the impression
                 * that it's doing something, since quick refreshes make it look like nothing
                 * was updated.
                 */
                if (fromSwipe) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

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
                                getEtasForRoute(route, fromSwipe, 0, relativeEnd);
                            }
                        }
                    });
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
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onArrivalsTaskCompleted(List<Stop> stopsWithArrival) {
        if (stopsWithArrival != null && !stopsWithArrival.isEmpty()) {
            stops = Utils.filterTimes(stopsWithArrival);

            //always setup the adapter to refresh the data
            setupTheAdapter(routeColor);

            mAdapter.notifyDataSetChanged();

            // Since we set up the adapter, we can call this
            View loadingView = getView().findViewById(R.id.loadingBar);
            ListView lv = getListView();
            lv.removeFooterView(loadingView);
        }
    }
}
