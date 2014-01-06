package com.jmstudios.corvallistransit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmstudios.corvallistransit.models.BusRoute;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to store Bus Routes in the application.
     */
    public static List<BusRoute> mRoutes = new ArrayList<BusRoute>();
    public static int dayOfWeek;

    public static final String sundayMessage =
            "No routes run on Sundays.\n\nCheck back tomorrow, and have a wonderful day!\n\n"
                    + DateTime.now().year().getAsText() + " - PC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Calendar c = Calendar.getInstance();
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek != Calendar.SUNDAY) {
            getRoutesAndEtasAsync();
        }
    }

    /**
     * Populates the list of Bus Routes for CTS.
     */
    private void getRoutesAndEtasAsync() {
        new ReadRouteInfo(this).execute(mRoutes);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
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
                mTitle = "Route C1 ETA";
                break;
            case 10:
                mTitle = "Route C2 ETA";
                break;
            case 11:
                mTitle = "Route C3 ETA";
                break;
            case 12:
                mTitle = "Beaver Bus North ETA";
                break;
            case 13:
                mTitle = "Beaver Bus SE ETA";
                break;
            case 14:
                mTitle = "Beaver Bus SW ETA";
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView stopText = (TextView) rootView.findViewById(R.id.stop_text);

            if (dayOfWeek != Calendar.SUNDAY) {
                int routeIndex = getArguments().getInt(ARG_SECTION_NUMBER);
                List<BusRoute> routes = MainActivity.mRoutes;
                BusRoute route = (routes != null && routes.size() > routeIndex) ? routes.get(routeIndex) : null;

                if (routes == null || routes.isEmpty() || route == null || route.stopList == null || route.stopList.isEmpty()) {
                    stopText.setText("No route information to display at this time.");
                } else {
                    stopText.setText("Stop: " + Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
                    TextView etaText = (TextView) rootView.findViewById(R.id.eta_text);
                    etaText.setText("9000m");
                }
            } else {
                stopText.setText(sundayMessage);
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

}
