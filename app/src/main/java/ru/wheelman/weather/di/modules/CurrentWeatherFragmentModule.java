package ru.wheelman.weather.di.modules;

import androidx.lifecycle.ViewModelProviders;
import ru.wheelman.weather.presentation.view.fragments.CurrentWeatherFragment;
import ru.wheelman.weather.presentation.view_model.CurrentWeatherViewModel;
import ru.wheelman.weather.presentation.view_model.CurrentWeatherViewModelImpl;
import toothpick.config.Module;

public class CurrentWeatherFragmentModule extends Module {
    public CurrentWeatherFragmentModule(CurrentWeatherFragment currentWeatherFragment) {
        bind(CurrentWeatherViewModel.class).toInstance(ViewModelProviders.of(currentWeatherFragment).get(CurrentWeatherViewModelImpl.class));
    }
}
