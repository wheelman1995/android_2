package ru.wheelman.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.wheelman.weather.model.WeatherDataModel;
import ru.wheelman.weather.model.forecast.ForecastDataModel;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherDataModel> loadWeatherData(@Query("id") int cityId, @Query("units") String units, @Query("appid") String apiKey);

    @GET("data/2.5/weather")
    Call<WeatherDataModel> loadWeatherDataByCoordinates(@Query("lat") double latitude, @Query("lon") double longitude, @Query("units") String units, @Query("appid") String apiKey);

    @GET("data/2.5/forecast")
    Call<ForecastDataModel> loadForecastedWeatherData(@Query("id") int cityId, @Query("units") String units, @Query("appid") String apiKey);

    @GET("data/2.5/forecast")
    Call<ForecastDataModel> loadForecastedWeatherDataByCoordinates(@Query("lat") double latitude, @Query("lon") double longitude, @Query("units") String units, @Query("appid") String apiKey);
}
