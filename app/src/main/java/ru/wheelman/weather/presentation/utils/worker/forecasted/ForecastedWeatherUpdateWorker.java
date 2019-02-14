package ru.wheelman.weather.presentation.utils.worker.forecasted;

import android.content.Context;

import java.util.Iterator;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.interactors.ForecastedWeatherInteractor;
import ru.wheelman.weather.presentation.utils.worker.WorkerConstants;
import toothpick.Toothpick;

public class ForecastedWeatherUpdateWorker extends Worker {

    @Inject
    ForecastedWeatherInteractor forecastedWeatherInteractor;

    public ForecastedWeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        Toothpick.inject(this, Toothpick.openScope(ApplicationScope.class));
    }

    @NonNull
    @Override
    public Result doWork() {

        Iterator<String> iterator = getTags().iterator();

        while (iterator.hasNext()) {
            String tag = iterator.next();

            switch (tag) {
                case WorkerConstants.TAG_UPDATE_BY_CITY_ID:
                    forecastedWeatherInteractor.updateFiveDayForecastByCityId();
                    return Result.success();

                case WorkerConstants.TAG_UPDATE_BY_COORDINATES:
                    forecastedWeatherInteractor.updateFiveDayForecastByCoordinates();
                    return Result.success();
            }
        }

        return Result.failure();
    }
}
