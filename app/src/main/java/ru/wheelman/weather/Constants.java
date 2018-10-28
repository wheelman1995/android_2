package ru.wheelman.weather;

public final class Constants {
//    private static final Constants ourInstance = new Constants();
//
//    public static Constants getInstance() {
//        return ourInstance;
//    }

    public static final String DEVELOPER_EMAIL = "anikinvitya@gmail.com";

    public static final String MAIN_SHARED_PREFERENCES_NAME = "main";
    public static final String SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY = "temp_units";
    public static final String SHARED_PREFERENCES_LATITUDE_KEY = "SHARED_PREFERENCES_LATITUDE_KEY";
    public static final String SHARED_PREFERENCES_LONGITUDE_KEY = "SHARED_PREFERENCES_LONGITUDE_KEY";
    public static final String SHARED_PREFERENCES_CURRENT_CITY_ID = "current city id";

    public static final String BASE_URL = "https://api.openweathermap.org";

    public static final String QUERY_CELSIUS = "metric";
    public static final String QUERY_FAHRENHEIT = "imperial";

    public static final String WORK_MANAGER_WEATHER_PERIODIC_UPDATE_TAG = "weather periodic update";
    public static final String WORK_MANAGER_WEATHER_ONE_TIME_UPDATE_TAG = "weather one-time update";

    public static final String WORK_MANAGER_DATA_CITY_ID = "cityId";
    public static final String WORK_MANAGER_DATA_UNITS = "units";
    public static final String WORK_MANAGER_DATA_LATITUDE = "latitude";
    public static final String WORK_MANAGER_DATA_LONGITUDE = "longitude";
    public static final String WORK_MANAGER_DATA_WORK_TYPE = "WORK_MANAGER_DATA_WORK_TYPE";

    public static final String WEATHER_ICON_URL_BASE = "https://openweathermap.org/img/w/";

    private Constants() {
    }
}
