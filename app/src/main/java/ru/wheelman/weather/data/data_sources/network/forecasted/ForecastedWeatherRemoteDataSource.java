package ru.wheelman.weather.data.data_sources.network.forecasted;

import ru.wheelman.weather.domain.entities.FiveDayForecast;

public interface ForecastedWeatherRemoteDataSource {
    FiveDayForecast requestFiveDayForecastByCityId();

    FiveDayForecast requestFiveDayForecastByCoordinates();
}
