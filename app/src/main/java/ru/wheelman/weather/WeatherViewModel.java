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

    }

    public LiveData<WeatherData> getWeatherData(int cityId) {
        weatherData = db.weatherDataDAO().getDataByCityId(cityId);
        return weatherData;
    }

    public void createDb() {
        db = Database.getDatabase(getApplication());
    }
}
