package cz.uhk.fim.mygeoalarm;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Petr on 11. 5. 2015.
 */
public class MainFragmentTab extends android.support.v4.app.Fragment {

    DestinationDatabaseHelper mHelper;
    SimpleCursorAdapter mAdapter;
    SQLiteDatabase mDatabase;
    TextView mDestinationName, mDestinationRadius, mDestinationCoordinates;
    String destinationName, destinationCoordinates;
    float destinationRadius;
    View mFragmentView;
    Destination activeDestination = new Destination();
    OnDestinationActivatedListener mListener;
    Button mBtnActivate;
    Button mBtnDeactivate;

    public static final String TAG = "Main Fragment Tab";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        mBtnActivate = (Button) mFragmentView.findViewById(R.id.destination_activate);
        mBtnDeactivate = (Button) mFragmentView.findViewById(R.id.destination_deactivate);

        mBtnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDestinationActivated(activeDestination);
                mBtnActivate.setVisibility(View.GONE);
                mBtnDeactivate.setVisibility(View.VISIBLE);
            }
        });
        mBtnDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDestinationDeactivated();
                mBtnDeactivate.setVisibility(View.GONE);
                mBtnActivate.setVisibility(View.VISIBLE);

            }
        });

        getData();
        updateView(mFragmentView);
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        updateView(mFragmentView);
    }

    private void getData() {
        mHelper = new DestinationDatabaseHelper(getActivity());
        mDatabase = mHelper.getReadableDatabase();

        String[] projection = new String[] {
                Destinations._ID, Destinations.COLUMN_NAME_NAME, Destinations.COLUMN_NAME_LONGITUDE, Destinations.COLUMN_NAME_LATITUDE,
                Destinations.COLUMN_NAME_RADIUS, Destinations.COLUMN_NAME_ACTIVE};
        Cursor c = mDatabase.query(Destinations.TABLE_NAME, projection, Destinations.COLUMN_NAME_ACTIVE + " = " + 1, null, null, null, null);
        int colId = c.getColumnIndex("_id");
        int colName = c.getColumnIndex("name");
        int colLongitude = c.getColumnIndex("longitude");
        int colLatitude = c.getColumnIndex("latitude");
        int colRadius = c.getColumnIndex("radius");
        c.moveToFirst();
        if (c.getCount() == 0) {
            destinationName = "No selected destinations.";
            destinationCoordinates = "Select one from Destinations tab.";
            destinationRadius = -1;
            mBtnActivate.setVisibility(Button.INVISIBLE);
            ((MainActivity)getActivity()).setDestinationSelected(false);
        } else {
            destinationName = c.getString(colName);
            destinationRadius = c.getFloat(colRadius);

            activeDestination.setId(c.getLong(colId));
            activeDestination.setName(c.getString(colName));
            activeDestination.setLongitude(c.getString(colLongitude));
            activeDestination.setLatitude(c.getString(colLatitude));
            activeDestination.setRadius(c.getFloat(colRadius));

            mBtnActivate.setVisibility(Button.VISIBLE);
            ((MainActivity)getActivity()).setDestinationSelected(true);
        }
        c.close();
        mHelper.close();
    }

    private void updateView(View view) {
        mDestinationName = (TextView) view.findViewById(R.id.destination_name);
        mDestinationRadius = (TextView) view.findViewById(R.id.destination_radius);
        mDestinationCoordinates = (TextView) view.findViewById(R.id.destination_coordinates);

        mDestinationName.setText(destinationName);

        if (destinationRadius == -1) {
            mDestinationCoordinates.setText(destinationCoordinates);
            mDestinationRadius.setText("");
        } else {
            mDestinationCoordinates.setText(activeDestination.getLatitude() + " | " + activeDestination.getLongitude());
            mDestinationRadius.setText("Activation radius: " + String.valueOf(destinationRadius) + " km");
        }

        if (((MainActivity) getActivity()).isGeofenceAdded()) {
            mBtnActivate.setVisibility(Button.INVISIBLE);
            mBtnDeactivate.setVisibility(Button.VISIBLE);
        } else {
            mBtnDeactivate.setVisibility(Button.INVISIBLE);
        }

    }

    public interface OnDestinationActivatedListener {
        public void onDestinationActivated(Destination destination);
        public void onDestinationDeactivated();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnDestinationActivatedListener) activity;
        } catch (ClassCastException e) {

        }
    }

}
