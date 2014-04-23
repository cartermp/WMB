package com.jmstudios.corvallistransit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.jmstudios.corvallistransit.jsontools.RetrieveJson;
import com.jmstudios.corvallistransit.models.Route;
import com.jmstudios.corvallistransit.models.Stop;
import com.jmstudios.corvallistransit.utils.Utils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Used to store Bus Routes in the application.
     */
    public static List<Route> mRoutes = new ArrayList<Route>() {{
        add(new Route() {{
            stopList = new ArrayList<Stop>() {{
                add(new Stop() {{
                    name = "Test Stop 1 oh man is this such a long stop name or what man holy crap";
                    expectedTime = new DateTime(2014, 4, 19, 12, 30);
                }});
                add(new Stop() {{
                    name = "NW Harrison Blvd & NW 36th St";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 3";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 4";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 5";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 6";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 7";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 8";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 9";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 10";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 11";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 12";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
                add(new Stop() {{
                    name = "Test Stop 13";
                    expectedTime = new DateTime(2014, 4, 19, 12, 35);
                }});
            }};
        }});
        add(new Route() {{
            stopList = new ArrayList<Stop>() {{
                add(new Stop() {{
                    name = "Test Stop 1";
                    expectedTime = Utils.convertToDateTime("19 Apr 14 13:00 -0700");
                }});
                add(new Stop() {{
                    name = "Test Stop 2";
                    expectedTime = Utils.convertToDateTime("19 Apr 14 13:02 -0700");
                }});
            }};
        }});
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

    /**
     * Boolean locks async pulling once it's started, we don't want multiple request simultaneously
     */
    private static boolean isWorking;

    /**
     * Yes, this is nasty, but we dispose of it when the app calls onDistroy()
     */
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        //if it's NOT sunday, pull our data down
        if (dayOfWeek != Calendar.SUNDAY) {
            retrieveAllRoutes();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }

    private void initialize() {
        context = this;
        isWorking = false;
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Calendar c = Calendar.getInstance();
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Static call updates ALL routes
     */
    public static void retrieveAllRoutes() {
        getRoutesAndEtasAsync("http://www.corvallis-bus.appspot.com/routes?stops=true", new String[]{"Name", "Road", "AdditionalName", "Description", "Polyline", "Color", "Direction", "Bearing", "AdherencePoint", "Lat", "Long", "ID"}, "routes", null, new String[]{"Path"});
    }

    /**
     * Populates the list of Bus Routes for CTS.
     */
    public static void getRoutesAndEtasAsync(String url, String[] jsonSearchList, String requestType, String[] additionalParams, final String[] arrayWithinArray) {
        if (!isWorking) {
            final ProgressDialog pd = new ProgressDialog(context);

            mRoutes = null;
            mRoutes = new ArrayList<Route>();
            RetrieveJson rt = new RetrieveJson(context, jsonSearchList, requestType, additionalParams, arrayWithinArray) {
                @Override
                public void onResponseReceived(Set result) {
                    isWorking = false;
                    final Iterator i = result.iterator();
                    Route lastRoute = null;
                    ArrayList<Stop> stopList = new ArrayList<Stop>();
                    String pp = "PATH";

                    while (i.hasNext()) {
                        HashMap<String, String> hm = (HashMap<String, String>) i.next();

                        if (hm.get("Polyline") != null) {
                            if (lastRoute != null) {
                                mRoutes.add(lastRoute);
                            }

                            lastRoute = new Route();
                            lastRoute.name = hm.get("Name");
                            lastRoute.polyLine = hm.get("Polyline");
                            lastRoute.stopList = stopList;

                            stopList = new ArrayList<Stop>();
                        } else if (hm.get(pp + "Road") != null) {
                            Stop newStop = new Stop();

                            newStop.name = hm.get(pp + "Name");
                            newStop.road = hm.get(pp + "Road");
                            newStop.bearing = Double.parseDouble(hm.get(pp + "Bearing"));
                            newStop.adherehancePoint = Boolean.parseBoolean(hm.get(pp + "AdherencePoint"));
                            newStop.latitude = Double.parseDouble(hm.get(pp + "Lat"));
                            newStop.longitude = Double.parseDouble(hm.get(pp + "Long"));
                            newStop.id = Integer.parseInt(hm.get(pp + "ID"));
                            //newStop.distance = Double.parseDouble(hm.get(pp + "Distance"));

                            stopList.add(newStop);
                        }
                    }

                    /*
                    System.out.println("In drawer, the size is:"+MainActivity.mRoutes.size());
                    Iterator i2 = MainActivity.mRoutes.iterator();
                    int x = 0;
                    String[] tmp = new String[mRoutes.size()];
                    while(i2.hasNext())
                    {
                        Route r = (Route)i2.next();
                        tmp[x] = r.name;
                        System.out.println("curName:"+tmp[x]);
                        x++;
                    }
                    NavigationDrawerFragment.mActiveRouteNames = tmp;
                    */
                }
            };
            rt.execute(url);
            isWorking = true;
        }
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
                mTitle = "Route 2 ETA";
                break;
            case 2:
                mTitle = "Route 3 ETA";
                break;
            case 3:
                mTitle = "Route BBSE ETA";
                break;
            case 4:
                mTitle = "Route 4 ETA";
                break;
            case 5:
                mTitle = "Route CVA ETA";
                break;
            case 6:
                mTitle = "Route BBN ETA";
                break;
            case 7:
                mTitle = "Route C3 ETA";
                break;
            case 8:
                mTitle = "Route C2 ETA";
                break;
            case 9:
                mTitle = "Route C1 ETA";
                break;
            case 10:
                mTitle = "Route BBSW ETA";
                break;
            case 11:
                mTitle = "Route 5 ETA";
                break;
            case 12:
                mTitle = "Route 1 ETA";
                break;
            case 13:
                mTitle = "Route 7 ETA";
                break;
            case 14:
                mTitle = "Route 8 ETA";
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

        return false;
//        return id == R.id.action_settings_refresh
//                || id == R.id.action_settings_map
//                || id == R.id.action_alarm
//                || super.onOptionsItemSelected(item);
    }
}
