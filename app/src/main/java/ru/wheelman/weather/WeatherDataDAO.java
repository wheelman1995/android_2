package ru.wheelman.weather;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WeatherDataDAO {
    @Query("SELECT * FROM WeatherData WHERE _id = :id")
    LiveData<WeatherData> getDataByCityId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherData weatherData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(WeatherData weatherData);


}
