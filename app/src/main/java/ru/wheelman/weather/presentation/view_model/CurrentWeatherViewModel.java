package ru.wheelman.weather.presentation.view_model;

public interface CurrentWeatherViewModel {
    CurrentWeatherViewModelImpl.ScreenState getScreenState();

    void onRefreshSwipeRefreshLayout();

    void onStart();

    void onStop();
}
