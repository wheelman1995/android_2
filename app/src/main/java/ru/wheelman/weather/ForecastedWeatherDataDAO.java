package ru.wheelman.weather;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ForecastedWeatherDataDAO {
    @Query("SELECT * FROM ForecastedWeatherData WHERE _id = :id")
    LiveData<ForecastedWeatherData> getForecastedDataByCityId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ForecastedWeatherData forecastedWeatherData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ForecastedWeatherData forecastedWeatherData);


}
