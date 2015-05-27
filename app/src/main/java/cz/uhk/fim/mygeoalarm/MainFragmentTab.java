package cz.uhk.fim.mygeoalarm;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    TextView mDestinationName, mDestinationRadius;
    String destinationName;
    float destinationRadius;
    View mFragmentView;
    Destination activeDestination = new Destination();
    OnDestinationActivatedListener mListener;
    Button mBtnActivate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        mBtnActivate = (Button) mFragmentView.findViewById(R.id.destination_activate);

        mBtnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDestinationActivated(activeDestination);
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
                Destinations._ID, Destinations.COLUMN_NAME_NAME, Destinations.COLUMN_NAME_COORDINATES, Destinations.COLUMN_NAME_RADIUS, Destinations.COLUMN_NAME_ACTIVE};
        Cursor c = mDatabase.query(Destinations.TABLE_NAME, projection, Destinations.COLUMN_NAME_ACTIVE + " = " + 1, null, null, null, null);
        int colId = c.getColumnIndex("_id");
        int colName = c.getColumnIndex("name");
        int colCoordinates = c.getColumnIndex("coordinates");
        int colRadius = c.getColumnIndex("radius");
        c.moveToFirst();
        if (c.getCount() == 0) {
            destinationName = "No active destinations";
            destinationRadius = -1;
            mBtnActivate.setVisibility(Button.INVISIBLE);
        } else {
            destinationName = c.getString(colName);
            destinationRadius = c.getFloat(colRadius);

            activeDestination.setId(c.getLong(colId));
            activeDestination.setName(c.getString(colName));
            activeDestination.setCoordinates(c.getString(colCoordinates));
            activeDestination.setRadius(c.getFloat(colRadius));

            mBtnActivate.setVisibility(Button.VISIBLE);
        }
        c.close();
    }

    private void updateView(View view) {
        mDestinationName = (TextView) view.findViewById(R.id.destination_name);
        mDestinationRadius = (TextView) view.findViewById(R.id.destination_radius);

        mDestinationName.setText(destinationName);
        if (destinationRadius == -1) {
            mDestinationRadius.setText("");
        } else {
            mDestinationRadius.setText(String.valueOf(destinationRadius));
        }
    }

    public interface OnDestinationActivatedListener {
        public void onDestinationActivated(Destination destination);
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
