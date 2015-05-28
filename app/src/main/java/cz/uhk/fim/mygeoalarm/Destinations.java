package cz.uhk.fim.mygeoalarm;

import android.provider.BaseColumns;

/**
 * Created by Petr on 21. 5. 2015.
 */
public class Destinations implements BaseColumns {

    public static final String TABLE_NAME = "destinations";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_RADIUS = "radius";
    public static final String COLUMN_NAME_ACTIVE = "active";
}
