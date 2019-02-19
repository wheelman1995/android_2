package ru.wheelman.weather.presentation.utils;

import android.util.Log;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import ru.wheelman.weather.data.repositories.ISearchSuggestionsProvider;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.presentation.utils.worker.current.CurrentWeatherUpdateWorkerHelper;
import ru.wheelman.weather.presentation.utils.worker.forecasted.ForecastedWeatherUpdateWorkerHelper;

@ApplicationScope
public class UpdateMethodSelectorImpl implements UpdateMethodSelector {
    private static final String TAG = UpdateMethodSelectorImpl.class.getSimpleName();
    private CurrentWeatherUpdateWorkerHelper currentWeatherUpdateWorkerHelper;
    private ForecastedWeatherUpdateWorkerHelper forecastedWeatherUpdateWorkerHelper;
    //    private WeatherUpdateTrigger weatherUpdateTrigger;
//    private WeatherUpdateByLocationTrigger weatherUpdateByLocationTrigger;
    private PreferenceHelper preferenceHelper;
    private LocationHelper locationHelper;
    private ISearchSuggestionsProvider suggestionsProvider;
    private IConnectivityHelper connectivityHelper;
    private MutableLiveData<Boolean> internetConnected;

    @Inject
    UpdateMethodSelectorImpl() {
        internetConnected = new MutableLiveData<>();
    }

    @Override
    public MutableLiveData<Boolean> getInternetConnected() {
        return internetConnected;
    }

    @Inject
    void setConnectivityHelper(IConnectivityHelper connectivityHelper) {
        this.connectivityHelper = connectivityHelper;
    }

    private boolean checkInternetConnected() {
        boolean internetConnected = connectivityHelper.isInternetConnected();
        this.internetConnected.setValue(internetConnected);
        return internetConnected;
    }

    @Inject
    void setSuggestionsProvider(ISearchSuggestionsProvider suggestionsProvider) {
        this.suggestionsProvider = suggestionsProvider;
    }

    @Inject
    void setLocationHelper(LocationHelper locationHelper) {
        this.locationHelper = locationHelper;
    }

    @Inject
    void setCurrentWeatherUpdateWorkerHelper(CurrentWeatherUpdateWorkerHelper currentWeatherUpdateWorkerHelper) {
        this.currentWeatherUpdateWorkerHelper = currentWeatherUpdateWorkerHelper;
    }

    @Inject
    void setForecastedWeatherUpdateWorkerHelper(ForecastedWeatherUpdateWorkerHelper forecastedWeatherUpdateWorkerHelper) {
        this.forecastedWeatherUpdateWorkerHelper = forecastedWeatherUpdateWorkerHelper;
    }

    @Inject
    void setPreferenceHelper(PreferenceHelper preferenceHelper) {
        this.preferenceHelper = preferenceHelper;
    }


    @Override
    public void selectAndUpdate(int cityId) {

        if (suggestionsProvider.useYourLocationWasSelected(cityId)) {
            safeStartListeningToLocationChanges();
        } else {
            safeStopListeningToLocationChanges();

            preferenceHelper.setLatestCityId(cityId);
            updateByCityId();
        }
    }

    private void updateByCityId() {
        if (checkInternetConnected()) {
            if (preferenceHelper.cityIdIsValid()) {
                setDataIsBeingUpdated();
                currentWeatherUpdateWorkerHelper.updateCurrentWeatherConditionsByCityId();
                forecastedWeatherUpdateWorkerHelper.updateFiveDayForecastByCityId();
            }
        }
    }

    private void setDataIsBeingUpdated() {
        preferenceHelper.setCurrentWeatherIsBeingUpdated(true);
        preferenceHelper.setForecastedWeatherIsBeingUpdated(true);
    }

    @Override
    public void selectAndUpdate() {
        if (preferenceHelper.isListeningToLocationChanges()) {
            forceRestartLocationListener();
        } else {
            updateByCityId();
        }
    }

    private void forceRestartLocationListener() {
        if (checkInternetConnected()) {
            locationHelper.stopListeningToLocationChanges();
            locationHelper.startListeningToLocationChanges();
        }
    }

    @Override
    public void updateByCoordinates() {
        if (checkInternetConnected()) {
            if (locationHelper.coordinatesAreValid()) {
                setDataIsBeingUpdated();
                currentWeatherUpdateWorkerHelper.updateCurrentWeatherConditionsByCoordinates();
                forecastedWeatherUpdateWorkerHelper.updateFiveDayForecastByCoordinates();
            }
        }
    }

//    @Inject
//    public void setWeatherUpdateTrigger(WeatherUpdateTrigger weatherUpdateTrigger) {
//        this.weatherUpdateTrigger = weatherUpdateTrigger;
//    }

//    @Inject
//    void setWeatherUpdateByLocationTrigger(WeatherUpdateByLocationTrigger weatherUpdateByLocationTrigger) {
//        this.weatherUpdateByLocationTrigger = weatherUpdateByLocationTrigger;
//    }


    @Override
    public void appRestoredWithoutLocationServices() {
        if (preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.stopListeningToLocationChanges();
            updateByCityId();
        }
    }

    @Override
    public void onAppNormalStart(boolean updateByLocationAllowed) {

        if (updateByLocationAllowed && preferenceHelper.isListeningToLocationChanges()) {
            Log.d(TAG, "onAppNormalStart perm list");
            if (checkInternetConnected()) {
                locationHelper.startListeningToLocationChanges();
            }
            return;
        }

        if (!updateByLocationAllowed && preferenceHelper.isListeningToLocationChanges()) {
            Log.d(TAG, "onAppNormalStart !perm list");
            locationHelper.stopListeningToLocationChanges();
        }
        Log.d(TAG, "onAppNormalStart updateByCityId");

        updateByCityId();
    }

    @Override
    public void onAppFirstStart(boolean updateByLocationAllowed) {
        if (updateByLocationAllowed) {
            Log.d(TAG, "onAppFirstStart updateByLocationAllowed");
            safeStartListeningToLocationChanges();
        } else {
            Log.d(TAG, "onAppFirstStart !updateByLocationAllowed");
            safeStopListeningToLocationChanges();
        }
    }

    private void safeStartListeningToLocationChanges() {
        if (checkInternetConnected()) {
            if (!preferenceHelper.isListeningToLocationChanges()) {
                locationHelper.startListeningToLocationChanges();
            }
        }
    }

    private void safeStopListeningToLocationChanges() {
        if (preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.stopListeningToLocationChanges();
        }
    }

}
