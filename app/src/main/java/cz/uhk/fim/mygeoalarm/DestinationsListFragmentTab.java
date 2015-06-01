package cz.uhk.fim.mygeoalarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Petr on 11. 5. 2015.
 */
public class DestinationsListFragmentTab extends android.support.v4.app.ListFragment {

    DestinationDatabaseHelper mHelper;
    SimpleCursorAdapter mAdapter;
    ListView mList;
    SQLiteDatabase mDatabase;
    //OnDestinationSelectedListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new DestinationDatabaseHelper(getActivity());
        mDatabase = mHelper.getWritableDatabase();

        String[] projection = new String[] {
                Destinations._ID, Destinations.COLUMN_NAME_NAME, Destinations.COLUMN_NAME_RADIUS, Destinations.COLUMN_NAME_ACTIVE};

        Cursor c = mDatabase.query(Destinations.TABLE_NAME, projection, null, null, null, null, null);

        mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_item_1,
                    c,
                    new String[] {Destinations.COLUMN_NAME_NAME, Destinations.COLUMN_NAME_RADIUS},
                    new int[] {android.R.id.text1, R.id.mujtext},
                    SimpleCursorAdapter.NO_SELECTION);
        mAdapter.setViewBinder(new MyViewBinder());
        setListAdapter(mAdapter);

    }

    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
         @Override
         public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

             if (columnIndex == cursor.getColumnIndex(Destinations.COLUMN_NAME_RADIUS)) {
                 TextView textView = (TextView) view;
                 String radius = cursor.getString(columnIndex);
                 textView.setText("radius: " + radius + " km");
                 return true;
             }
             return false;
         }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_destinations_list, container, false);
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mList = (ListView) v.findViewById(android.R.id.list);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.context_menu_destination_list_fragment, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_destination:
                        for (int i = 0; i < mAdapter.getCount(); i++) {
                            if (mList.isItemChecked(i)) {
                                Long id = mAdapter.getItemId(i);
                                mDatabase.delete(Destinations.TABLE_NAME,
                                        Destinations._ID + " LIKE ?", new String[] {id+""});
                            }
                        }

                    Cursor c = mDatabase.query(Destinations.TABLE_NAME,
                            null, null, null, null, null, null);
                    Cursor oldCursor = mAdapter.swapCursor(c);
                        if (oldCursor != null) {
                            oldCursor.close();
                        }
                    mode.finish();
                    return true;
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        String[] select = new String[] {Destinations._ID, Destinations.COLUMN_NAME_ACTIVE};

        Cursor c = mDatabase.query(Destinations.TABLE_NAME, select, Destinations._ID + "=" + id, null, null, null, null );
        Log.d("pred", "pred" + id);
        int col = c.getColumnIndex("active");
        c.moveToFirst();
        Log.d("pred", "pred" + c.getCount() + " " + c.getColumnCount());

        Long active = c.getLong(col);
        ContentValues cv = new ContentValues();

        if (active == 0) {
            cv.put(Destinations.COLUMN_NAME_ACTIVE, 1);
            Log.d("update", "1");
        } else {
            cv.put(Destinations.COLUMN_NAME_ACTIVE, 0);
            Log.d("update", "0");
        }

        mDatabase.update(Destinations.TABLE_NAME, cv, Destinations._ID + " = " + id, null);
        cv.put(Destinations.COLUMN_NAME_ACTIVE, 0);
        mDatabase.update(Destinations.TABLE_NAME, cv, Destinations._ID + " != " + id, null);
        c.close();
        ((MainActivity)getActivity()).setGeofenceChange(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_destination:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
