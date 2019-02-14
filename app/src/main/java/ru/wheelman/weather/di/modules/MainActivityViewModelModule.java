package ru.wheelman.weather.di.modules;

import ru.wheelman.weather.presentation.view_model.MainActivityViewModelImpl;
import toothpick.config.Module;

public class MainActivityViewModelModule extends Module {
    public MainActivityViewModelModule() {
//        bind(WeatherUpdateTrigger.class).to(WeatherUpdateTriggerImpl.class);
        bind(MainActivityViewModelImpl.ScreenState.class);
    }
}
