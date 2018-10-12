package ru.wheelman.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.wheelman.weather.model.WeatherDataModel;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherDataModel> loadWeatherData(@Query("id") int cityId, @Query("units") String units, @Query("appid") String apiKey);
}
