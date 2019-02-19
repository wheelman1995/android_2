package ru.wheelman.weather.data.data_sources.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.wheelman.weather.data.data_sources.network.current.model.CurrentWeather;
import ru.wheelman.weather.data.data_sources.network.forecasted.model.ForecastedWeatherXML;

public interface IOpenWeatherAPI {

    @Xml
    @GET("data/2.5/forecast")
    Call<ForecastedWeatherXML> loadForecastedWeatherData(@Query("id") int cityId,
                                                         @Query("units") String units,
                                                         @Query("appid") String apiKey,
                                                         @Query("mode") String mode);

    @Xml
    @GET("data/2.5/forecast")
    Call<ForecastedWeatherXML> loadForecastedWeatherDataByCoordinates(@Query("lat") double latitude,
                                                                      @Query("lon") double longitude,
                                                                      @Query("units") String units,
                                                                      @Query("appid") String apiKey,
                                                                      @Query("mode") String mode);

    @Json
    @GET("data/2.5/weather")
    Call<CurrentWeather> loadWeatherData(@Query("id") int cityId,
                                         @Query("units") String units,
                                         @Query("appid") String apiKey);

    @Json
    @GET("data/2.5/weather")
    Call<CurrentWeather> loadWeatherDataByCoordinates(@Query("lat") double latitude,
                                                      @Query("lon") double longitude,
                                                      @Query("units") String units,
                                                      @Query("appid") String apiKey);
}
