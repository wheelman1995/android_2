package ru.viktor.homework;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class WeatherViewModel extends ViewModel {

    private MutableLiveData<String> weatherData;

    public MutableLiveData<String> getWeatherData() {
        if (weatherData == null) {
            weatherData = new MutableLiveData<>();
        }
        return weatherData;
    }
}
