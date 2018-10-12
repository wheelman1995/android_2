package ru.wheelman.weather;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wheelman.weather.model.WeatherDataModel;

public class WeatherUpdateWorker extends Worker {

    private static final String TAG = "WeatherUpdateWorker";

    public WeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    @NonNull
    public Result doWork() {
        Context context = getApplicationContext();

//        String city = getInputData().getString("city");
//        String country = getInputData().getString("country");

        int cityId = getInputData().getInt(Constants.WORK_MANAGER_DATA_CITY_ID, -1);
        Units unit = Units.getUnitByIndex(getInputData().getInt(Constants.WORK_MANAGER_DATA_UNITS, 0));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OpenWeather openWeather = retrofit.create(OpenWeather.class);

        String queryUnit = null;
        switch (unit) {
            case CELSIUS:
                queryUnit = Constants.QUERY_CELSIUS;
                break;
            case FAHRENHEIT:
                queryUnit = Constants.QUERY_FAHRENHEIT;
                break;
            default:
                queryUnit = Constants.QUERY_CELSIUS;
        }

        Call<WeatherDataModel> call = openWeather.loadWeatherData(cityId, queryUnit, Constants.API_KEY);
        Response<WeatherDataModel> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.body() == null || response.body().getDt() == 0f) {
            return Result.RETRY;
        }
        WeatherDataModel data = response.body();

        WeatherData weatherData = new WeatherData();
        weatherData.setId(1);
        weatherData.setDt(data.getDt());
        weatherData.setTemperature(data.getMain().getTemp());
        weatherData.setCity(data.getName());
        weatherData.setCountry(data.getSys().getCountry());
        Database db = Database.getDatabase(getApplicationContext());
        db.weatherDataDAO().insert(weatherData);

//        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
//        Intent intent = new Intent();
//        intent.setAction(Constants.WEATHER_DATA_DOWNLOAD_FINISHED);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("WeatherDataModel", data);
//        intent.putExtras(bundle);
//        localBroadcastManager.sendBroadcast(intent);

//        Data output = new Data.Builder()
//                .putString("data", city)
//                .build();
//        setOutputData(output);
        Log.d(TAG, "work done");

        return Result.SUCCESS;
    }


}
