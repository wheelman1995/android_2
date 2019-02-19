package ru.wheelman.weather.data.data_sources.network;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import ru.wheelman.weather.di.scopes.ApplicationScope;

@ApplicationScope
public class OpenWeatherAPI {
    public static final String WEATHER_ICON_URL_PREFIX = "https://openweathermap.org/img/w/";
    public static final String WEATHER_ICON_URL_SUFFIX = ".png";
    public static final String MODE_XML = "xml";
    public static final String XML_UNIT_CELSIUS = "celsius";
    public static final String XML_UNIT_FAHRENHEIT = "imperial";
    static final String CELSIUS = "metric";
    static final String FAHRENHEIT = "imperial";
    static final String XML_RESPONSE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    static final int FORECAST_LENGTH_IN_DAYS = 5;
    private static final String BASE_URL = "https://api.openweathermap.org";
    private final OkHttpClient httpClient;
    private IOpenWeatherAPI openWeatherAPI;

    @Inject
    public OpenWeatherAPI() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new XmlOrJsonConverterFactory())
                .client(httpClient)
                .build();

        openWeatherAPI = retrofit.create(IOpenWeatherAPI.class);

    }

    public IOpenWeatherAPI getOpenWeatherAPI() {
        return openWeatherAPI;
    }
}
