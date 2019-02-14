//package ru.wheelman.weather.presentation.utils;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.google.gson.Gson;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Locale;
//
//import androidx.annotation.NonNull;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//import retrofit2.Call;
//import retrofit2.Response;
//import ru.wheelman.weather.Constants;
//import ru.wheelman.weather.R;
//import ru.wheelman.weather.data.data_sources.network.current.CurrentWeatherService;
//import ru.wheelman.weather.data.data_sources.network.current.model.CurrentWeather;
//import ru.wheelman.weather.data.data_sources.network.forecasted.model.ForecastedWeather;
//import ru.wheelman.weather.data.data_sources.network.forecasted.model.OneDataPiece;
//import ru.wheelman.weather.data.repositories.SearchSuggestionsProvider;
//import ru.wheelman.weather.domain.entities.FiveDayForecast;
//import ru.wheelman.weather.domain.entities.Units;
//
//public class WeatherUpdateWorkerOld extends Worker {
//
//    public static final String ACTION_NEW_DATA_RECEIVED = "ru.wheelman.weather.action.ACTION_NEW_DATA_RECEIVED";
//    public static final int WORK_TYPE_LOAD_WEATHER_BY_ID = 0;
//    public static final int WORK_TYPE_LOAD_WEATHER_BY_COORDINATES = 1;
//    private static final String TAG = "WeatherUpdateWorkerOld";
//    //    public static final int WORK_TYPE_LOAD_FORECAST = 2;
////    public static final int WORK_TYPE_LOAD_FORECAST_BY_COORDINATES = 3;
//    private String unitSymbol;
//    private String queryUnit;
//    private CurrentWeatherService currentWeatherService;
//    private int cityId;
//    private Context context;
//    private long sunrise;
//    private long sunset;
//    private double latitude;
//    private double longitude;
//    private Gson gson;
//    private int workType;
//
//
//    public WeatherUpdateWorkerOld(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//
//    @Override
//    @NonNull
//    public Result doWork() {
//
////        int workType = getInputData().getInt(Constants.WORK_MANAGER_DATA_WORK_TYPE, -1);
//
//        initVariables();
//
//        initRetrofit();
//
//        Result result;
//
//        switch (workType) {
//            case WORK_TYPE_LOAD_WEATHER_BY_ID:
//                result = loadCurrentWeather();
//                break;
//            case WORK_TYPE_LOAD_WEATHER_BY_COORDINATES:
//                result = loadCurrentWeatherByCoordinates();
//                break;
//            default:
//                throw new RuntimeException("workType for worker has not been specified!");
//        }
//
//        return result;
//    }
//
//    private void initVariables() {
//        gson = new Gson();
//        context = getApplicationContext();
//        cityId = getInputData().getInt(Constants.WORK_MANAGER_DATA_CITY_ID, SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID);
//
//        workType = cityId == SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID ? WORK_TYPE_LOAD_WEATHER_BY_COORDINATES : WORK_TYPE_LOAD_WEATHER_BY_ID;
//
//        Units unit = Units.getUnitByIndex(getInputData().getInt(Constants.WORK_MANAGER_DATA_UNITS, Units.CELSIUS.getUnitIndex()));
//        switch (unit) {
//            case CELSIUS:
//                queryUnit = Constants.QUERY_CELSIUS;
//                unitSymbol = context.getString(R.string.celsius);
//                break;
//            case FAHRENHEIT:
//                queryUnit = Constants.QUERY_FAHRENHEIT;
//                unitSymbol = context.getString(R.string.fahrenheit);
//                break;
//            default:
//                queryUnit = Constants.QUERY_CELSIUS;
//                unitSymbol = context.getString(R.string.celsius);
//        }
//    }
//
//
//    private Result loadForecastByCoordinates() {
//        Call<ForecastedWeather> call = currentWeatherService.loadForecastedWeatherDataByCoordinates(latitude, longitude, queryUnit, API_KEY.API_KEY);
//
//        Response<ForecastedWeather> response = null;
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (response == null || response.body() == null || response.body().getOneDatumPieces()[0].getDt() == 0L) {
//            return Result.RETRY;
//        }
//        ForecastedWeather data = response.body();
//
//        FiveDayForecast fiveDayForecast = createFiveDayForecast(data);//to json
//
//        ForecastedWeatherData forecastedWeatherData = new ForecastedWeatherData();
//
//        String jsonData = gson.toJson(fiveDayForecast);
//
//        forecastedWeatherData.setId(cityId);
//        forecastedWeatherData.setJsonData(jsonData);
//
//        Database db = Database.getDatabase(getApplicationContext());
//        db.forecastedWeatherDataDAO().insert(forecastedWeatherData);
//
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_NEW_DATA_RECEIVED));
//        return Result.SUCCESS;
//    }
//
//    private Result loadForecast() {
//        Call<ForecastedWeather> call = currentWeatherService.loadForecastedWeatherData(cityId, queryUnit, API_KEY.API_KEY);
//
//        Response<ForecastedWeather> response = null;
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (response == null || response.body() == null || response.body().getOneDatumPieces()[0].getDt() == 0L) {
//            return Result.RETRY;
//        }
//        ForecastedWeather data = response.body();
//
//        FiveDayForecast fiveDayForecast = createFiveDayForecast(data);//to json
//
//        ForecastedWeatherData forecastedWeatherData = new ForecastedWeatherData();
//
//        String jsonData = gson.toJson(fiveDayForecast);
//
//        forecastedWeatherData.setId(data.getCity().getId());
//        forecastedWeatherData.setJsonData(jsonData);
//
//        Database db = Database.getDatabase(getApplicationContext());
//        db.forecastedWeatherDataDAO().insert(forecastedWeatherData);
//
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_NEW_DATA_RECEIVED));
//        return Result.SUCCESS;
//    }
//
//    private FiveDayForecast createFiveDayForecast(ForecastedWeather data) {
//        class OneDay {
//            private ArrayList<Long> dates = new ArrayList<>();
//            private ArrayList<String> weatherConditionDescriptions = new ArrayList<>();
//            //            private ArrayList<Float> temperatures = new ArrayList<>();
//            private ArrayList<Float> nightTemperatures = new ArrayList<>();
//            private ArrayList<Float> dayTemperatures = new ArrayList<>();
//            private ArrayList<String> icons = new ArrayList<>();
//
//            public float getMinDayTemperature() {
//                if (dayTemperatures.isEmpty())
//                    return Constants.INVALID_TEMPERATURE;
//                return Collections.min(dayTemperatures);
//            }
//
//            public float getMaxDayTemperature() {
//                if (dayTemperatures.isEmpty())
//                    return Constants.INVALID_TEMPERATURE;
//                return Collections.max(dayTemperatures);
//            }
//
//            public float getMinNightTemperature() {
//                if (nightTemperatures.isEmpty())
//                    return Constants.INVALID_TEMPERATURE;
//                return Collections.min(nightTemperatures);
//            }
//
//            public float getMaxNightTemperature() {
//                if (nightTemperatures.isEmpty())
//                    return Constants.INVALID_TEMPERATURE;
//                return Collections.max(nightTemperatures);
//            }
//
//            private String getTheWorstDescription() {
//                for (int i = 0; i < DescriptionsIcons.values().length; i++) {
//                    if (weatherConditionDescriptions.contains(DescriptionsIcons.values()[i].description))
//                        return DescriptionsIcons.values()[i].description;
//                }
//                return null;
//            }
//
//            private String getTheWorstIcon() {
//                for (int i = 0; i < DescriptionsIcons.values().length; i++) {
//                    for (int j = 0; j < DescriptionsIcons.values()[i].icons.length; j++) {
//                        if (icons.contains(DescriptionsIcons.values()[i].icons[j]))
//                            return DescriptionsIcons.values()[i].icons[j];
//                    }
//                }
//                return null;
//            }
//
////            private float getMinTemperature() {
////                float min = temperatures.get(0);
////                for (int i = 0; i < temperatures.size(); i++) {
////                    if (temperatures.get(i) < min)
////                        min = temperatures.get(i);
////                }
////                return min;
////            }
////
////            private float getMaxTemperature() {
////                float max = temperatures.get(0);
////                for (int i = 0; i < temperatures.size(); i++) {
////                    if (temperatures.get(i) > max)
////                        max = temperatures.get(i);
////                }
////                return max;
////            }
//        }
//
//
//        OneDataPiece[] oneDatumPieces = data.getOneDatumPieces();
//
//        long nextSunrise = sunrise + 86400L; //current day ends with the next sunrise (24 hours in seconds = 86400)
//        long currentSunrise = sunrise;
//        long currentSunset = sunset;
//
//
//        ArrayList<OneDay> days = new ArrayList<>();
//        int indexOfDay = -1;
//        //if the first data is for tomorrow (tomorrow begins, after the sunrise), do not add the first day here, it will be added below
//        if (oneDatumPieces[0].getDt() < nextSunrise) {
//            days.add(new OneDay());
//            indexOfDay++;
//        }
//
//        for (int i = 0; i < oneDatumPieces.length; i++) {
//            //split data into days
//            //a day begins with a sunrise and ends with the next sunrise, thus we would not divide a night into pieces
//            if (oneDatumPieces[i].getDt() < nextSunrise) {
//
//                days.get(indexOfDay).dates.add(oneDatumPieces[i].getDt());
//                days.get(indexOfDay).weatherConditionDescriptions.add(oneDatumPieces[i].getWeather()[0].getMain());
//
//                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM HH:mm:ss", Locale.UK);
//                if (oneDatumPieces[i].getDt() >= currentSunrise && oneDatumPieces[i].getDt() < currentSunset) {
//
//                    Log.d(TAG, "day");
//                    Log.d(TAG, sdf.format(new Date(oneDatumPieces[i].getDt() * 1000)));
//                    Log.d(TAG, "current sunrise " + sdf.format(currentSunrise * 1000));
//                    Log.d(TAG, "current sunset " + sdf.format(currentSunset * 1000));
//
//                    days.get(indexOfDay).dayTemperatures.add(oneDatumPieces[i].getMain().getTemp());
//                } else {
//
//                    Log.d(TAG, "night");
//                    Log.d(TAG, sdf.format(new Date(oneDatumPieces[i].getDt() * 1000)));
//                    Log.d(TAG, "current sunrise " + sdf.format(currentSunrise * 1000));
//                    Log.d(TAG, "current sunset " + sdf.format(currentSunset * 1000));
//
//                    days.get(indexOfDay).nightTemperatures.add(oneDatumPieces[i].getMain().getTemp());
//                }
//
//                days.get(indexOfDay).icons.add(oneDatumPieces[i].getWeather()[0].getIcon());
//            } else {
//                days.add(new OneDay());
//                indexOfDay++;
//                currentSunrise += 86400L;
//                currentSunset += 86400L;
//                nextSunrise += 86400L;
//                i--;
//            }
//        }
////        Log.d(TAG, String.valueOf(days.size()));
//        //drop the incomplete day
//        int numberOfDays = days.size() == 6 ? 5 : days.size();
//
//        long[] dates = new long[numberOfDays];
//        String[] weatherConditionDescriptions = new String[numberOfDays];
//        float[][] dayTemperatures = new float[numberOfDays][2];
//        float[][] nightTemperatures = new float[numberOfDays][2];
//        String[] icons = new String[numberOfDays];
//        for (int i = 0; i < numberOfDays; i++) {
//            dates[i] = days.get(i).dates.get(0);
//
//            Log.d(TAG, "day temperatures " + days.get(i).dayTemperatures.size());
//            Log.d(TAG, "night temperatures " + days.get(i).nightTemperatures.size());
//            nightTemperatures[i][0] = days.get(i).getMinNightTemperature();
//            nightTemperatures[i][1] = days.get(i).getMaxNightTemperature();
//
//            Log.d(TAG, "0 " + days.get(i).getMinDayTemperature());
//            Log.d(TAG, "1 " + days.get(i).getMaxDayTemperature());
//            Log.d(TAG, "dayTemps " + days.get(i).dayTemperatures.toString());
//            dayTemperatures[i][0] = days.get(i).getMinDayTemperature();
//            dayTemperatures[i][1] = days.get(i).getMaxDayTemperature();
//
//            weatherConditionDescriptions[i] = days.get(i).getTheWorstDescription();
//
//            icons[i] = days.get(i).getTheWorstIcon();
//        }
//
//        return new FiveDayForecast(data.getCity().getId(), dates, weatherConditionDescriptions, dayTemperatures, nightTemperatures, icons);
//    }
//
//    private Result loadCurrentWeatherByCoordinates() {
//        latitude = getInputData().getDouble(Constants.WORK_MANAGER_DATA_LATITUDE, -1);
//        longitude = getInputData().getDouble(Constants.WORK_MANAGER_DATA_LONGITUDE, -1);
//
//        Call<CurrentWeather> call = currentWeatherService.loadWeatherDataByCoordinates(latitude, longitude, queryUnit, API_KEY.API_KEY);
//
//        Response<CurrentWeather> response = null;
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (response == null || response.body() == null || response.body().getDt() == 0L) {
//            return Result.RETRY;
//        }
//        CurrentWeather data = response.body();
//
//        WeatherData weatherData = new WeatherData();
//
//        weatherData.setId(cityId);
//        weatherData.setDt(data.getDt());
//        weatherData.setTemperature(String.format(Locale.UK, unitSymbol, data.getMain().getTemp()));
//        weatherData.setCity(data.getName());
//        weatherData.setCountry(data.getSys().getCountry());
//        sunrise = data.getSys().getSunrise();
//        sunset = data.getSys().getSunset();
//        weatherData.setSunrise(sunrise);
//        weatherData.setSunset(sunset);
//        weatherData.setWeatherDescription(data.getWeather()[0].getDescription());
//        weatherData.setWeatherIcon(data.getWeather()[0].getIcon());
//        weatherData.setWeatherId(data.getWeather()[0].getId());
//        weatherData.setWeatherMain(data.getWeather()[0].getMain());
//        Database db = Database.getDatabase(getApplicationContext());
//        db.weatherDataDAO().insert(weatherData);
//
//        return loadForecastByCoordinates();
//    }
//
//    private Result loadCurrentWeather() {
//        Call<CurrentWeather> call = currentWeatherService.loadWeatherData(cityId, queryUnit, API_KEY.API_KEY);
//
//        Response<CurrentWeather> response = null;
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (response == null || response.body() == null || response.body().getDt() == 0L) {
//            return Result.RETRY;
//        }
//        CurrentWeather data = response.body();
//
//        WeatherData weatherData = new WeatherData();
//
//        weatherData.setId(data.getId());
//        weatherData.setDt(data.getDt());
//        weatherData.setTemperature(String.format(Locale.UK, unitSymbol, data.getMain().getTemp()));
//        weatherData.setCity(data.getName());
//        weatherData.setCountry(data.getSys().getCountry());
//        sunrise = data.getSys().getSunrise();
//        sunset = data.getSys().getSunset();
//        weatherData.setSunrise(sunrise);
//        weatherData.setSunset(sunset);
//        weatherData.setWeatherDescription(data.getWeather()[0].getDescription());
//        weatherData.setWeatherIcon(data.getWeather()[0].getIcon());
//        weatherData.setWeatherId(data.getWeather()[0].getId());
//        weatherData.setWeatherMain(data.getWeather()[0].getMain());
//        Database db = Database.getDatabase(getApplicationContext());
//        db.weatherDataDAO().insert(weatherData);
//
//        return loadForecast();
//    }
//
//    @Override
//    public void onStopped(boolean cancelled) {
//        Database.destroyInstance();
//        super.onStopped(cancelled);
//    }
//
//    enum DescriptionsIcons {
//        SNOW("Snow", new String[]{"13d", "13n"}), THUNDERSTORM("Thunderstorm", new String[]{"11d", "11n"}),
//        RAIN("Rain", new String[]{"10d", "10n", "13d", "13n", "09d", "09n"}), DRIZZLE("Drizzle", new String[]{"09d", "09n"}),
//        MIST("Atmosphere", new String[]{"50d", "50n"}), CLOUDS("Clouds", new String[]{"02d", "02n", "03d", "03n", "04d", "04n"}),
//        CLEAR("Clear", new String[]{"01d", "01n"});
//
//        String description;
//        String[] icons;
//
//        DescriptionsIcons(String description, String[] icons) {
//            this.description = description;
//            this.icons = icons;
//        }
//    }
//}
