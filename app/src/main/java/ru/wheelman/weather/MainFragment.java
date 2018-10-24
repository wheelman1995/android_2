package ru.wheelman.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    public static final int ID_FOR_VIEW_PAGER = 0;
    public static final int PERMISSION_REQUEST_CODE = 0;
    private static final String TAG = MainFragment.class.getSimpleName();

    private TextView dataReceivingTimeValue;
    private TextView dataReceivingTime;
    private TextView currentTemperature;
    private WeatherViewModel weatherViewModel;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    //    private String unitsStringRes;
    private int cityId;
    private int unitIndex;
    private Observer<WeatherData> weatherDataObserver;
    private ImageView weatherIcon;
    private ImageView mainFragmentBackground;

    private AmbientConditionsFragment ambientConditionsFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;
    private TextView weatherDescription;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BroadcastReceiver dataUpdateStatusReceiver;
    private LiveData<WeatherData> liveWeatherData;

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();

//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);

        return f;
    }

    private void initAmbientConditionsFragment() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null ||
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment, null)
                    .commit();
        }
    }

    private void initVariables(View view) {
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        //default value = current location
        cityId = sp.getInt(Constants.SHARED_PREFERENCES_CURRENT_CITY_ID, SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID);
        unitIndex = sp.getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = new LocationRequest().setFastestInterval(1000L * 60L * 30L)
                .setInterval(1000L * 60L * 60L)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setSmallestDisplacement(5_000f);
        swipeRefreshLayout = view.findViewById(R.id.srl_fragment_main);
        dataReceivingTimeValue = view.findViewById(R.id.tv_data_receiving_time_value);
        currentTemperature = view.findViewById(R.id.tv_current_temperature_value);
        weatherIcon = view.findViewById(R.id.iv_weather_icon);
        weatherDescription = view.findViewById(R.id.tv_weather_condition_description);
        mainFragmentBackground = view.findViewById(R.id.iv_fragment_main_background);


//        Matrix matrix = new Matrix(mainFragmentBackground.getImageMatrix());
//        Log.d(TAG, matrix.toString());
//        matrix.setScale(0.4f, 0.4f);
//        mainFragmentBackground.setImageMatrix(matrix);

        dataReceivingTime = view.findViewById(R.id.tv_data_receiving_time);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
    }


