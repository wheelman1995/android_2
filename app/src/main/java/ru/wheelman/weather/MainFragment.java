package ru.wheelman.weather;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private TextView dataReceivingTime;
    private TextView currentTemperature;
    private BroadcastReceiver bReceiver;
    private WeatherViewModel weatherViewModel;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    //    private String unitsStringRes;
    private PeriodicWorkRequest weatherUpdateWork;
    private int cityId;
    private int unitIndex;
    private Observer<WeatherData> weatherDataObserver;

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();

//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        initVariables();

        initListeners();
    }

    private void initVariables() {
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        //todo: default value = current location
        cityId = sp.getInt(Constants.SHARED_PREFERENCES_CURRENT_CITY_ID, -1);
        unitIndex = sp.getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex());

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
        createPeriodicWork(cityId, unitIndex, true);
    }

    private void initListeners() {

        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case Constants.SHARED_PREFERENCES_CURRENT_CITY_ID:
                        cityId = sharedPreferences.getInt(key, -1);
                        onNewCitySelected();
                        break;
                    case Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY:
                        unitIndex = sharedPreferences.getInt(key, Units.CELSIUS.getUnitIndex());
                        onUnitsChanged();
                        break;
                }
            }
        };

        weatherDataObserver = new Observer<WeatherData>() {
            @Override
            public void onChanged(WeatherData weatherData) {
                ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
                if (weatherData == null) {
                    actionBar.setTitle(getString(R.string.waiting_for_data));
                    dataReceivingTime.setText(getString(R.string.not_applicable));
                    currentTemperature.setText(getString(R.string.not_applicable));
                }
                if (weatherData != null) {
                    Date date = new Date(weatherData.getDt() * 1000L);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, HH:mm", Locale.UK);
                    sdf.setTimeZone(TimeZone.getDefault());


                    dataReceivingTime.setText(sdf.format(date));

//                    chooseUnitStringResource();

                    currentTemperature.setText(weatherData.getTemperature());

                    actionBar.setTitle(String.format(Locale.UK, "%s, %s", weatherData.getCity(), weatherData.getCountry()));
                }
            }
        };

        getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private void onNewCitySelected() {
        createPeriodicWork(cityId, unitIndex, true);
        subscribeForDataUpdates();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        dataReceivingTime = view.findViewById(R.id.tv_data_receiving_time);
        currentTemperature = view.findViewById(R.id.tv_current_temperature_value);

//        bReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                WeatherDataModel weatherDataModel = intent.getParcelableExtra("WeatherDataModel");
//                Main main = weatherDataModel.getMain();
//
//                Date date = new Date(weatherDataModel.getDt() * 1000L);
//                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, HH:mm", Locale.UK);
//                sdf.setTimeZone(TimeZone.getDefault());
//
//
//                dataReceivingTime.setText(sdf.format(date));
//                currentTemperature.setText(String.format(Locale.ENGLISH, "%.1f%s", main.getTemp(), getString(R.string.celsius)));
//
//            }
//        };

//         Create the observer which updates the UI.
//        Observer<WorkStatus> observer = new Observer<WorkStatus>() {
//            @Override
//            public void onChanged(@Nullable WorkStatus status) {
//                // Update the UI
//                if (status != null && status.getState().isFinished()) {
//                    String data = status.getOutputData().getString("data");
//                    Log.d(TAG, data);
//                }
//            }
//        };

//        Observer<List<WorkStatus>> observer = new Observer<List<WorkStatus>>() {
//            @Override
//            public void onChanged(@Nullable List<WorkStatus> statuses) {
//                // Update the UI
//                for (int i = 0; i < statuses.size(); i++) {
//                    if (statuses.get(i) != null)
//                    Log.d(TAG, statuses.get(i).getOutputData().getString("data"));
//                }
//            }
//        };

//        OneTimeWorkRequest weatherUpdateWork = new OneTimeWorkRequest.Builder(WeatherUpdateWorker.class)
//                .setInputData(new Data.Builder().putString("city", "pskov")
//                        .putString("country", "ru")
//                        .build())
//                .build();


        createPeriodicWork(cityId, unitIndex, false);


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer
//        WorkManager.getInstance().getStatusById(weatherUpdateWork.getId())
//                .observe(this, observer);
//        WorkManager.getInstance().getStatusesByTag("tag")
//                .observe(this, observer);

//        Intent intent = new Intent(getApplicationContext(), WeatherUpdateService.class);
//        WeatherUpdateService.enqueueWork(this, intent);

        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        subscribeForDataUpdates();
        return view;
    }

    private void createPeriodicWork(int cityId, int unitIndex, boolean replaceExistingWork) {
        weatherUpdateWork = new PeriodicWorkRequest.Builder(WeatherUpdateWorker.class, 15, TimeUnit.MINUTES).addTag(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
                .setInputData(new Data.Builder()
                        .putInt(Constants.WORK_MANAGER_DATA_CITY_ID, cityId)
                        .putInt(Constants.WORK_MANAGER_DATA_UNITS, unitIndex)
                        .build())
                .build();

//        ExistingPeriodicWorkPolicy periodicWorkPolicy = replaceExistingWork ? ExistingPeriodicWorkPolicy.REPLACE : ExistingPeriodicWorkPolicy.KEEP;
        if (replaceExistingWork) {
            WorkManager.getInstance().cancelAllWorkByTag(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG);
        }
        WorkManager.getInstance().enqueueUniquePeriodicWork(Constants.WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG, ExistingPeriodicWorkPolicy.KEEP, weatherUpdateWork);
    }

    private void subscribeForDataUpdates() {
        weatherViewModel.getWeatherData(cityId).observe(this, weatherDataObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
//        IntentFilter iFilter = new IntentFilter();
//        iFilter.addAction(Constants.WEATHER_DATA_DOWNLOAD_FINISHED);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(bReceiver, iFilter);
    }

    @Override
    public void onPause() {
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(bReceiver);

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d(TAG, String.valueOf(item.getItemId()));
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onDetach();
    }
}
