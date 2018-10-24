package ru.wheelman.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wheelman.weather.model.WeatherDataModel;
import ru.wheelman.weather.model.forecast.ForecastDataModel;
import ru.wheelman.weather.model.forecast.ThreeHourData;

public class WeatherUpdateWorker extends Worker {

    private static final String TAG = "WeatherUpdateWorker";
    public static final String ACTION_NEW_DATA_RECEIVED = "ru.wheelman.weather.action.ACTION_NEW_DATA_RECEIVED";

    public static final int WORK_TYPE_LOAD_CURRENT_WEATHER = 0;
    public static final int WORK_TYPE_LOAD_CURRENT_WEATHER_BY_COORDINATES = 1;
    public static final int WORK_TYPE_LOAD_FORECAST = 2;
    public static final int WORK_TYPE_LOAD_FORECAST_BY_COORDINATES = 3;
    private String unitSymbol;
    private String queryUnit;
    private OpenWeather openWeather;
    private int cityId;
    private Context context;
    private long sunrise;
    private long sunset;
    private double latitude;
    private double longitude;
    private Gson gson;


    public WeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("MissingPermission")
    @Override
    @NonNull
    public Result doWork() {

        int workType = getInputData().getInt(Constants.WORK_MANAGER_DATA_WORK_TYPE, -1);

        initVariables();

        initRetrofit();

        Result result;

        switch (workType) {
            case WORK_TYPE_LOAD_CURRENT_WEATHER:
                result = loadCurrentWeather();
                break;
            case WORK_TYPE_LOAD_CURRENT_WEATHER_BY_COORDINATES:
                result = loadCurrentWeatherByCoordinates();
                break;
            default:
                throw new RuntimeException("workType for worker has not been specified!");
        }

        return result;
    }

    private void initVariables() {
        gson = new Gson();
        context = getApplicationContext();
        cityId = getInputData().getInt(Constants.WORK_MANAGER_DATA_CITY_ID, SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID);
        Units unit = Units.getUnitByIndex(getInputData().getInt(Constants.WORK_MANAGER_DATA_UNITS, Units.CELSIUS.getUnitIndex()));
        switch (unit) {
            case CELSIUS:
                queryUnit = Constants.QUERY_CELSIUS;
                unitSymbol = context.getString(R.string.celsius);
                break;
            case FAHRENHEIT:
                queryUnit = Constants.QUERY_FAHRENHEIT;
                unitSymbol = context.getString(R.string.fahrenheit);
                break;
            default:
                queryUnit = Constants.QUERY_CELSIUS;
                unitSymbol = context.getString(R.string.celsius);
        }
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }

    private Result loadForecastByCoordinates() {
        Call<ForecastDataModel> call = openWeather.loadForecastedWeatherDataByCoordinates(latitude, longitude, queryUnit, Constants.API_KEY);

        Response<ForecastDataModel> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.body() == null || response.body().getThreeHourData()[0].getDt() == 0L) {
            return Result.RETRY;
        }
        ForecastDataModel data = response.body();

        FiveDayForecast fiveDayForecast = createFiveDayForecast(data);//to json

        ForecastedWeatherData forecastedWeatherData = new ForecastedWeatherData();

        String jsonData = gson.toJson(fiveDayForecast);

        forecastedWeatherData.setId(cityId);
        forecastedWeatherData.setJsonData(jsonData);

