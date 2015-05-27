package cz.uhk.fim.mygeoalarm;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
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
                Destinations._ID, Destinations.COLUMN_NAME_NAME, Destinations.COLUMN_NAME_RADIUS, Destinations.COLUMN_NAME_ACTIVE};
        Cursor c = mDatabase.query(Destinations.TABLE_NAME, projection, Destinations.COLUMN_NAME_ACTIVE + " = " + 1, null, null, null, null);
        int colName = c.getColumnIndex("name");
        int colRadius = c.getColumnIndex("radius");
        c.moveToFirst();
        if (c.getCount() == 0) {
            destinationName = "No active destinations";
            destinationRadius = -1;
        } else {
            destinationName = c.getString(colName);
            destinationRadius = c.getFloat(colRadius);
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
}