//    private void chooseUnitStringResource() {
//        switch (Units.getUnitByIndex(unitIndex)) {
//            case CELSIUS:
//                unitsStringRes = getString(R.string.celsius);
//                break;
//            case FAHRENHEIT:
//                unitsStringRes = getString(R.string.fahrenheit);
//                break;
//        }
//    }

    private void onUnitsChanged() {
        recreateAmbientConditionsFragment();
        if (cityId == SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID) {
            createPeriodicWork(cityId, unitIndex, latitude, longitude, true);
            return;
        }
        createPeriodicWork(cityId, unitIndex, true);
    }

    private void recreateAmbientConditionsFragment() {
        if (ambientConditionsFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(ambientConditionsFragment)
                    .commit();
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment)
                    .commit();
        }
    }

    private void initListeners() {
        dataUpdateStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataUpdateStatusReceiver, new IntentFilter(WeatherUpdateWorker.ACTION_NEW_DATA_RECEIVED));

        onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
            switch (key) {
                case Constants.SHARED_PREFERENCES_CURRENT_CITY_ID:
                    cityId = sharedPreferences.getInt(key, SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID);
                    onNewCitySelected();
                    break;
                case Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY:
                    unitIndex = sharedPreferences.getInt(key, Units.CELSIUS.getUnitIndex());
                    onUnitsChanged();
                    break;
            }
        };

        weatherDataObserver = new Observer<WeatherData>() {
            @Override
            public void onChanged(WeatherData weatherData) {
                ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
                if (weatherData == null) {
                    actionBar.setTitle(getString(R.string.waiting_for_data));
                    dataReceivingTimeValue.setText(getString(R.string.not_applicable));
                    currentTemperature.setText(getString(R.string.not_applicable));
                    weatherIcon.setImageAlpha(0);
                    weatherDescription.setText(R.string.not_applicable);
                }
                if (weatherData != null) {

                    String icon = weatherData.getWeatherIcon();
                    long sunset = weatherData.getSunset();
                    long sunrise = weatherData.getSunrise();

                    ((MainActivity) getActivity()).setNavDrawerHeaderContent(sunset, sunrise);

                    Date date = new Date(weatherData.getDt() * 1000L);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM, HH:mm", Locale.UK);
                    sdf.setTimeZone(TimeZone.getDefault());


                    dataReceivingTimeValue.setText(sdf.format(date));

                    currentTemperature.setText(weatherData.getTemperature());

                    actionBar.setTitle(String.format(Locale.UK, "%s, %s", weatherData.getCity(), weatherData.getCountry()));

                    weatherIcon.setImageAlpha(255);
                    Picasso.get().load(String.format(Locale.UK, "%s%s.png", Constants.WEATHER_ICON_URL_BASE, icon)).into(weatherIcon);

                    setBackgroundImage(icon);

                    weatherDescription.setText(weatherData.getWeatherDescription());

                    boolean day = icon.endsWith("d");
                    int textColorId = day ? getResources().getColor(android.R.color.black) : getResources().getColor(android.R.color.white);
                    setTextColor(textColorId);
                    int textShadowColorId = day ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black);
                    setTextShadowColor(textShadowColorId);
                }
            }

            private void setBackgroundImage(String icon) {
                mainFragmentBackground.setImageURI(Uri.parse(String.format(Locale.UK, "android.resource://%s/drawable/b%s", getActivity().getPackageName(), icon)));

                Matrix matrix = new Matrix(mainFragmentBackground.getImageMatrix());

                float scale;
                float viewWidth = mainFragmentBackground.getWidth() - mainFragmentBackground.getPaddingLeft() - mainFragmentBackground.getPaddingRight();
                float viewHeight = mainFragmentBackground.getHeight() - mainFragmentBackground.getPaddingTop() - mainFragmentBackground.getPaddingBottom();
                float drawableWidth = mainFragmentBackground.getDrawable().getIntrinsicWidth();
                float drawableHeight = mainFragmentBackground.getDrawable().getIntrinsicHeight();

                //crop either image start and end equally, or only the bottom
                if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
                    scale = viewHeight / drawableHeight;
                    float dx = -(drawableWidth * scale - viewWidth) / 2f;
                    matrix.postTranslate(dx, 0f);
                } else {
                    scale = viewWidth / drawableWidth;
                }
                //
                matrix.setScale(scale, scale);

                mainFragmentBackground.setImageMatrix(matrix);
            }

            private void setTextColor(int colorId) {

                dataReceivingTime.setTextColor(colorId);
                currentTemperature.setTextColor(colorId);
                weatherDescription.setTextColor(colorId);
                dataReceivingTimeValue.setTextColor(colorId);

            }

            private void setTextShadowColor(int colorId) {

                dataReceivingTime.setShadowLayer(10f, 0f, 0f, colorId);
                currentTemperature.setShadowLayer(10f, 0f, 0f, colorId);
                weatherDescription.setShadowLayer(10f, 0f, 0f, colorId);
                dataReceivingTimeValue.setShadowLayer(10f, 0f, 0f, colorId);

            }
        };

        getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    Log.d(TAG, String.valueOf(latitude) + " " + longitude + " onLocationResult");
                    createPeriodicWork(cityId, unitIndex, latitude, longitude, true);
                }
            }
        };
        swipeRefreshLayout.setOnRefreshListener(this::refreshManually);
    }

    void refreshManually() {
        if (cityId == SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID) {
            createPeriodicWork(cityId, unitIndex, latitude, longitude, true);
        } else {
            createPeriodicWork(cityId, unitIndex, true);
        }
    }

    private void onNewCitySelected() {
        if (cityId == SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "onNewCitySelected requestPermissions");
                requestPermissions();
                return;
            } else {
                subscribeForLocationUpdates();
                return;
            }
        }
        createPeriodicWork(cityId, unitIndex, true);
    }

    private void requestPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showRationale();
