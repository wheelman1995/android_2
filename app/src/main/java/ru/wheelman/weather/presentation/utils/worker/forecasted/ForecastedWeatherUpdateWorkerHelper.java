package ru.wheelman.weather.presentation.utils.worker.forecasted;

public interface ForecastedWeatherUpdateWorkerHelper {
    void updateFiveDayForecastByCityId();

    void updateFiveDayForecastByCoordinates();
}
