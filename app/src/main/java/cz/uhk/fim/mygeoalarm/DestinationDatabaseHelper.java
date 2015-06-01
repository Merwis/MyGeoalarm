package cz.uhk.fim.mygeoalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Petr on 21. 5. 2015.
 */
public class DestinationDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DestinationDatabaseHelper.class.getCanonicalName();
    public static final String DATABASE_NAME = "destinationdatabase.db";
    public static final int VERSION = 5;

    public static final String SQL_CREATE_DESTINATION_TABLE =
            "CREATE TABLE " + Destinations.TABLE_NAME + " (" +
            Destinations._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Destinations.COLUMN_NAME_NAME + " TEXT, " +
            Destinations.COLUMN_NAME_LONGITUDE + " TEXT, " +
            Destinations.COLUMN_NAME_LATITUDE + " TEXT, " +
            Destinations.COLUMN_NAME_RADIUS + " INTEGER, " +
            Destinations.COLUMN_NAME_ACTIVE + " INTEGER)";

    public static final String SQL_DROP_DESTINATION_TABLE =
            "DROP TABLE IF EXISTS " + Destinations.TABLE_NAME;

    public static final String SQL_CREATE_ALARMSOUNDS_TABLE =
            "CREATE TABLE " + AlarmSounds.TABLE_NAME + " (" +
                    AlarmSounds._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AlarmSounds.COLUMN_NAME_URI + " TEXT)";

    public static final String SQL_DROP_ALARMSOUNDS_TABLE =
            "DROP TABLE IF EXISTS " + AlarmSounds.TABLE_NAME;

    public DestinationDatabaseHelper (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database");
        db.execSQL(SQL_CREATE_DESTINATION_TABLE);
        db.execSQL(SQL_CREATE_ALARMSOUNDS_TABLE);
        fillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Deleting database");
        db.execSQL(SQL_DROP_DESTINATION_TABLE);
        Log.d(TAG, "Creating database");
        db.execSQL(SQL_CREATE_DESTINATION_TABLE);
        fillDatabase(db);
    }

    public void fillDatabase(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Destinations.COLUMN_NAME_NAME, "Praha - demo");
        contentValues.put(Destinations.COLUMN_NAME_LONGITUDE, "14.438338838517666");
        contentValues.put(Destinations.COLUMN_NAME_LATITUDE, "50.07663649442885");
        contentValues.put(Destinations.COLUMN_NAME_RADIUS, 15);
        contentValues.put(Destinations.COLUMN_NAME_ACTIVE, 0);
        db.insert(Destinations.TABLE_NAME, null, contentValues);

        contentValues.put(Destinations.COLUMN_NAME_NAME, "Nove Mesto - demo");
        contentValues.put(Destinations.COLUMN_NAME_LONGITUDE, "16.1516458");
        contentValues.put(Destinations.COLUMN_NAME_LATITUDE, "50.3603444");
        contentValues.put(Destinations.COLUMN_NAME_RADIUS, 1);
        contentValues.put(Destinations.COLUMN_NAME_ACTIVE, 0);
        db.insert(Destinations.TABLE_NAME, null, contentValues);

        //db.close();
        //zavreno v MainFragmentTab/getData()/mHelper.close()
    }
}
