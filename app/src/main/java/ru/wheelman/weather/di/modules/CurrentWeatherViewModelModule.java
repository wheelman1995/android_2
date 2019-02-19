package ru.wheelman.weather.di.modules;

import ru.wheelman.weather.presentation.data_mappers.CurrentWeatherDataMapper;
import ru.wheelman.weather.presentation.data_mappers.DataMapper;
import toothpick.config.Module;

public class CurrentWeatherViewModelModule extends Module {
    public CurrentWeatherViewModelModule() {
        bind(DataMapper.class).to(CurrentWeatherDataMapper.class);
    }
}
