package ru.wheelman.weather.presentation.utils.worker.current;

public interface CurrentWeatherUpdateWorkerHelper {
    void updateCurrentWeatherConditionsByCityId();

    void updateCurrentWeatherConditionsByCoordinates();
}
