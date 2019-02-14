package ru.wheelman.weather.data.repositories;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

public interface CurrentWeatherRepository {
    LiveData<CurrentWeatherConditions> getCurrentWeatherConditionsByCityId();

    void updateCurrentWeatherConditionsByCityId();

    void updateCurrentWeatherConditionsByCoordinates();
}
