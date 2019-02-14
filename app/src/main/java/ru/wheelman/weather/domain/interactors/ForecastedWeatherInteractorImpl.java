package ru.wheelman.weather.domain.interactors;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.data.repositories.ForecastedWeatherRepository;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

@ApplicationScope
public class ForecastedWeatherInteractorImpl implements ForecastedWeatherInteractor {

    private ForecastedWeatherRepository forecastedWeatherRepository;

    @Inject
    public ForecastedWeatherInteractorImpl(ForecastedWeatherRepository forecastedWeatherRepository) {
        this.forecastedWeatherRepository = forecastedWeatherRepository;
    }

    @Override
    public LiveData<FiveDayForecast> getFiveDayForecastByCityId() {
        return forecastedWeatherRepository.getFiveDayForecastByCityId();
    }

    @Override
    public void updateFiveDayForecastByCityId() {
        forecastedWeatherRepository.updateFiveDayForecastByCityId();
    }

    @Override
    public void updateFiveDayForecastByCoordinates() {
        forecastedWeatherRepository.updateFiveDayForecastByCoordinates();
    }
}
