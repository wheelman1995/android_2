package ru.wheelman.weather.presentation.utils.worker.current;

import android.content.Context;

import java.util.Iterator;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.interactors.CurrentWeatherInteractor;
import ru.wheelman.weather.presentation.utils.worker.WorkerConstants;
import toothpick.Toothpick;

public class CurrentWeatherUpdateWorker extends Worker {

    @Inject
    CurrentWeatherInteractor currentWeatherInteractor;

    public CurrentWeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
                    currentWeatherInteractor.updateCurrentWeatherConditionsByCityId();
                    return Result.success();
                case WorkerConstants.TAG_UPDATE_BY_COORDINATES:
                    currentWeatherInteractor.updateCurrentWeatherConditionsByCoordinates();
                    return Result.success();

            }
        }

        return Result.failure();
    }
}
