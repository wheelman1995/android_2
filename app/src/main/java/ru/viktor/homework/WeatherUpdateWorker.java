package ru.viktor.homework;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WeatherUpdateWorker extends Worker {

    private static final String TAG = "WeatherUpdateWorker";

    public WeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String city = getInputData().getString("city");
        // get weather data from Internet here


        Data output = new Data.Builder()
                .putString("data", city)
                .build();
        setOutputData(output);
        Log.d(TAG, "work done");

        return Result.SUCCESS;
    }


}
