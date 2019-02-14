package ru.wheelman.weather.data.data_sources.network.current;

import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

public interface CurrentWeatherRemoteDataSource {
    CurrentWeatherConditions requestCurrentWeatherConditionsByCityId();

    CurrentWeatherConditions requestCurrentWeatherConditionsByCoordinates();
}
