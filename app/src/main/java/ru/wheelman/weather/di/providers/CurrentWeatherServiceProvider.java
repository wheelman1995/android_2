package ru.wheelman.weather.di.providers;

import javax.inject.Inject;
import javax.inject.Provider;

import ru.wheelman.weather.data.data_sources.network.OpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.current.CurrentWeatherService;
import ru.wheelman.weather.di.scopes.ApplicationScope;

@ApplicationScope
public class CurrentWeatherServiceProvider implements Provider<CurrentWeatherService> {

    private OpenWeatherAPI openWeatherAPI;

    @Inject
    public CurrentWeatherServiceProvider(OpenWeatherAPI openWeatherAPI) {
        this.openWeatherAPI = openWeatherAPI;
    }

    @Override
    public CurrentWeatherService get() {
        return openWeatherAPI.getCurrentWeatherService();
    }
}
