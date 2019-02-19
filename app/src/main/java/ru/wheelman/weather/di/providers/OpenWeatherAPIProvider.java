package ru.wheelman.weather.di.providers;

import javax.inject.Inject;
import javax.inject.Provider;

import ru.wheelman.weather.data.data_sources.network.IOpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.OpenWeatherAPI;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import toothpick.ProvidesSingletonInScope;

@ApplicationScope
@ProvidesSingletonInScope
public class OpenWeatherAPIProvider implements Provider<IOpenWeatherAPI> {

    private OpenWeatherAPI openWeatherAPI;

    @Inject
    public OpenWeatherAPIProvider(OpenWeatherAPI openWeatherAPI) {
        this.openWeatherAPI = openWeatherAPI;
    }

    @Override
    public IOpenWeatherAPI get() {
        return openWeatherAPI.getOpenWeatherAPI();
    }
}
