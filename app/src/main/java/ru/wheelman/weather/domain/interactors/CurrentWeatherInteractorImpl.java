package ru.wheelman.weather.domain.interactors;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.data.repositories.CurrentWeatherRepository;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

@ApplicationScope
public class CurrentWeatherInteractorImpl implements CurrentWeatherInteractor {

    private CurrentWeatherRepository currentWeatherRepository;

    @Inject
    public CurrentWeatherInteractorImpl(CurrentWeatherRepository currentWeatherRepository) {
        this.currentWeatherRepository = currentWeatherRepository;
    }

    @Override
    public LiveData<CurrentWeatherConditions> getCurrentWeatherConditionsByCityId() {
        return currentWeatherRepository.getCurrentWeatherConditionsByCityId();
    }

    @Override
    public void updateCurrentWeatherConditionsByCityId() {
        currentWeatherRepository.updateCurrentWeatherConditionsByCityId();
    }

    @Override
    public void updateCurrentWeatherConditionsByCoordinates() {
        currentWeatherRepository.updateCurrentWeatherConditionsByCoordinates();
    }
}
