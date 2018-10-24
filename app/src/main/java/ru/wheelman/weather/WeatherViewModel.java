package ru.wheelman.weather;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class WeatherViewModel extends AndroidViewModel {

    private Database db;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        createDb();

    }

    LiveData<WeatherData> getWeatherData(int cityId) {
        return db.weatherDataDAO().getDataByCityId(cityId);
    }

    LiveData<ForecastedWeatherData> getForecastWeatherData(int cityId) {
        return db.forecastedWeatherDataDAO().getForecastedDataByCityId(cityId);
    }

    private void createDb() {
        db = Database.getDatabase(getApplication());
    }
}
