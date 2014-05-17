package com.jmstudios.corvallistransit.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.activities.MainActivity;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.Utils;

import java.util.List;

public class RouteMapFragment extends Fragment {
    private static final String ROUTE_IDX = "route_index";
    private static final String FROM_STOP = "from_stop";
    private static final String STOP_LAT = "stop_latitude";
    private static final String STOP_LNG = "stop_longitude";
    private final LatLng CORVALLIS = new LatLng(44.557285, -123.2852531);

    private double stopLat;
    private double stopLng;
    private boolean fromStop = false;
    private Route route;

    private ClusterManager<Stop> mClusterManager;
    private GoogleMap map;
    private MapView mapView;

    public static RouteMapFragment newInstance(int routeIdx, boolean fromStop,
                                               double lat, double lng) {
        RouteMapFragment frag = new RouteMapFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ROUTE_IDX, routeIdx);
        bundle.putBoolean(FROM_STOP, fromStop);
        bundle.putDouble(STOP_LAT, lat);
        bundle.putDouble(STOP_LNG, lng);

        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();

        setUpBundleArgs(args);

        View v = inflater.inflate(R.layout.fragment_route_map, container, false);

        if (v != null) {
            mapView = (MapView) v.findViewById(R.id.map);
            if (mapView != null) {
                mapView.onCreate(savedInstanceState);
            }
        }

        MapsInitializer.initialize(this.getActivity());

        route = getRoute();

        setupMapIfNeeded(fromStop);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    private void setUpBundleArgs(Bundle args) {
        if (args != null) {
            if (args.containsKey(FROM_STOP)) {
                fromStop = args.getBoolean(FROM_STOP);
            }

            if (args.containsKey(STOP_LAT)) {
                stopLat = args.getDouble(STOP_LAT);
            }

            if (args.containsKey(STOP_LNG)) {
                stopLng = args.getDouble(STOP_LNG);
            }
        }
    }

    private void setupMapIfNeeded(boolean fromStop) {
        if (map == null) {
            if (mapView != null) {
                map = mapView.getMap();
            }

            if (map != null) {
                setupMap(fromStop);
            }
        }
    }

    private void setupMap(boolean fromStop) {
        animateMap(fromStop);

        setUpClusterer();

        setUpMapUI();

        if (map != null && route != null) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(route.polyLinePositions);
            polylineOptions.color(Color.parseColor("#" + route.color));

            map.addPolyline(polylineOptions);
        }
    }

    private void setUpClusterer() {
        if (map != null) {
            mClusterManager = new ClusterManager<Stop>(getActivity(), map);

            map.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    if (route != null && route.stopList != null && !route.stopList.isEmpty()) {
                        int i = Utils.findStopByLocation(route.stopList, marker.getPosition());
                        if (i >= 0) {
                            Stop s = route.stopList.get(i);
                            if (s != null) {
                                marker.setTitle(s.etaText());
                                marker.setSnippet(s.name);
                            }

                            return null;
                        }
                    }
                    return null;
                }
            });

            map.setOnCameraChangeListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);

            if (route != null && route.stopList != null && !route.stopList.isEmpty()) {
                mClusterManager.addItems(route.stopList);
            }
        }
    }

    private void setUpMapUI() {
        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    private Route getRoute() {
        Route route = null;

        Bundle b = getArguments();
        if (b != null) {
            int routeIndex = b.getInt(ROUTE_IDX);
            List<Route> routes = MainActivity.mRoutes;

            if (routes != null && routes.size() > routeIndex) {
                route = routes.get(routeIndex);
            }
        }

        return route;
    }

    private void animateMap(boolean fromStop) {
        if (map != null) {
            LatLng pos = fromStop ? new LatLng(stopLat, stopLng) : CORVALLIS;
            float zoom = fromStop ? 15 : 13;

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pos)
                    .zoom(zoom)
                    .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
