package pt.afonsogarcia.lunchcern;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pt.afonsogarcia.lunchcern.domain.WeekDay;
import pt.afonsogarcia.lunchcern.network.BuildMenuTask;
import pt.afonsogarcia.lunchcern.network.GetMenuJSONTask;
import pt.afonsogarcia.lunchcern.ui.MenuAdapter;
import pt.afonsogarcia.lunchcern.ui.VerticalSpaceItemDecoration;

public class WeeklyMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private HashMap<Integer, WeekDay> weekMenu;

    private Integer dayOfWeek;
    private Boolean noWearableAvailable = false;

    private NavigationView navigationView;
    private Toolbar toolbar;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_menu);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApiIfAvailable(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        RecyclerView menuView = (RecyclerView) findViewById(R.id.menu_view);
        if (menuView != null) {
            menuView.addItemDecoration(new VerticalSpaceItemDecoration());
            menuView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            menuView.setLayoutManager(mLayoutManager);
        }

        new GetMenuJSONTask(this).execute();

        if(!noWearableAvailable) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, TimerReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 45);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weekly_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            generateWearNotification();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void generateWearNotification() {
        if(!noWearableAvailable) {
            PutDataMapRequest map = PutDataMapRequest.create("/lunchAtCERN");
            map.getDataMap().putLong("time", new Date().getTime());
            PutDataRequest req = map.asPutDataRequest();
            req.setUrgent();
            Wearable.DataApi.putDataItem(mGoogleApiClient, req);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_monday) {
            dayOfWeek = 1;
        } else if (id == R.id.nav_tuesday) {
            dayOfWeek = 2;
        } else if (id == R.id.nav_wednesday) {
            dayOfWeek = 3;
        } else if (id == R.id.nav_thursday) {
            dayOfWeek = 4;
        } else if (id == R.id.nav_friday) {
            dayOfWeek = 5;
        }

        generateMenuList();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void generateMenuList() {
        TextView noMenuAvailable = (TextView) findViewById(R.id.no_menu_available);
        RecyclerView menuView = (RecyclerView) findViewById(R.id.menu_view);

        WeekDay day = weekMenu.get(dayOfWeek);

        toolbar.setTitle(day.getWeekDayName());

        if(navigationView != null && noMenuAvailable != null && menuView != null) {
            if(dayOfWeek > 0 && dayOfWeek < 6) {
                noMenuAvailable.setVisibility(View.INVISIBLE);
                menuView.setVisibility(View.VISIBLE);

                navigationView.getMenu().getItem(dayOfWeek - 1).setChecked(true);

                menuView.setAdapter(new MenuAdapter(day));

            } else {
                menuView.setVisibility(View.INVISIBLE);
                noMenuAvailable.setVisibility(View.VISIBLE);

                for(int i = 0; i < 5; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            }
        }
    }

    public void receiveMenuJSON(String content) {
        new BuildMenuTask(this).execute(content);
    }

    public void receiveMenu(HashMap<Integer, WeekDay> content) {
        weekMenu = content;

        Calendar cal = Calendar.getInstance();
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if(dayOfWeek == 0 || dayOfWeek == 6) {
            dayOfWeek = 1;
        }

        generateMenuList();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        noWearableAvailable = true;
    }

    public class TimerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            generateWearNotification();
        }
    }
}
