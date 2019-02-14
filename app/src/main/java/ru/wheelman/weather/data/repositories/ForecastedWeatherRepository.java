package ru.wheelman.weather.data.repositories;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

public interface ForecastedWeatherRepository {
    LiveData<FiveDayForecast> getFiveDayForecastByCityId();

    void updateFiveDayForecastByCityId();

    void updateFiveDayForecastByCoordinates();
}
