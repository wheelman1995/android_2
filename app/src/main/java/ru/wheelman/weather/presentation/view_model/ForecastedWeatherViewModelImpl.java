package ru.wheelman.weather.presentation.view_model;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import ru.wheelman.weather.domain.entities.FiveDayForecast;
import ru.wheelman.weather.domain.interactors.ForecastedWeatherInteractor;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;

public class ForecastedWeatherViewModelImpl extends ViewModel implements ForecastedWeatherViewModel {

    private final ScreenState screenState;
    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    UpdateMethodSelector updateMethodSelector;
    @Inject
    ForecastedWeatherInteractor forecastedWeatherInteractor;
    private PreferenceHelper.LatestCityIdListener latestCityIdListener;
    private LiveData<FiveDayForecast> liveFiveDayForecast;
    private Observer<FiveDayForecast> fiveDayForecastObserver;
    private PreferenceHelper.ForecastedWeatherUpdateStatusListener forecastedWeatherUpdateStatusListener;

    public ForecastedWeatherViewModelImpl() {

        screenState = new ScreenState();
        initListeners();
    }

    private void initListeners() {
        latestCityIdListener = this::updateWeather;

        fiveDayForecastObserver = this::processNewFiveDayForecast;

        forecastedWeatherUpdateStatusListener = () -> screenState.setForecastedWeatherIsBeingUpdated(preferenceHelper.forecastedWeatherIsBeingUpdated());
    }

    private void processNewFiveDayForecast(FiveDayForecast fiveDayForecast) {

    }

    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

    @Override
    public void onRefreshSwipeRefreshLayout() {
        updateMethodSelector.selectAndUpdate();
        preferenceHelper.setForecastedWeatherIsBeingUpdated(true);
    }

    private void updateWeather() {
        removeForecastedWeatherConditionsObserver();

        liveFiveDayForecast = forecastedWeatherInteractor.getFiveDayForecastByCityId();

        liveFiveDayForecast.observeForever(fiveDayForecastObserver);
    }

    private void removeForecastedWeatherConditionsObserver() {
        if (liveFiveDayForecast.hasObservers()) {
            liveFiveDayForecast.removeObserver(fiveDayForecastObserver);
        }
    }

    @Override
    public void onStart() {
        preferenceHelper.subscribeToLatestCityIdChanges(latestCityIdListener);
        preferenceHelper.subscribeToForecastedWeatherUpdateStatusChanges(forecastedWeatherUpdateStatusListener);
    }

    @Override
    public void onStop() {
        preferenceHelper.unsubscribeFromLatestCityIdChanges(latestCityIdListener);
        preferenceHelper.unsubscribeFromForecastedWeatherUpdateStatusChanges(forecastedWeatherUpdateStatusListener);
    }

    @Override
    protected void onCleared() {
        removeForecastedWeatherConditionsObserver();
        super.onCleared();
    }

    public static class ScreenState extends BaseObservable {
        private boolean forecastedWeatherIsBeingUpdated;

        public boolean isForecastedWeatherIsBeingUpdated() {
            return forecastedWeatherIsBeingUpdated;
        }

        public void setForecastedWeatherIsBeingUpdated(boolean forecastedWeatherIsBeingUpdated) {
            this.forecastedWeatherIsBeingUpdated = forecastedWeatherIsBeingUpdated;
        }
    }

    static class AdapterData {
        private String[] dates;
        private String[] weatherConditionDescriptions;
        private String[] weatherIconURLs;
        private String[] dayTemperatures;
        private String[] nightTemperatures;
    }
}
