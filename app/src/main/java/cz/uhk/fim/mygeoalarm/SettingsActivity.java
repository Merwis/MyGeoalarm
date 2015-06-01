package cz.uhk.fim.mygeoalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Petr on 31. 5. 2015.
 */
public class SettingsActivity extends Activity {

    Button mBtnSound;
    DestinationDatabaseHelper mHelper;
    SQLiteDatabase mDatabase;
    Uri audioUri;
    TextView mSongName;

    private SharedPreferences mSharedPreferences;

    public static final String TAG = "Settings activity";
    public static final String SHARED_PREFERENCES = "GeoalarmSharedPreferences";
    public static final String SONG_ADDED_KEY = "GeoalarmSongAddedKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mHelper = new DestinationDatabaseHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        mBtnSound = (Button) findViewById(R.id.fileButton);
        mSongName = (TextView) findViewById(R.id.settingsSongName);
        mBtnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickAudioIntent, 1);
            }
        });
        mSongName.setText(mSharedPreferences.getString(SONG_ADDED_KEY, "Default song"));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            audioUri = data.getData();
            ContentValues contentValues = new ContentValues();
            contentValues.put(AlarmSounds.COLUMN_NAME_URI, audioUri.toString());
            mDatabase.insert(AlarmSounds.TABLE_NAME, null, contentValues);
            Log.d(TAG, "inserted audio uri: " + audioUri);

            String[] projection = new String[] {
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE
            };
            Cursor c = managedQuery(audioUri, projection, null, null, null);
            if (c.isClosed()) {
                c = managedQuery(audioUri, projection, null, null, null);
            }
            int colArtist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int colTitle = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            c.moveToLast();

            String song = c.getString(colArtist) + " - " + c.getString(colTitle);
            c.close();
            Log.d(TAG, song);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(SONG_ADDED_KEY, song);
            editor.commit();
            mSongName.setText(mSharedPreferences.getString(SONG_ADDED_KEY, "Default song"));

            refresh();
        }
    }

    private void refresh() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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

}
