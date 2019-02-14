package ru.wheelman.weather.data.data_sources.network.current;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.wheelman.weather.data.data_sources.network.current.model.CurrentWeather;

public interface CurrentWeatherService {
    @GET("data/2.5/weather")
    Call<CurrentWeather> loadWeatherData(@Query("id") int cityId,
                                         @Query("units") String units,
                                         @Query("appid") String apiKey);

    @GET("data/2.5/weather")
    Call<CurrentWeather> loadWeatherDataByCoordinates(@Query("lat") double latitude,
                                                      @Query("lon") double longitude,
                                                      @Query("units") String units,
                                                      @Query("appid") String apiKey);
}
