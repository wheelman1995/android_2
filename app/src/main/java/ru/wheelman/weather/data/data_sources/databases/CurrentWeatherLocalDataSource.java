package ru.wheelman.weather.data.data_sources.databases;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

public interface CurrentWeatherLocalDataSource {
    LiveData<CurrentWeatherConditions> getCurrentWeatherConditionsByCityId();

    void saveCurrentWeatherConditions(CurrentWeatherConditions currentWeatherConditions);
}
