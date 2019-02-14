package ru.wheelman.weather.data.data_sources.databases.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

@Dao
public interface ForecastedWeatherDAO {
    @Query("SELECT json FROM ForecastedWeather WHERE _id = :id")
    LiveData<FiveDayForecast> getForecastedDataByCityId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Query("insert or replace into ForecastedWeather (_id, json) values (:id, :fiveDayForecast)")
    void insert(int id, FiveDayForecast fiveDayForecast);

//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    void update(ForecastedWeatherData forecastedWeatherData);
}
