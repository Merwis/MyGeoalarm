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
    public static final int VERSION = 2;

    public static final String SQL_CREATE_DESTINATION_TABLE =
            "CREATE TABLE " + Destinations.TABLE_NAME + " (" +
            Destinations._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Destinations.COLUMN_NAME_NAME + " TEXT, " +
            Destinations.COLUMN_NAME_COORDINATES + " TEXT, " +
            Destinations.COLUMN_NAME_RADIUS + " INTEGER)";

    public static final String SQL_DROP_DESTINATION_TABLE =
            "DROP TABLE IF EXISTS " + Destinations.TABLE_NAME;

    public DestinationDatabaseHelper (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database");
        db.execSQL(SQL_CREATE_DESTINATION_TABLE);
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
        contentValues.put(Destinations.COLUMN_NAME_NAME, "Hradec Králové");
        contentValues.put(Destinations.COLUMN_NAME_COORDINATES, "50.212121, 36.2132132");
        contentValues.put(Destinations.COLUMN_NAME_RADIUS, 1);
        db.insert(Destinations.TABLE_NAME, null, contentValues);

        contentValues.put(Destinations.COLUMN_NAME_NAME, "Nove Mesto");
        contentValues.put(Destinations.COLUMN_NAME_COORDINATES, "50.212121, 39.2132132");
        contentValues.put(Destinations.COLUMN_NAME_RADIUS, 1);
        db.insert(Destinations.TABLE_NAME, null, contentValues);

        contentValues.put(Destinations.COLUMN_NAME_NAME, "Nachod");
        contentValues.put(Destinations.COLUMN_NAME_COORDINATES, "50.212121, 32.2132132");
        contentValues.put(Destinations.COLUMN_NAME_RADIUS, 2);
        db.insert(Destinations.TABLE_NAME, null, contentValues);
    }
}
