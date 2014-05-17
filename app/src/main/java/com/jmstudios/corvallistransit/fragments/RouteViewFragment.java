package com.jmstudios.corvallistransit.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jmstudios.corvallistransit.AsyncTasks.RefreshTask;
import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.activities.MainActivity;
import com.jmstudios.corvallistransit.adapters.RouteAdapter;
import com.jmstudios.corvallistransit.interfaces.ArrivalsTaskCompleted;
import com.jmstudios.corvallistransit.interfaces.RefreshTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.SystemUtils;
import com.jmstudios.corvallistransit.utils.Utils;
import com.jmstudios.corvallistransit.utils.WebUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class RouteViewFragment extends ListFragment
        implements ArrivalsTaskCompleted, RefreshTaskCompleted {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Used for on-demand fragment refresh
     */
    public static int mSectionNumber;
    public List<Stop> stops = new ArrayList<Stop>();
    private Activity mParentActivity;
    private RouteAdapter.MapListenerCallbacks mMapCallbacks;
    private PullToRefreshLayout mPullToRefreshLayout;
    private RouteAdapter mAdapter;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParentActivity = activity;
        mMapCallbacks = (RouteAdapter.MapListenerCallbacks) activity;
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMapCallbacks = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv = getListView();
        int idx = getRouteIndex();

        if (lv != null) {
            getListView().setBackgroundColor(Color.parseColor(Utils.routeColors[idx]));
        }

        if (Utils.getCurrentDay() == Calendar.SUNDAY) {
            setEmptyText(getResources().getString(R.string.sunday_message));
        } else {
            if (stops == null || stops.isEmpty()) {
                Log.d("RouteViewFrag", "stops are null or empty!");
                doRefresh(false);
            }

            setEmptyText(getResources().getString(R.string.no_route_info));
        }

        View v = getListView().getEmptyView();
        if (v != null) {
            v.setBackgroundColor(Color.parseColor(Utils.routeColors[idx]));
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

    private int getRouteIndex() {
        int routeIndex = 0;

        Bundle args = getArguments();
        if (args != null) {
            routeIndex = args.getInt(ARG_SECTION_NUMBER) - 1;
        }

        return routeIndex;
    }

    private Route getRoute() {
        Route route = null;

        int routeIndex = getRouteIndex();
        List<Route> routes = MainActivity.mRoutes;

        if (routes != null && routes.size() > routeIndex) {
            route = routes.get(routeIndex);
        }

        return route;
    }

    private void setupTheAdapter() {
        if (stops != null) {
            if (mParentActivity != null) {
                Route route = getRoute();
                if (route != null) {
                    mAdapter = new RouteAdapter(mParentActivity, mMapCallbacks, stops);
                    setListAdapter(mAdapter);
                }
            } else {
                Log.d("RouteViewFrag", "activity is null when setting up the adapter!");
            }
        }
    }

    @Override
    public void onArrivalsTaskCompleted(List<Stop> stopsWithArrival) {
        if (stopsWithArrival != null && !stopsWithArrival.isEmpty()) {
            stops = stopsWithArrival;

            setupTheAdapter();

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onArrivalsTaskTimeout() {
        final Activity a = getActivity();
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WebUtils.launchCheckConnectionDialog(a);
                }
            });
        }
    }

    @Override
    public void onArrivalsTaskError() {
        final Activity a = getActivity();
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SystemUtils.doArrivalsErrorDialogSetup(a);
                }
            });
        }
    }

    @Override
    public void onRefreshTaskComplete() {
        if (mPullToRefreshLayout != null) {
            mPullToRefreshLayout.setRefreshComplete();
        }

        if (getView() != null) {
            setListShown(true);
        }
    }

    /**
     * Performs the refresh via a quick AsyncTask.  This AsyncTask
     * invokes the route/eta refresh on the main thread.
     */
    private void doRefresh(final boolean fromSwipe) {
        final Route route = getRoute();

        setListShown(false);

        new RefreshTask(mParentActivity, route, this, this, fromSwipe)
                .execute();
    }

}
