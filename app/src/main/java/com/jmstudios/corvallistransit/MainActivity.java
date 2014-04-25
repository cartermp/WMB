package com.jmstudios.corvallistransit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.jmstudios.corvallistransit.jsontools.RouteTaskCompleted;
import com.jmstudios.corvallistransit.jsontools.RoutesTask;
import com.jmstudios.corvallistransit.models.Route;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        RouteTaskCompleted {
    /**
     * Used to store Bus Routes in the application.
     */
    public static List<Route> mRoutes = new ArrayList<Route>();

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
     * Static call updates ALL routes
     */
    public static void retrieveAllRoutes(RouteTaskCompleted listener, Context context) {
        new RoutesTask(listener, context).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        //if it's NOT sunday, pull our data down
        if (dayOfWeek != Calendar.SUNDAY && mRoutes.isEmpty()) {
            boolean canConnect = checkConnection();
            if (canConnect) {
                retrieveAllRoutes(this, this);
            } else {
                launchCheckConnectionDialog();
            }
        }
    }

    private void launchCheckConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No Network Connection!")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    private void initialize() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Calendar c = Calendar.getInstance();
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        final Handler handler = new Handler();
        final int pos = position;

        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RouteViewFragment.newInstance(pos + 1))
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return false;
    }

    @Override
    public void onRoutesTaskCompleted(List<Route> routes) {
        mRoutes = routes;
    }
}
