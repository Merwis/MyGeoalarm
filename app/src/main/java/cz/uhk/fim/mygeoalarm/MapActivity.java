package cz.uhk.fim.mygeoalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Petr on 1. 6. 2015.
 */
public class MapActivity extends Activity implements OnMapReadyCallback{

    MapFragment mMapFragment;
    MarkerOptions mMarker;
    Button mAddCoordinatesButton;
    LatLng mLatLng, mOldLatLng;
    String mName, mLongitude, mLatitude, mRadius, mLastKnownLongitude, mLastKnownLatitude;
    SharedPreferences mSharedPreferences;


    public static final String TAG = "MapActivity";
    public static final String SHARED_PREFERENCES = "GeoalarmSharedPreferences";
    public static final String LAST_KNOWN_LONGITUDE_ADDED_KEY = "GeoalarmLongitudeAddedKey";
    public static final String LAST_KNOWN_LATITUDE_ADDED_KEY = "GeoalarmLatitudeAddedKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            mName = b.getString("name");
            mLongitude = (b.getString("longitude"));
            mLatitude = (b.getString("latitude"));
            mRadius = (b.getString("radius"));
        }

        if (savedInstanceState != null) {
            mLongitude = savedInstanceState.getString("longitude");
            mLatitude = savedInstanceState.getString("latitude");
        }



        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mAddCoordinatesButton = (Button) findViewById(R.id.addGpsCoordinates);
        mAddCoordinatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MapActivity.this, AddDestinationActivity.class);
                Bundle b = new Bundle();
                b.putString("longitude", mLatLng.longitude + "");
                b.putString("latitude", mLatLng.latitude + "");
                b.putString("name", mName);
                b.putString("radius", mRadius);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mLastKnownLongitude = mSharedPreferences.getString(LAST_KNOWN_LONGITUDE_ADDED_KEY, "");
        mLastKnownLatitude = mSharedPreferences.getString(LAST_KNOWN_LATITUDE_ADDED_KEY, "");
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        if (!mLongitude.isEmpty() && !mLatitude.isEmpty()) {
            mOldLatLng = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            mMarker = new MarkerOptions().position(mOldLatLng).title(mName);
            mLatLng = mOldLatLng;
            googleMap.addMarker(mMarker);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 8f));
        } else if (!mLastKnownLongitude.isEmpty() && !mLastKnownLatitude.isEmpty()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(mLastKnownLatitude),
                            Double.parseDouble(mLastKnownLongitude)),
                    8f));
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mLatLng = latLng;
                if (mMarker == null) {
                    mMarker = new MarkerOptions().position(latLng).title(mName);
                    googleMap.addMarker(mMarker);
                } else {
                    googleMap.clear();
                    mMarker.position(latLng);
                    googleMap.addMarker(mMarker);
                }
                drawCircle(googleMap);
                Log.d(TAG, "" + latLng.latitude + " " + latLng.longitude);
            }
        });

        if (mOldLatLng != null) {
            if (mOldLatLng.latitude == mLatLng.latitude) {
                drawCircle(googleMap);
            }
        }


    }

    public void drawCircle(GoogleMap googleMap) {
        if (mMarker != null && !mRadius.isEmpty()) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(mLatLng)
                    .radius(Double.parseDouble(mRadius) * 1000)
                    .visible(true);
            googleMap.addCircle(circleOptions);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("longitude", mLatLng.longitude + "");
        outState.putString("latitude", mLatLng.latitude + "");
    }
}
