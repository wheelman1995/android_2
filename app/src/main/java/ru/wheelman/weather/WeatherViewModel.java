package ru.wheelman.weather;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class WeatherViewModel extends AndroidViewModel {

    private LiveData<WeatherData> weatherData;

    private Database db;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        createDb();
        weatherData = db.weatherDataDAO().getData();
    }

    public LiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public void createDb() {
        db = Database.getDatabase(getApplication());
    }
}
