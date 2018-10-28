package ru.wheelman.weather;

import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class ForecastedWeatherData {

    @PrimaryKey
    @ColumnInfo(name = BaseColumns._ID)
    private int id;

    //    dates;
//weatherConditionDescriptions;
//maxDayTemperatures;
//minNightTemperatures;
// icons;
    @ColumnInfo(name = "json_data")
    private String jsonData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    //    @ColumnInfo(name = "sunset")
//    private long sunset;
//    @ColumnInfo(name = "sunrise")
//    private long sunrise;
//
//    @ColumnInfo(name = "temperature")
//    private String temperature;
//
//
//    @ColumnInfo(name = "city")
//    private String city;
//
//    @ColumnInfo(name = "country")
//    private String country;
//
//
//    @ColumnInfo(name = "weather_id")
//    private String weatherId;
//
//    @ColumnInfo(name = "weather_main")
//    private String weatherMain;
//
//    @ColumnInfo(name = "weather_description")
//    private String weatherDescription;
//
//    @ColumnInfo(name = "weather_icon")
//    private String weatherIcon;
}
