package cz.uhk.fim.mygeoalarm;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>, MainFragmentTab.OnDestinationActivatedListener  {

    ActionBar mActionBar;
    ViewPager mViewPager;
    FragmentPageAdapter mFragmentPageAdapter;
    GoogleApiClient mGoogleApiClient;
    Destination activeDestination;

    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private boolean mGeofenceAdded;
    private SharedPreferences mSharedPreferences;



    public static final String TAG = "Main activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);
        setContentView(mViewPager);

        mFragmentPageAdapter = new FragmentPageAdapter(getSupportFragmentManager(), this.getApplicationContext());
        mViewPager.setAdapter(mFragmentPageAdapter);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        actionBar.addTab(actionBar.newTab().setText("Home").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Destinations").setTabListener(this));
        actionBar.setDisplayShowTitleEnabled(true);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);

                android.support.v4.app.Fragment fragment =
                        ((FragmentPageAdapter) mViewPager.getAdapter()).getFragment(i);

                if (i == 0 && fragment != null) {
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mGeofenceList = new ArrayList<Geofence>();

        mGeofencePendingIntent = null;

        mSharedPreferences = getSharedPreferences("Preference", MODE_PRIVATE);


        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_destination_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_destination:
                Intent intent = new Intent(this, AddDestinationActivity.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to GAC");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.d(TAG, "connect");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(mGeofenceList);
        Log.d(TAG, "geof req");
        return builder.build();
    }

    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d(TAG, "try");
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {

        }
    }

    public void removeGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient, getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            mGeofenceAdded = !mGeofenceAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(String.valueOf(activeDestination.getId()), mGeofenceAdded);
            editor.commit();

            Toast.makeText(
                    this,
                    (mGeofenceAdded ? "Geofence pridan" : "Geofence odstranen"),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Log.d(TAG, "Nekde je chyba");
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestinationActivated(Destination destination) {
        activeDestination = destination;
        Log.d(TAG, "created " + activeDestination.getName());

        mGeofenceAdded = mSharedPreferences.getBoolean(String.valueOf(activeDestination.getId()), false);

        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(activeDestination.getId()))
                        .setCircularRegion(
                                Double.parseDouble(activeDestination.getLatitude()),
                                Double.parseDouble(activeDestination.getLongitude()),
                                activeDestination.getRadius() * 1000
                        )
                        .setExpirationDuration(12 * 60 * 60 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build()
        );

        addGeofences();

    }

    @Override
    public void onDestinationDeactivated() {
        removeGeofences();
    }
}
