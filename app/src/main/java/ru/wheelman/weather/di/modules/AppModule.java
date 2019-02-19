package ru.wheelman.weather.di.modules;

import android.content.Context;

import ru.wheelman.weather.R;
import ru.wheelman.weather.WeatherApp;
import ru.wheelman.weather.di.qualifiers.ApiKeyQualifier;
import ru.wheelman.weather.presentation.data_binding.BindingAdapters;
import ru.wheelman.weather.presentation.data_binding.IBindingAdapters;
import ru.wheelman.weather.presentation.utils.ConnectivityHelper;
import ru.wheelman.weather.presentation.utils.IConnectivityHelper;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.PreferenceHelperImpl;
import toothpick.config.Module;

public class AppModule extends Module {
    public AppModule(WeatherApp weatherApp) {
        bind(WeatherApp.class).toInstance(weatherApp);
        bind(Context.class).toInstance(weatherApp);
        bind(String.class).withName(ApiKeyQualifier.class).toInstance(weatherApp.getResources().getString(R.string.open_weather_map_api_key));
        bind(PreferenceHelper.class).to(PreferenceHelperImpl.class);
        bind(IBindingAdapters.class).to(BindingAdapters.class);
        bind(IConnectivityHelper.class).to(ConnectivityHelper.class);
    }

}
