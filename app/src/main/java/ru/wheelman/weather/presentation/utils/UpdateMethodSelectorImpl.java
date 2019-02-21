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

    @Inject
    CurrentWeatherUpdateWorkerHelper currentWeatherUpdateWorkerHelper;
    @Inject
    ForecastedWeatherUpdateWorkerHelper forecastedWeatherUpdateWorkerHelper;
    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    LocationHelper locationHelper;
    @Inject
    ISearchSuggestionsProvider suggestionsProvider;
    @Inject
    IConnectivityHelper connectivityHelper;

    private MutableLiveData<Boolean> internetConnected;

    @Inject
    UpdateMethodSelectorImpl() {
        internetConnected = new MutableLiveData<>();
    }

    @Override
    public MutableLiveData<Boolean> getInternetConnected() {
        return internetConnected;
    }

    private boolean checkInternetConnected() {
        boolean internetConnected = connectivityHelper.isInternetConnected();
        this.internetConnected.setValue(internetConnected);
        return internetConnected;
    }

    @Override
    public void selectAndUpdate(int cityId) {
        checkInternetConnected();

        if (suggestionsProvider.useYourLocationWasSelected(cityId)) {
            safeStartListeningToLocationChanges();
        } else {
            safeStopListeningToLocationChanges();

            preferenceHelper.setLatestCityId(cityId);
            updateByCityId();
        }
    }

    private void updateByCityId() {
        if (preferenceHelper.cityIdIsValid()) {
            setDataIsBeingUpdated();
            currentWeatherUpdateWorkerHelper.updateCurrentWeatherConditionsByCityId();
            forecastedWeatherUpdateWorkerHelper.updateFiveDayForecastByCityId();
        }
    }

    private void setDataIsBeingUpdated() {
        preferenceHelper.setCurrentWeatherIsBeingUpdated(true);
        preferenceHelper.setForecastedWeatherIsBeingUpdated(true);
    }

    @Override
    public void selectAndUpdate() {
        checkInternetConnected();

        if (preferenceHelper.isListeningToLocationChanges()) {
            forceRestartLocationListener();
        } else {
            updateByCityId();
        }
    }

    private void forceRestartLocationListener() {
        locationHelper.stopListeningToLocationChanges();
        locationHelper.startListeningToLocationChanges();
    }

    @Override
    public void updateByCoordinates() {
        if (locationHelper.coordinatesAreValid()) {
            setDataIsBeingUpdated();
            currentWeatherUpdateWorkerHelper.updateCurrentWeatherConditionsByCoordinates();
            forecastedWeatherUpdateWorkerHelper.updateFiveDayForecastByCoordinates();
        }
    }


    @Override
    public void appRestoredWithoutLocationServices() {
        checkInternetConnected();

        if (preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.stopListeningToLocationChanges();
            updateByCityId();
        }
    }

    @Override
    public void onAppNormalStart(boolean updateByLocationAllowed) {
        checkInternetConnected();

        if (updateByLocationAllowed && preferenceHelper.isListeningToLocationChanges()) {
            Log.d(TAG, "onAppNormalStart perm list");
            locationHelper.startListeningToLocationChanges();
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
        checkInternetConnected();

        if (updateByLocationAllowed) {
            Log.d(TAG, "onAppFirstStart updateByLocationAllowed");
            safeStartListeningToLocationChanges();
        } else {
            Log.d(TAG, "onAppFirstStart !updateByLocationAllowed");
            safeStopListeningToLocationChanges();
        }
    }

    private void safeStartListeningToLocationChanges() {
        if (!preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.startListeningToLocationChanges();
        }
    }

    private void safeStopListeningToLocationChanges() {
        if (preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.stopListeningToLocationChanges();
        }
    }

}
