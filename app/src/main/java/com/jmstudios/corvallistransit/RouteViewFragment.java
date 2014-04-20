package com.jmstudios.corvallistransit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RouteViewFragment extends ListFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public RouteViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RouteViewFragment newInstance(int sectionNumber)
    {
        RouteViewFragment fragment = new RouteViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int routeIndex = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
        List<Route> routes = MainActivity.mRoutes;
        Route route = (routes != null && routes.size() > routeIndex)
                ? routes.get(routeIndex) : null;

        RouteAdapter adapter = new RouteAdapter(getActivity(),
                (route == null) ? new ArrayList<Stop>() : route.stopList);

        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = null;

        if (MainActivity.dayOfWeek != Calendar.SUNDAY)
        {
            rootView = inflater.inflate(R.layout.route_list, null);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
