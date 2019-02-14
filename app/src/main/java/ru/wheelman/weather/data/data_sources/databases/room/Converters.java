package ru.wheelman.weather.data.data_sources.databases.room;

import com.google.gson.Gson;

import androidx.room.TypeConverter;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;
import ru.wheelman.weather.domain.entities.FiveDayForecast;

public class Converters {

    private static Gson gson = new Gson();

    @TypeConverter
    public static CurrentWeatherConditions jsonToCurrentWeatherConditions(String json) {
        return gson.fromJson(json, CurrentWeatherConditions.class);
    }

    @TypeConverter
    public static String currentWeatherConditionsToJson(CurrentWeatherConditions currentWeatherConditions) {
        return gson.toJson(currentWeatherConditions);
    }

    @TypeConverter
    public static FiveDayForecast jsonToFiveDayForecast(String json) {
        return gson.fromJson(json, FiveDayForecast.class);
    }

    @TypeConverter
    public static String fiveDayForecastToJson(FiveDayForecast fiveDayForecast) {
        return gson.toJson(fiveDayForecast);
    }
}
