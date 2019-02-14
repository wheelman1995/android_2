package ru.wheelman.weather.presentation.utils;

import android.util.Log;

import javax.inject.Inject;

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

    @Inject
    UpdateMethodSelectorImpl() {
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
            if (!preferenceHelper.isListeningToLocationChanges()) {
//            preferenceHelper.subscribeToLocationChanges(listener);
                locationHelper.startListeningToLocationChanges();
            }
        } else {
            if (preferenceHelper.isListeningToLocationChanges()) {
                locationHelper.stopListeningToLocationChanges();
//            preferenceHelper.unsubscribeFromLocationChanges(listener);
            }


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
        if (preferenceHelper.isListeningToLocationChanges()) {
            updateByCoordinates();
        } else {
            updateByCityId();
        }
    }

    @Override
    public void updateByCoordinates() {
        if (locationHelper.coordinatesAreValid()) {
            setDataIsBeingUpdated();
            currentWeatherUpdateWorkerHelper.updateCurrentWeatherConditionsByCoordinates();
            forecastedWeatherUpdateWorkerHelper.updateFiveDayForecastByCoordinates();
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
    public void permissionsRevoked() {
        if (preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.stopListeningToLocationChanges();
            updateByCityId();
        }
    }


    @Override
    public void onAppNormalStart(boolean permissionsGranted) {
        Log.d(TAG, "onAppNormalStart");
        if (preferenceHelper.isListeningToLocationChanges()) {
            locationHelper.startListeningToLocationChanges();
        } else {
            updateByCityId();
        }
    }

    @Override
    public void onAppFirstStart(boolean permissionsGranted) {
        Log.d(TAG, "onAppFirstStart");
        if (permissionsGranted) {
            locationHelper.startListeningToLocationChanges();
        }
    }

}