        Database db = Database.getDatabase(getApplicationContext());
        db.forecastedWeatherDataDAO().insert(forecastedWeatherData);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_NEW_DATA_RECEIVED));
        return Result.SUCCESS;
    }

    private Result loadForecast() {
        Call<ForecastDataModel> call = openWeather.loadForecastedWeatherData(cityId, queryUnit, Constants.API_KEY);

        Response<ForecastDataModel> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.body() == null || response.body().getThreeHourData()[0].getDt() == 0L) {
            return Result.RETRY;
        }
        ForecastDataModel data = response.body();

        FiveDayForecast fiveDayForecast = createFiveDayForecast(data);//to json

        ForecastedWeatherData forecastedWeatherData = new ForecastedWeatherData();

        String jsonData = gson.toJson(fiveDayForecast);

        forecastedWeatherData.setId(data.getCity().getId());
        forecastedWeatherData.setJsonData(jsonData);

        Database db = Database.getDatabase(getApplicationContext());
        db.forecastedWeatherDataDAO().insert(forecastedWeatherData);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_NEW_DATA_RECEIVED));
        return Result.SUCCESS;
    }

    private FiveDayForecast createFiveDayForecast(ForecastDataModel data) {
        class OneDay {
            private ArrayList<Long> dates = new ArrayList<>();
            private ArrayList<String> weatherConditionDescriptions = new ArrayList<>();
            private ArrayList<Float> temperatures = new ArrayList<>();
            private ArrayList<String> icons = new ArrayList<>();


            private String getTheWorstDescription() {
                for (int i = 0; i < DescriptionsIcons.values().length; i++) {
                    if (weatherConditionDescriptions.contains(DescriptionsIcons.values()[i].description))
                        return DescriptionsIcons.values()[i].description;
                }
                return null;
            }

            private String getTheWorstIcon() {
                for (int i = 0; i < DescriptionsIcons.values().length; i++) {
                    for (int j = 0; j < DescriptionsIcons.values()[i].icons.length; j++) {
                        if (icons.contains(DescriptionsIcons.values()[i].icons[j]))
                            return DescriptionsIcons.values()[i].icons[j];
                    }
                }
                return null;
            }

            private float getMinTemperature() {
                float min = temperatures.get(0);
                for (int i = 0; i < temperatures.size(); i++) {
                    if (temperatures.get(i) < min)
                        min = temperatures.get(i);
                }
                return min;
            }

            private float getMaxTemperature() {
                float max = temperatures.get(0);
                for (int i = 0; i < temperatures.size(); i++) {
                    if (temperatures.get(i) > max)
                        max = temperatures.get(i);
                }
                return max;
            }
        }


        ThreeHourData[] threeHourData = data.getThreeHourData();

        long srise = sunrise + 86400L; //current day ends with the next sunrise (24 hours in seconds = 86400)

        ArrayList<OneDay> days = new ArrayList<>();
        int indexOfDay = -1;
        //if the first data is for tomorrow (tomorrow begins, after the sunrise), do not add the first day here, it will be added below
        if (threeHourData[0].getDt() <= srise) {
            days.add(new OneDay());
            indexOfDay++;
        }

        for (int i = 0; i < threeHourData.length; i++) {
            //split data into days
            //one day begins on sunrise and ends on the next sunrise, thus we would not divide a night into pieces
            if (threeHourData[i].getDt() <= srise) {
                days.get(indexOfDay).dates.add(threeHourData[i].getDt());
                days.get(indexOfDay).weatherConditionDescriptions.add(threeHourData[i].getWeather()[0].getMain());
                days.get(indexOfDay).temperatures.add(threeHourData[i].getMain().getTemp());
                days.get(indexOfDay).icons.add(threeHourData[i].getWeather()[0].getIcon());
            } else {
                days.add(new OneDay());
                indexOfDay++;
                srise += 86400L;
            }
        }
        //drop the incomplete day

//        Log.d(TAG, String.valueOf(days.size()));
        int numberOfDays = days.size() == 6 ? 5 : days.size();

        long[] dates = new long[numberOfDays];
        String[] weatherConditionDescriptions = new String[numberOfDays];
        float[] maxDayTemperatures = new float[numberOfDays];
        float[] minNightTemperatures = new float[numberOfDays];
        String[] icons = new String[numberOfDays];

        for (int i = 0; i < numberOfDays; i++) {
            dates[i] = days.get(i).dates.get(0);

            minNightTemperatures[i] = days.get(i).getMinTemperature();

            maxDayTemperatures[i] = days.get(i).getMaxTemperature();

            weatherConditionDescriptions[i] = days.get(i).getTheWorstDescription();

            icons[i] = days.get(i).getTheWorstIcon();
        }

        return new FiveDayForecast(data.getCity().getId(), dates, weatherConditionDescriptions, maxDayTemperatures, minNightTemperatures, icons);
    }

    private Result loadCurrentWeatherByCoordinates() {
        latitude = getInputData().getDouble(Constants.WORK_MANAGER_DATA_LATITUDE, -1);
        longitude = getInputData().getDouble(Constants.WORK_MANAGER_DATA_LONGITUDE, -1);

        Call<WeatherDataModel> call = openWeather.loadWeatherDataByCoordinates(latitude, longitude, queryUnit, Constants.API_KEY);

        Response<WeatherDataModel> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.body() == null || response.body().getDt() == 0L) {
            return Result.RETRY;
        }
        WeatherDataModel data = response.body();

        WeatherData weatherData = new WeatherData();

        weatherData.setId(cityId);
        weatherData.setDt(data.getDt());
        weatherData.setTemperature(String.format(Locale.UK, unitSymbol, data.getMain().getTemp()));
        weatherData.setCity(data.getName());
        weatherData.setCountry(data.getSys().getCountry());
        sunrise = data.getSys().getSunrise();
        sunset = data.getSys().getSunset();
        weatherData.setSunrise(sunrise);
        weatherData.setSunset(sunset);
        weatherData.setWeatherDescription(data.getWeather()[0].getDescription());
        weatherData.setWeatherIcon(data.getWeather()[0].getIcon());
        weatherData.setWeatherId(data.getWeather()[0].getId());
        weatherData.setWeatherMain(data.getWeather()[0].getMain());
        Database db = Database.getDatabase(getApplicationContext());
        db.weatherDataDAO().insert(weatherData);

        return loadForecastByCoordinates();
    }

    private Result loadCurrentWeather() {
        Call<WeatherDataModel> call = openWeather.loadWeatherData(cityId, queryUnit, Constants.API_KEY);

        Response<WeatherDataModel> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.body() == null || response.body().getDt() == 0L) {
            return Result.RETRY;
        }
        WeatherDataModel data = response.body();

        WeatherData weatherData = new WeatherData();

        weatherData.setId(data.getId());
        weatherData.setDt(data.getDt());
        weatherData.setTemperature(String.format(Locale.UK, unitSymbol, data.getMain().getTemp()));
        weatherData.setCity(data.getName());
        weatherData.setCountry(data.getSys().getCountry());
        sunrise = data.getSys().getSunrise();
        sunset = data.getSys().getSunset();
        weatherData.setSunrise(sunrise);
        weatherData.setSunset(sunset);
        weatherData.setWeatherDescription(data.getWeather()[0].getDescription());
        weatherData.setWeatherIcon(data.getWeather()[0].getIcon());
        weatherData.setWeatherId(data.getWeather()[0].getId());
        weatherData.setWeatherMain(data.getWeather()[0].getMain());
        Database db = Database.getDatabase(getApplicationContext());
        db.weatherDataDAO().insert(weatherData);

        return loadForecast();
    }

    @Override
    public void onStopped(boolean cancelled) {
        Database.destroyInstance();
        super.onStopped(cancelled);
    }

    enum DescriptionsIcons {
        SNOW("Snow", new String[]{"13d", "13n"}), THUNDERSTORM("Thunderstorm", new String[]{"11d", "11n"}),
        RAIN("Rain", new String[]{"10d", "10n", "13d", "13n", "09d", "09n"}), DRIZZLE("Drizzle", new String[]{"09d", "09n"}),
        MIST("Atmosphere", new String[]{"50d", "50n"}), CLOUDS("Clouds", new String[]{"02d", "02n", "03d", "03n", "04d", "04n"}),
        CLEAR("Clear", new String[]{"01d", "01n"});

        String description;
        String[] icons;

        DescriptionsIcons(String description, String[] icons) {
            this.description = description;
            this.icons = icons;
        }
    }
}
