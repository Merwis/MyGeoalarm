package cz.uhk.fim.mygeoalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Petr on 21. 5. 2015.
 */
public class AddDestinationActivity extends Activity {

    EditText mEtDestinationName, mEtDestinationLongitude, mEtDestinationLatitude, mEtDestinationRadius;
    Button mSaveButton;

    DestinationDatabaseHelper mHelper;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);

        mEtDestinationName = (EditText) findViewById(R.id.addDestinationName);
        mEtDestinationLongitude = (EditText) findViewById(R.id.addDestinationLongitude);
        mEtDestinationLatitude = (EditText) findViewById(R.id.addDestinationLatitude);
        mEtDestinationRadius = (EditText) findViewById(R.id.addDestinationRadius);

        mSaveButton = (Button) findViewById(R.id.addDestinationSave);

        mHelper = new DestinationDatabaseHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEtDestinationName.getText().toString().trim().equals("")) {
                    mEtDestinationName.setError("Destination name is required");
                } else if (mEtDestinationLongitude.getText().toString().trim().equals("")) {
                    mEtDestinationLongitude.setError("Longitude is required");
                } else if (mEtDestinationLatitude.getText().toString().trim().equals("")) {
                    mEtDestinationLatitude.setError("Latitude is required");
                } else if (mEtDestinationRadius.getText().toString().trim().equals("")) {
                    mEtDestinationRadius.setError("Radius is required");
                } else {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Destinations.COLUMN_NAME_NAME, mEtDestinationName.getText().toString());
                    contentValues.put(Destinations.COLUMN_NAME_LONGITUDE, mEtDestinationLongitude.getText().toString());
                    contentValues.put(Destinations.COLUMN_NAME_LATITUDE, mEtDestinationLatitude.getText().toString());
                    contentValues.put(Destinations.COLUMN_NAME_RADIUS, mEtDestinationRadius.getText().toString());
                    contentValues.put(Destinations.COLUMN_NAME_ACTIVE, "0");

                    mDatabase.insert(Destinations.TABLE_NAME, null, contentValues);

                    getBack();
                }
            }
        });
    }

    public void getBack() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
