package ru.wheelman.weather.data.repositories;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.data.data_sources.databases.ForecastedWeatherLocalDataSource;
import ru.wheelman.weather.data.data_sources.network.forecasted.ForecastedWeatherRemoteDataSource;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

@ApplicationScope
public class ForecastedWeatherRepositoryImpl implements ForecastedWeatherRepository {

    private ForecastedWeatherRemoteDataSource forecastedWeatherRemoteDataSource;
    private ForecastedWeatherLocalDataSource forecastedWeatherLocalDataSource;

    @Inject
    public ForecastedWeatherRepositoryImpl(ForecastedWeatherRemoteDataSource forecastedWeatherRemoteDataSource, ForecastedWeatherLocalDataSource forecastedWeatherLocalDataSource) {
        this.forecastedWeatherRemoteDataSource = forecastedWeatherRemoteDataSource;
        this.forecastedWeatherLocalDataSource = forecastedWeatherLocalDataSource;
    }

    @Override
    public LiveData<FiveDayForecast> getFiveDayForecastByCityId() {
        return forecastedWeatherLocalDataSource.getFiveDayForecastByCityId();
    }

    @Override
    public void updateFiveDayForecastByCityId() {
        FiveDayForecast fiveDayForecast = forecastedWeatherRemoteDataSource.requestFiveDayForecastByCityId();
        forecastedWeatherLocalDataSource.saveFiveDayForecast(fiveDayForecast);
    }

    @Override
    public void updateFiveDayForecastByCoordinates() {
        FiveDayForecast fiveDayForecast = forecastedWeatherRemoteDataSource.requestFiveDayForecastByCoordinates();
        forecastedWeatherLocalDataSource.saveFiveDayForecast(fiveDayForecast);
    }
}
