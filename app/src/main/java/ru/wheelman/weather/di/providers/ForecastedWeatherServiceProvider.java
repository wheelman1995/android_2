package ru.wheelman.weather.di.providers;

import javax.inject.Inject;
import javax.inject.Provider;

import ru.wheelman.weather.data.data_sources.network.OpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.forecasted.ForecastedWeatherService;
import ru.wheelman.weather.di.scopes.ApplicationScope;

@ApplicationScope
public class ForecastedWeatherServiceProvider implements Provider<ForecastedWeatherService> {

    private OpenWeatherAPI openWeatherAPI;

    @Inject
    public ForecastedWeatherServiceProvider(OpenWeatherAPI openWeatherAPI) {
        this.openWeatherAPI = openWeatherAPI;
    }

    @Override
    public ForecastedWeatherService get() {
        return openWeatherAPI.getForecastedWeatherService();
    }
}
