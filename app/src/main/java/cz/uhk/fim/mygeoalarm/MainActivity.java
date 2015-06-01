package cz.uhk.fim.mygeoalarm;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>, MainFragmentTab.OnDestinationActivatedListener  {

    ViewPager mViewPager;
    FragmentPageAdapter mFragmentPageAdapter;
    GoogleApiClient mGoogleApiClient;
    Destination activeDestination;

    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private boolean mGeofenceAdded;
    private SharedPreferences mSharedPreferences;

    private LocationRequest mLocationRequest;

    private boolean mGeofenceChange = false;
    private boolean mDestinationSelected = false;

    private Button mBtnOpenAddDestination;



    public static final String TAG = "Main activity";
    public static final String SHARED_PREFERENCES = "GeoalarmSharedPreferences";
    public static final String GEOFENCES_ADDED_KEY = "GeoalarmGeofencesAddedKey";
    public static final String LAST_KNOWN_LONGITUDE_ADDED_KEY = "GeoalarmLongitudeAddedKey";
    public static final String LAST_KNOWN_LATITUDE_ADDED_KEY = "GeoalarmLatitudeAddedKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mFragmentPageAdapter = new FragmentPageAdapter(getSupportFragmentManager());
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


                if (i == 0 && mGeofenceChange == true && mGeofenceAdded == true && mDestinationSelected == true) {
                    Log.d(TAG, "geofence change");
                    removeGeofences();
                    stopGpsUpdates();
                    mGeofenceChange = false;
                }

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

        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        mGeofenceAdded = mSharedPreferences.getBoolean(GEOFENCES_ADDED_KEY, false);


        buildGoogleApiClient();

        mBtnOpenAddDestination = (Button) findViewById(R.id.openAddDestination);
        mBtnOpenAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddDestinationActivity.class);
                startActivity(intent);
            }
        });

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
                Intent intentDestination = new Intent(this, AddDestinationActivity.class);
                startActivity(intentDestination);
                return true;
            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(LAST_KNOWN_LONGITUDE_ADDED_KEY, mLastLocation.getLongitude() + "");
            editor.putString(LAST_KNOWN_LATITUDE_ADDED_KEY, mLastLocation.getLatitude() + "");
            editor.commit();
        }
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

            mGeofenceAdded = true;

            Toast.makeText(
                    this,
                    ("Geoalarm activated"),
                    Toast.LENGTH_SHORT
            ).show();
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
            Log.d(TAG, "Geofence deactivated");
            mGeofenceAdded = false;

            Toast.makeText(
                    this,
                    ("Geoalarm deactivated"),
                    Toast.LENGTH_SHORT
            ).show();
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(GEOFENCES_ADDED_KEY, mGeofenceAdded);
            editor.commit();

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
        startGpsUpdates();
        activeDestination = destination;
        Log.d(TAG, "created " + activeDestination.getName());

        mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(activeDestination.getName()))
                        .setCircularRegion(
                                Double.parseDouble(activeDestination.getLatitude()),
                                Double.parseDouble(activeDestination.getLongitude()),
                                activeDestination.getRadius() * 1000
                        )
                        .setExpirationDuration(12 * 60 * 60 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build()
        );
        Log.d(TAG, "" + activeDestination.getLatitude() + " " + activeDestination.getLongitude() + " " + activeDestination.getRadius());
        addGeofences();
   }

    private void stopGpsUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,getGeofencePendingIntent());
    }

    private void startGpsUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, getGeofencePendingIntent());
    }

    @Override
    public void onDestinationDeactivated() {
        stopGpsUpdates();
        removeGeofences();
    }

    public boolean isGeofenceAdded() {
        return mGeofenceAdded;
    }

    public void setGeofenceChange(boolean geofenceChange) {
        mGeofenceChange = geofenceChange;
    }

    public void setDestinationSelected(boolean destinationSelected) {
        mDestinationSelected = destinationSelected;
    }

}
