package ru.wheelman.weather.presentation.view_model;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import ru.wheelman.weather.BR;
import ru.wheelman.weather.di.modules.CurrentWeatherViewModelModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.CurrentWeatherViewModelScope;
import ru.wheelman.weather.di.scopes.MainActivityViewModelScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;
import ru.wheelman.weather.domain.interactors.CurrentWeatherInteractor;
import ru.wheelman.weather.presentation.data_mappers.DataMapper;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import toothpick.Scope;
import toothpick.Toothpick;

public class CurrentWeatherViewModelImpl extends ViewModel implements CurrentWeatherViewModel {

    private static final String TAG = CurrentWeatherViewModelImpl.class.getSimpleName();

    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    CurrentWeatherInteractor currentWeatherInteractor;
    @Inject
    Context context;
    @Inject
    UpdateMethodSelector updateMethodSelector;
    @Inject
    DataMapper<CurrentWeatherConditions, ScreenState> dataMapper;

    private PreferenceHelper.LatestCityIdListener latestCityIdListener;
    private LiveData<CurrentWeatherConditions> liveCurrentWeatherConditions;
    private Observer<CurrentWeatherConditions> currentWeatherConditionsObserver;
    private ScreenState screenState;
    private PreferenceHelper.CurrentWeatherUpdateStatusListener currentWeatherUpdateStatusListener;

    public CurrentWeatherViewModelImpl() {
        initToothpick();

        initVariables();

        initListeners();

    }

    private void initVariables() {
        screenState = new ScreenState();
    }

    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, MainActivityViewModelScope.class, CurrentWeatherViewModelScope.class);
        scope.installModules(new CurrentWeatherViewModelModule());
        Toothpick.inject(this, scope);
    }

    private void initListeners() {
        latestCityIdListener = this::updateWeather;

        currentWeatherConditionsObserver = currentWeatherConditions -> {
            if (currentWeatherConditions != null) {
                clearScreen();
                dataMapper.map(currentWeatherConditions, screenState);
            }
        };

        currentWeatherUpdateStatusListener = () -> {
            screenState.setCurrentWeatherIsBeingUpdated(preferenceHelper.currentWeatherIsBeingUpdated());
        };

    }

    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

    private void updateWeather() {
        Log.d(TAG, "latest city id changed");
        removeCurrentWeatherConditionsObserver();

        liveCurrentWeatherConditions = currentWeatherInteractor.getCurrentWeatherConditionsByCityId();

        liveCurrentWeatherConditions.observeForever(currentWeatherConditionsObserver);
    }

    private void removeCurrentWeatherConditionsObserver() {
        if (liveCurrentWeatherConditions != null && liveCurrentWeatherConditions.hasObservers()) {
            liveCurrentWeatherConditions.removeObserver(currentWeatherConditionsObserver);
        }
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
//        Database.destroyInstance();
        removeCurrentWeatherConditionsObserver();
        Toothpick.closeScope(CurrentWeatherViewModelScope.class);
        super.onCleared();
    }

    @Override
    public void onRefreshSwipeRefreshLayout() {
        updateMethodSelector.selectAndUpdate();

        Boolean internetConnected = updateMethodSelector.getInternetConnected().getValue();
        if (internetConnected != null && !internetConnected) {
            screenState.setCurrentWeatherIsBeingUpdated(false);
        }
    }

    @Override
    public void onStart() {
        preferenceHelper.subscribeToLatestCityIdChanges(latestCityIdListener);
        preferenceHelper.subscribeToCurrentWeatherUpdateStatusChanges(currentWeatherUpdateStatusListener);
    }

    @Override
    public void onStop() {
        preferenceHelper.unsubscribeFromLatestCityIdChanges(latestCityIdListener);
        preferenceHelper.unsubscribeFromCurrentWeatherUpdateStatusChanges(currentWeatherUpdateStatusListener);
    }

    @Override
    public void onViewCreated() {
        updateWeather();
    }

    private void clearScreen() {
        screenState.setDataReceivingTime(null);
        screenState.setTemperature(null);
        screenState.setWeatherIconURL(null);
        screenState.setWeatherConditionDescription(null);
        screenState.setBackgroundURI(null);
        screenState.setUpdateTime(null);
    }

    public static class ScreenState extends BaseObservable {
        private boolean currentWeatherIsBeingUpdated;
        private String dataReceivingTime;
        private String temperature;
        private String weatherIconURL;
        private String weatherConditionDescription;
        private Uri backgroundURI;
        private String updateTime;
        private boolean backgroundIsDark;

        @Bindable
        public boolean isBackgroundIsDark() {
            return backgroundIsDark;
        }

        public void setBackgroundIsDark(boolean backgroundIsDark) {
            this.backgroundIsDark = backgroundIsDark;
            notifyPropertyChanged(BR.backgroundIsDark);
        }

        @Bindable
        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
            notifyPropertyChanged(BR.updateTime);
        }

        @Bindable
        public String getDataReceivingTime() {
            return dataReceivingTime;
        }

        public void setDataReceivingTime(String dataReceivingTime) {
            this.dataReceivingTime = dataReceivingTime;
            notifyPropertyChanged(BR.dataReceivingTime);
        }

        @Bindable
        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
            notifyPropertyChanged(BR.temperature);
        }

        @Bindable
        public String getWeatherIconURL() {
            return weatherIconURL;
        }

        public void setWeatherIconURL(String weatherIconURL) {
            this.weatherIconURL = weatherIconURL;
            notifyPropertyChanged(BR.weatherIconURL);
        }

        @Bindable
        public String getWeatherConditionDescription() {
            return weatherConditionDescription;
        }

        public void setWeatherConditionDescription(String weatherConditionDescription) {
            this.weatherConditionDescription = weatherConditionDescription;
            notifyPropertyChanged(BR.weatherConditionDescription);
        }

        @Bindable
        public Uri getBackgroundURI() {
            return backgroundURI;
        }

        public void setBackgroundURI(Uri backgroundURI) {
            this.backgroundURI = backgroundURI;
            notifyPropertyChanged(BR.backgroundURI);
        }

        @Bindable
        public boolean getCurrentWeatherIsBeingUpdated() {
            return currentWeatherIsBeingUpdated;
        }

        public void setCurrentWeatherIsBeingUpdated(boolean currentWeatherIsBeingUpdated) {
            this.currentWeatherIsBeingUpdated = currentWeatherIsBeingUpdated;
            notifyPropertyChanged(BR.currentWeatherIsBeingUpdated);
        }
    }
}
