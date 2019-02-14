package ru.wheelman.weather.data.data_sources.databases.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;

@Dao
public interface CurrentWeatherDAO {

    @Query("SELECT json FROM CurrentWeather WHERE _id = :id")
    LiveData<CurrentWeatherConditions> getDataByCityId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Query("insert or replace into CurrentWeather (_id, json) values (:id, :currentWeatherConditions)")
    void insert(int id, CurrentWeatherConditions currentWeatherConditions);

//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    void update(WeatherData weatherData);


}


