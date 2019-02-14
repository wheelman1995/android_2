package ru.wheelman.weather.domain.interactors;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

public interface CurrentWeatherInteractor {
    LiveData<CurrentWeatherConditions> getCurrentWeatherConditionsByCityId();

    void updateCurrentWeatherConditionsByCityId();

    void updateCurrentWeatherConditionsByCoordinates();
}