//            Log.d(TAG, "showRationale");

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
//            Log.d(TAG, "requestPermissions");
        }
    }

    private void showRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.rationale_title))
                .setIcon(R.drawable.rationale_icon)
                .setMessage(R.string.location_permission_rationale)
                .setPositiveButton(R.string.location_permission_rationale_positive_button, (dialog, which) -> {
//                        getActivity().onBackPressed();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeForLocationUpdates();
            } else {
//                Log.d(TAG, "PERMISSION_NOT_GRANTED onRequestPermissionsResult");
                SearchSuggestionsProvider.setCurrentLocationSuggestionEnabled(false);
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                    showPermissionDeniedWarning();
            }
        }
    }

    private void showPermissionDeniedWarning() {
        //show you will not be able to automatically receive weather for your location until you manually grant the location permission!
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.permission_denied_warning_title))
                .setIcon(R.drawable.location_permission_denied_warning_icon)
                .setMessage(R.string.location_permission_denied_warning)
                .setPositiveButton(R.string.location_permission_warning_positive_button, (dialog, which) -> {
//                        getActivity().onBackPressed();
                });
        builder.create().show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initVariables(view);

        initListeners();

        initAmbientConditionsFragment();


        if (cityId != SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID) {
            createPeriodicWork(cityId, unitIndex, false);
        }



        return view;
    }

    private void createPeriodicWork(int cityId, int unitIndex, boolean replaceExistingWork) {
        PeriodicWorkRequest weatherUpdateWork = new PeriodicWorkRequest.Builder(WeatherUpdateWorker.class, 60, TimeUnit.MINUTES).addTag(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .setInputData(new Data.Builder()
                        .putInt(Constants.WORK_MANAGER_DATA_CITY_ID, cityId)
                        .putInt(Constants.WORK_MANAGER_DATA_UNITS, unitIndex)
                        .putInt(Constants.WORK_MANAGER_DATA_WORK_TYPE, WeatherUpdateWorker.WORK_TYPE_LOAD_CURRENT_WEATHER)
                        .build())
                .build();

//        ExistingPeriodicWorkPolicy periodicWorkPolicy = replaceExistingWork ? ExistingPeriodicWorkPolicy.REPLACE : ExistingPeriodicWorkPolicy.KEEP;
        if (replaceExistingWork) {
            WorkManager.getInstance().cancelAllWorkByTag(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG);
        }
        WorkManager.getInstance().enqueueUniquePeriodicWork(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG, ExistingPeriodicWorkPolicy.KEEP, weatherUpdateWork);
        subscribeForDataUpdates();
    }

    private void createPeriodicWork(int cityId, int unitIndex, double latitude, double longitude, boolean replaceExistingWork) {
        PeriodicWorkRequest weatherUpdateWork = new PeriodicWorkRequest.Builder(WeatherUpdateWorker.class, 60, TimeUnit.MINUTES).addTag(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .setInputData(new Data.Builder()
                        .putInt(Constants.WORK_MANAGER_DATA_CITY_ID, cityId)
                        .putInt(Constants.WORK_MANAGER_DATA_UNITS, unitIndex)
                        .putDouble(Constants.WORK_MANAGER_DATA_LONGITUDE, longitude)
                        .putDouble(Constants.WORK_MANAGER_DATA_LATITUDE, latitude)
                        .putInt(Constants.WORK_MANAGER_DATA_WORK_TYPE, WeatherUpdateWorker.WORK_TYPE_LOAD_CURRENT_WEATHER_BY_COORDINATES)
                        .build())
                .build();

//        ExistingPeriodicWorkPolicy periodicWorkPolicy = replaceExistingWork ? ExistingPeriodicWorkPolicy.REPLACE : ExistingPeriodicWorkPolicy.KEEP;
        if (replaceExistingWork) {
            WorkManager.getInstance().cancelAllWorkByTag(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG);
        }
        WorkManager.getInstance().enqueueUniquePeriodicWork(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG, ExistingPeriodicWorkPolicy.KEEP, weatherUpdateWork);
        subscribeForDataUpdates();
    }

    private void subscribeForDataUpdates() {
        if (liveWeatherData != null) {
            if (liveWeatherData.hasObservers()) {
                liveWeatherData.removeObservers(this);
            }
        }
        liveWeatherData = weatherViewModel.getWeatherData(cityId);
        liveWeatherData.observe(this, weatherDataObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume");

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SearchSuggestionsProvider.setCurrentLocationSuggestionEnabled(true);
            if (cityId == SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID) {
                subscribeForLocationUpdates();
            }
        } else {
            SearchSuggestionsProvider.setCurrentLocationSuggestionEnabled(false);
        }
    }

    @SuppressLint("MissingPermission")
    private void subscribeForLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//        Log.d(TAG, "subscribeForLocationUpdates");
    }

    @Override
    public void onPause() {
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(bReceiver);
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onPause();
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataUpdateStatusReceiver);
        getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onDetach();
    }
}
