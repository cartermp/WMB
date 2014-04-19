package com.jmstudios.corvallistransit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.jmstudios.corvallistransit.jsontools.RetrieveJson;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String sundayMessage =
            "No routes run on Sundays.\n\nCheck back tomorrow, and have a wonderful day!\n\n"
                    + DateTime.now().year().getAsText() + " - PC";
    /**
     * Used to store Bus Routes in the application.
     */
    public static List<Route> mRoutes = new ArrayList<Route>() {{
//        add(new Route() {{
//            stopList = new ArrayList<Stop>() {{
//                add(new Stop() {{
//                    name = "Test Stop 1 oh man is this such a long stop name or what man holy crap";
//                    eta = 12;
//                }});
//                add(new Stop() {{
//                    name = "Test Stop 2";
//                    eta = 15;
//                }});
//            }};
//        }});
//        add(new Route() {{
//            stopList = new ArrayList<Stop>() {{
//                add(new Stop() {{
//                    name = "Test Stop 1";
//                    eta = 21;
//                }});
//                add(new Stop() {{
//                    name = "Test Stop 2";
//                    eta = 30;
//                }});
//            }};
//        }});
    }};
    public static int dayOfWeek;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**Boolean locks async pulling once it's started, we don't want multiple request simultaneously */
    private static boolean isWorking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        //if it's NOT sunday, pull our data down
        if (dayOfWeek != Calendar.SUNDAY)
        {
            getRoutesAndEtasAsync("http://www.corvallis-bus.appspot.com/routes?stops=true", new String[]{"Name", "Road", "AdditionalName", "Description", "Polyline", "Color", "Direction", "Bearing", "AdherencePoint", "Lat", "Long", "ID", "Distance"}, "routes", null, new String[]{"Path"});
        }
    }

    private void initialize()
    {
        isWorking = false;
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Calendar c = Calendar.getInstance();
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * Populates the list of Bus Routes for CTS.
     */
    private void getRoutesAndEtasAsync(String url, String[] jsonSearchList, String requestType, String[] additionalParams, final String[] arrayWithinArray)
    {
        //new ReadRouteInfo(this).execute(mRoutes);
        RetrieveJson rt = new RetrieveJson(jsonSearchList, requestType, additionalParams, arrayWithinArray)
        {
            @Override
            public void onResponseReceived(TreeSet result)
            {
                isWorking = false;
                Iterator i = result.iterator();
                while(i.hasNext())
                {
                    final HashMap<String,String> hm = (HashMap)i.next();
                    mRoutes.add(new Route()
                    {{
                        name = hm.get("Name");
                        polyLine = hm.get("Polyline");
                        stopList = new ArrayList<Stop>()
                        {{
                            add(new Stop()
                            {{
                                String pp = "PATH";
                                name = hm.get(pp+"Name");
                                road = hm.get(pp+"Road");
                                bearing = Double.parseDouble(hm.get(pp+"Bearing"));

                            }});
                        }};
                    }}
                    );
                    /*
                    add(new Route() {{
//            stopList = new ArrayList<Stop>() {{
//                add(new Stop() {{
//                    name = "Test Stop 1 oh man is this such a long stop name or what man holy crap";
//                    eta = 12;
//                }});
//                add(new Stop() {{
//                    name = "Test Stop 2";
//                    eta = 15;
//                }});
//            }};
//        }});
                     */

                }
            }
        };
        rt.execute(url);
        isWorking = true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, RouteViewFragment.newInstance(position + 1))
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

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
