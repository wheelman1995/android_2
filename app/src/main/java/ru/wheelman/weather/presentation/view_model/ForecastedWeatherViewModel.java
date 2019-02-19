package ru.wheelman.weather.presentation.view_model;

public interface ForecastedWeatherViewModel {

    ForecastedWeatherViewModelImpl.ScreenState getScreenState();

    void onRefreshSwipeRefreshLayout();

    void onStart();

    void onStop();

    void onViewCreated();
}
