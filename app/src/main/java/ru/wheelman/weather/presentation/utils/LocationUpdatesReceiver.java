package ru.wheelman.weather.presentation.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import javax.inject.Inject;

import ru.wheelman.weather.di.scopes.ApplicationScope;
import toothpick.Toothpick;

public class LocationUpdatesReceiver extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATES = "ru.wheelman.weather.presentation.utils.PROCESS_UPDATES";
    private static final String TAG = LocationUpdatesReceiver.class.getSimpleName();

    @Inject
    UpdateMethodSelector updateMethodSelector;

    @Inject
    PreferenceHelper preferenceHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toothpick.inject(this, Toothpick.openScope(ApplicationScope.class));
        Log.d(TAG, "onReceive");
        if (intent != null) {
            Log.d(TAG, "onReceive: intent != null");
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {

                LocationResult result = LocationResult.extractResult(intent);
                Log.d(TAG, "onReceive: result !=null " + (result != null));
                if (result != null) {
                    Location lastLocation = result.getLastLocation();
                    double latitude = lastLocation.getLatitude();
                    double longitude = lastLocation.getLongitude();

                    preferenceHelper.setLatitude(latitude);
                    preferenceHelper.setLongitude(longitude);

                    updateMethodSelector.updateByCoordinates();
                    Log.d(TAG, "new location set");

                }
            }
        }
    }
}
