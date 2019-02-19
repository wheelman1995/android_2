package ru.wheelman.weather.di.modules;

import androidx.lifecycle.ViewModelProviders;
import ru.wheelman.weather.presentation.view.fragments.ForecastedWeatherFragment;
import ru.wheelman.weather.presentation.view.fragments.ForecastedWeatherFragmentAdapter;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModel;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModelImpl;
import toothpick.config.Module;

public class ForecastedWeatherFragmentModule extends Module {
    public ForecastedWeatherFragmentModule(ForecastedWeatherFragment forecastedWeatherFragment) {
        bind(ForecastedWeatherViewModel.class).toInstance(ViewModelProviders.of(forecastedWeatherFragment).get(ForecastedWeatherViewModelImpl.class));
        bind(ForecastedWeatherFragmentAdapter.class);
    }
}
