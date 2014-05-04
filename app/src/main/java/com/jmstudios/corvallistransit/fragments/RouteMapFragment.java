package com.jmstudios.corvallistransit.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.activities.MainActivity;
import com.jmstudios.corvallistransit.models.Route;

import java.util.List;

public class RouteMapFragment extends Fragment {
    private static final String ROUTE_IDX = "route_index";
    private final LatLng CORVALLIS = new LatLng(44.557285, -123.2852531);
    private GoogleMap map;
    private Route route;

    public static RouteMapFragment newInstance(int routeIdx) {
        RouteMapFragment frag = new RouteMapFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ROUTE_IDX, routeIdx);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        final View theView = inflater.inflate(R.layout.fragment_route_map, container, false);

        route = getRoute();

        initMap();

        return theView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FragmentManager fm = getFragmentManager();
        MapFragment mf = (MapFragment) fm.findFragmentById(R.id.map);

        if (mf != null) {
            fm.beginTransaction().remove(mf).commit();
        }
    }

    private void initMap() {
        if (map == null) {
            MapFragment mapFragment = MapFragment.newInstance();
            map = mapFragment.getMap();

            if (map != null) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(route.polyLinePositions);
                polylineOptions.color(Color.parseColor("#" + route.color));

                map.addPolyline(polylineOptions);
            }
        }
    }

    private Route getRoute() {
        Route route = null;

        int routeIndex = getArguments().getInt(ROUTE_IDX);
        List<Route> routes = MainActivity.mRoutes;

        if (routes != null && routes.size() > routeIndex) {
            route = routes.get(routeIndex);
        }

        return route;
    }
}
