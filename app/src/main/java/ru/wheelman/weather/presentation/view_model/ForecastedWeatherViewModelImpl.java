package ru.wheelman.weather.presentation.view_model;

import android.util.Log;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import io.reactivex.subjects.BehaviorSubject;
import ru.wheelman.weather.BR;
import ru.wheelman.weather.di.modules.ForecastedWeatherViewModelModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.ForecastedWeatherViewModelScope;
import ru.wheelman.weather.domain.entities.FiveDayForecast;
import ru.wheelman.weather.domain.interactors.ForecastedWeatherInteractor;
import ru.wheelman.weather.presentation.data_mappers.DataMapper;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import toothpick.Scope;
import toothpick.Toothpick;

public class ForecastedWeatherViewModelImpl extends ViewModel implements ForecastedWeatherViewModel {

    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    UpdateMethodSelector updateMethodSelector;
    @Inject
    ForecastedWeatherInteractor forecastedWeatherInteractor;
    private static final String TAG = ForecastedWeatherViewModelImpl.class.getSimpleName();
    @Inject
    DataMapper<FiveDayForecast, AdapterViewModel> dataMapper;
    @Inject
    AdapterViewModel adapterViewModel;
    private PreferenceHelper.LatestCityIdListener latestCityIdListener;
    private LiveData<FiveDayForecast> liveFiveDayForecast;
    private Observer<FiveDayForecast> fiveDayForecastObserver;
    private PreferenceHelper.ForecastedWeatherUpdateStatusListener forecastedWeatherUpdateStatusListener;
    private ScreenState screenState;

    public ForecastedWeatherViewModelImpl() {
        initToothpick();

        initVariables();

        initListeners();
    }

    private void initVariables() {
        screenState = new ScreenState();
    }

    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, ForecastedWeatherViewModelScope.class);
        scope.installModules(new ForecastedWeatherViewModelModule());
        Toothpick.inject(this, scope);
    }

    private void clearAdapter() {
        adapterViewModel.getDates().clear();
        adapterViewModel.getDayTemperatures().clear();
        adapterViewModel.getNightTemperatures().clear();
        adapterViewModel.getWeatherConditionDescriptions().clear();
        adapterViewModel.getWeatherIconURLs().clear();
    }

    private void initListeners() {
        latestCityIdListener = this::updateWeather;

        fiveDayForecastObserver = fiveDayForecast -> {
            if (fiveDayForecast != null) {
                Log.d(TAG, "initListeners: fivedayforecast not null");
                clearAdapter();
                dataMapper.map(fiveDayForecast, adapterViewModel);
                adapterViewModel.getItemCountSubject().onNext(true);
            }
        };

        forecastedWeatherUpdateStatusListener = () -> screenState.setForecastedWeatherIsBeingUpdated(preferenceHelper.forecastedWeatherIsBeingUpdated());
    }

    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

    @Override
    public void onRefreshSwipeRefreshLayout() {
        updateMethodSelector.selectAndUpdate();

        Boolean internetConnected = updateMethodSelector.getInternetConnected().getValue();
        if (internetConnected != null && !internetConnected) {
            screenState.setForecastedWeatherIsBeingUpdated(false);
        }
    }

    private void updateWeather() {
        removeForecastedWeatherConditionsObserver();

        liveFiveDayForecast = forecastedWeatherInteractor.getFiveDayForecastByCityId();

        liveFiveDayForecast.observeForever(fiveDayForecastObserver);
    }

    private void removeForecastedWeatherConditionsObserver() {
        if (liveFiveDayForecast != null && liveFiveDayForecast.hasObservers()) {
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
    public void onViewCreated() {
        updateWeather();
    }

    @Override
    protected void onCleared() {
        removeForecastedWeatherConditionsObserver();
        Toothpick.closeScope(ForecastedWeatherViewModelScope.class);
        super.onCleared();
    }

    public interface AdapterViewModel {
        BehaviorSubject<Boolean> getItemCountSubject();

        ObservableList<String> getDates();

        ObservableList<String> getWeatherConditionDescriptions();

        ObservableList<String> getWeatherIconURLs();

        ObservableList<String> getDayTemperatures();

        ObservableList<String> getNightTemperatures();

        int getItemCount();
    }

    public static class ScreenState extends BaseObservable {
        private boolean forecastedWeatherIsBeingUpdated;

        @Bindable
        public boolean isForecastedWeatherIsBeingUpdated() {
            return forecastedWeatherIsBeingUpdated;
        }

        public void setForecastedWeatherIsBeingUpdated(boolean forecastedWeatherIsBeingUpdated) {
            this.forecastedWeatherIsBeingUpdated = forecastedWeatherIsBeingUpdated;
            notifyPropertyChanged(BR.forecastedWeatherIsBeingUpdated);
        }
    }

    @ForecastedWeatherViewModelScope
    public static class AdapterViewModelImpl implements AdapterViewModel {
        private ObservableList<String> dates;
        private ObservableList<String> weatherConditionDescriptions;
        private ObservableList<String> weatherIconURLs;
        private ObservableList<String> dayTemperatures;
        private ObservableList<String> nightTemperatures;
        private BehaviorSubject<Boolean> itemCountSubject;

        @Inject
        public AdapterViewModelImpl() {
            itemCountSubject = BehaviorSubject.create();
            initLists();
        }

        @Override
        public BehaviorSubject<Boolean> getItemCountSubject() {
            return itemCountSubject;
        }

        @Override
        public ObservableList<String> getDates() {
            return dates;
        }

        @Override
        public ObservableList<String> getWeatherConditionDescriptions() {
            return weatherConditionDescriptions;
        }

        @Override
        public ObservableList<String> getWeatherIconURLs() {
            return weatherIconURLs;
        }

        @Override
        public ObservableList<String> getDayTemperatures() {
            return dayTemperatures;
        }

        @Override
        public ObservableList<String> getNightTemperatures() {
            return nightTemperatures;
        }

        private void initLists() {
            dates = new ObservableArrayList<>();
            weatherConditionDescriptions = new ObservableArrayList<>();
            weatherIconURLs = new ObservableArrayList<>();
            dayTemperatures = new ObservableArrayList<>();
            nightTemperatures = new ObservableArrayList<>();
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }
    }
}
