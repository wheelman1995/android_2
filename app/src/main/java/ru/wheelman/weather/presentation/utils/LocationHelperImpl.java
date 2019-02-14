package ru.wheelman.weather.presentation.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.presentation.view.fragments.SettingsFragment;

@ApplicationScope
public class LocationHelperImpl implements LocationHelper {

    //    private LocationCallback locationCallback;
    private static final long FASTEST_INTERVAL = 1000L * 60L * 30L; // 30 min
    //    private static final long INTERVAL = 1000L * 60L * 60L; // 60 min
    private static final float SMALLEST_DISPLACEMENT = 5_000f; // 5 kilometers
    private static final String TAG = LocationHelperImpl.class.getSimpleName();
    private static double MAX_LAT = 90d;
    private static double MIN_LAT = -90d;
    private static double MAX_LON = 180d;
    private static double MIN_LON = -180d;
    private Context context;
    private PreferenceHelper preferenceHelper;
    //    @Inject
//    PreferenceHelper preferenceHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Inject
    public LocationHelperImpl(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = new LocationRequest()
                .setFastestInterval(FASTEST_INTERVAL)
                .setInterval(SettingsFragment.UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER);
//                .setSmallestDisplacement(SMALLEST_DISPLACEMENT);

//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult != null) {
//                    Location location = locationResult.getLastLocation();
//                    double lat = location.getLatitude();
//                    double lon = location.getLongitude();
//
//                    preferenceHelper.setLocation(lat, lon);
////
////                    float[] results = new float[1];
////
////                    if (-90d <= latitude && latitude <= 90d && -180d <= longitude && longitude <= 180d) {
////                        Location.distanceBetween(latitude, longitude, lat, lon, results);
////                        if (results[0] < 5000) {
////                            return;
//////                            saveNewLatLonAndRecreateWork(lat, lon);
////                        }
//////                        else {
//////                            subscribeForDataUpdates();
//////                        }
////                    }
////                    saveNewLatLonAndRecreateWork(lat, lon);
//////                    Log.d(TAG, String.valueOf(latitude) + " " + longitude + " onLocationResult");
//                }
//            }
//        };
    }

    @Inject
    void setPreferenceHelper(PreferenceHelper preferenceHelper) {
        this.preferenceHelper = preferenceHelper;
    }

    @Override
    public boolean coordinatesAreValid() {

        double lat = preferenceHelper.getLatitude();
        double lon = preferenceHelper.getLongitude();

        return MIN_LAT <= lat && lat <= MAX_LAT &&
                MIN_LON <= lon && lon <= MAX_LON;
    }

    @Override
    @SuppressLint("MissingPermission")
    public void startListeningToLocationChanges() {
        Log.d(TAG, "startListeningToLocationChanges");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        preferenceHelper.setListeningToLocationChanges(true);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, LocationUpdatesReceiver.class);
        intent.setAction(LocationUpdatesReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void stopListeningToLocationChanges() {
        Log.d(TAG, "stopListeningToLocationChanges");
        fusedLocationProviderClient.removeLocationUpdates(getPendingIntent());
        preferenceHelper.setListeningToLocationChanges(false);
    }
}
