package ru.wheelman.weather.data.data_sources.databases;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

public interface ForecastedWeatherLocalDataSource {
    LiveData<FiveDayForecast> getFiveDayForecastByCityId();

    void saveFiveDayForecast(FiveDayForecast fiveDayForecast);
}
