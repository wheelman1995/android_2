package ru.wheelman.weather.data.repositories;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import ru.wheelman.weather.data.data_sources.databases.CurrentWeatherLocalDataSource;
import ru.wheelman.weather.data.data_sources.network.current.CurrentWeatherRemoteDataSource;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

@ApplicationScope
public class CurrentWeatherRepositoryImpl implements CurrentWeatherRepository {

    private final CurrentWeatherRemoteDataSource currentWeatherRemoteDataSource;
    private final CurrentWeatherLocalDataSource currentWeatherLocalDataSource;

    @Inject
    public CurrentWeatherRepositoryImpl(CurrentWeatherRemoteDataSource currentWeatherRemoteDataSource, CurrentWeatherLocalDataSource currentWeatherLocalDataSource) {
        this.currentWeatherRemoteDataSource = currentWeatherRemoteDataSource;
        this.currentWeatherLocalDataSource = currentWeatherLocalDataSource;
    }

    @Override
    public LiveData<CurrentWeatherConditions> getCurrentWeatherConditionsByCityId() {
        return currentWeatherLocalDataSource.getCurrentWeatherConditionsByCityId();
    }

    @Override
    public void updateCurrentWeatherConditionsByCityId() {
        CurrentWeatherConditions currentWeatherConditions = currentWeatherRemoteDataSource.requestCurrentWeatherConditionsByCityId();
        saveToLocalDataSource(currentWeatherConditions);
    }

    @Override
    public void updateCurrentWeatherConditionsByCoordinates() {
        CurrentWeatherConditions currentWeatherConditions = currentWeatherRemoteDataSource.requestCurrentWeatherConditionsByCoordinates();
        saveToLocalDataSource(currentWeatherConditions);
    }

    private void saveToLocalDataSource(CurrentWeatherConditions currentWeatherConditions) {
        if (currentWeatherConditions != null) {
            currentWeatherLocalDataSource.saveCurrentWeatherConditions(currentWeatherConditions);
        }
    }
}
