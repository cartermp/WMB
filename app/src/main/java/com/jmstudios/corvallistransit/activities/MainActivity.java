package com.jmstudios.corvallistransit.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.jmstudios.corvallistransit.R;
import com.jmstudios.corvallistransit.adapters.RouteAdapter;
import com.jmstudios.corvallistransit.fragments.NavigationDrawerFragment;
import com.jmstudios.corvallistransit.fragments.RouteMapFragment;
import com.jmstudios.corvallistransit.fragments.RouteViewFragment;
import com.jmstudios.corvallistransit.interfaces.RouteTaskCompleted;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.utils.WebUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        RouteTaskCompleted, RouteAdapter.MapListenerCallbacks {

    /**
     * Used by Mixpanel to properly identify our session
     */
    private static final String MIXPANEL_TOKEN = "3733fd953730250288a417e9f7522751";
    /**
     * Used to store Bus Routes in the application.
     */
    public static List<Route> mRoutes = new ArrayList<Route>();
    private MixpanelAPI mixPanel;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        doRoutesSetup();

        setupMixpanel();
    }

    private void setupMixpanel() {
        mixPanel = MixpanelAPI.getInstance(getApplication(), MIXPANEL_TOKEN);
        JSONObject props = new JSONObject();
        try {
            props.put("appOpen (android)", 1);
        } catch (JSONException jse) {
        }

        mixPanel.track("appOpen (Android)", props);
    }


    @Override
    protected void onResume() {
        super.onResume();
        doRoutesSetup();
    }

    @Override
    protected void onDestroy() {
        mixPanel.flush();
        super.onDestroy();
    }

    private void doRoutesSetup() {
        if (mRoutes.isEmpty()) {
            boolean canConnect = WebUtils.checkConnection(this);
            if (!canConnect) {
                WebUtils.launchCheckConnectionDialog(this);
            }
        }
    }

    /**
     * Performs initial setup operations.
     */
    private void initialize() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * Alerts NavigationDrawer to update the eta's for our display now that our routes have been fetched.
     */
    private void loadInitialArrivalTimes() {
        mNavigationDrawerFragment.loadInitialArrivals();
    }

    @Override
    public void onRouteMapButtonPressed(final int position, final boolean fromStop,
                                        final double lat, final double lng) {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RouteMapFragment.newInstance(
                                position, fromStop, lat, lng))
                        .commit();
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        final Handler handler = new Handler();

        // Posting the work off on a handler makes it *slightly*
        // "faster" from the user's perspective with a large list.
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RouteViewFragment.newInstance(position + 1))
                        .commit();
            }
        });
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Route 1 ETA";
                break;
            case 2:
                mTitle = "Route 2 ETA";
                break;
            case 3:
                mTitle = "Route 3 ETA";
                break;
            case 4:
                mTitle = "Route 4 ETA";
                break;
            case 5:
                mTitle = "Route 5 ETA";
                break;
            case 6:
                mTitle = "Route 6 ETA";
                break;
            case 7:
                mTitle = "Route 7 ETA";
                break;
            case 8:
                mTitle = "Route 8 ETA";
                break;
            case 9:
                mTitle = "Route BBN ETA";
                break;
            case 10:
                mTitle = "Route BBSE ETA";
                break;
            case 11:
                mTitle = "Route BBSW ETA";
                break;
            case 12:
                mTitle = "Route C1 ETA";
                break;
            case 13:
                mTitle = "Route C2 ETA";
                break;
            case 14:
                mTitle = "Route C3 ETA";
                break;
            case 15:
                mTitle = "Route CVA ETA";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Always return false because for some reason not doing so won't
        // consume all touch events.
        return false;
    }

    /**
     * Our callback for when Routes have been downloaded..
     * <p/>
     * This is called on the UI thread.
     */
    @Override
    public void onRoutesTaskCompleted(List<Route> routes) {
        mRoutes = routes;

        //calls method to alert NavDrawer to update content for eta's
        loadInitialArrivalTimes();
    }

    /**
     * Our callback for when we got a timeout from Routes.
     * <p/>
     * This is called on the UI thread.
     */
    @Override
    public void onRoutesTaskTimeout() {
        WebUtils.launchCheckConnectionDialog(this);
    }

    @Override
    public void onEtaCardClick(double lat, double lng) {
        NavigationDrawerFragment.mapOpen = true;
        onRouteMapButtonPressed(NavigationDrawerFragment.mCurrentSelectedPosition,
                true, lat, lng);
    }
}
