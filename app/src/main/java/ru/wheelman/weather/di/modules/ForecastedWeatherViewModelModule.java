package ru.wheelman.weather.di.modules;

import ru.wheelman.weather.presentation.data_mappers.DataMapper;
import ru.wheelman.weather.presentation.data_mappers.FiveDayForecastDataMapper;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModelImpl.AdapterViewModel;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModelImpl.AdapterViewModelImpl;
import toothpick.config.Module;

public class ForecastedWeatherViewModelModule extends Module {
    public ForecastedWeatherViewModelModule() {
        bind(DataMapper.class).to(FiveDayForecastDataMapper.class);
        bind(AdapterViewModel.class).to(AdapterViewModelImpl.class);
    }
}
