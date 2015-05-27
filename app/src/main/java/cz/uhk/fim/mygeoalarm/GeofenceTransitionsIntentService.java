package cz.uhk.fim.mygeoalarm;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Petr on 27. 5. 2015.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "geofence-transitions-service";

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
