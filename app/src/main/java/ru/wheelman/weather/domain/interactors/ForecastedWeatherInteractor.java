package ru.wheelman.weather.domain.interactors;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

public interface ForecastedWeatherInteractor {
    LiveData<FiveDayForecast> getFiveDayForecastByCityId();

    void updateFiveDayForecastByCityId();

    void updateFiveDayForecastByCoordinates();
}
