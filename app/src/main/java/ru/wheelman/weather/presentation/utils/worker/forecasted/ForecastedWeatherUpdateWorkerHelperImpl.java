package ru.wheelman.weather.presentation.utils.worker.forecasted;

import javax.inject.Inject;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.presentation.view.fragments.SettingsFragment;

import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.BACKOFF_DELAY_MILLISECONDS;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.BACKOFF_DELAY_TIMEUNIT;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.BACKOFF_POLICY;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.CONSTRAINTS;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.EXISTING_PERIODIC_WORK_POLICY;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.EXISTING_WORK_POLICY;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.REPEAT_INTERVAL_TIMEUNIT;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.TAG_UPDATE_BY_CITY_ID;
import static ru.wheelman.weather.presentation.utils.worker.WorkerConstants.TAG_UPDATE_BY_COORDINATES;

@ApplicationScope
public class ForecastedWeatherUpdateWorkerHelperImpl implements ForecastedWeatherUpdateWorkerHelper {

    private static final String UNIQUE_WORK_NAME_UPDATE_FORECASTED_WEATHER = "update_forecasted_weather";

    @Inject
    public ForecastedWeatherUpdateWorkerHelperImpl() {
    }

    @Override
    public void updateFiveDayForecastByCityId() {
        cancelUniqueWork();

        PeriodicWorkRequest weatherUpdateWork = new PeriodicWorkRequest.Builder(
                ForecastedWeatherUpdateWorker.class,
                SettingsFragment.UPDATE_INTERVAL_MILLISECONDS,
                REPEAT_INTERVAL_TIMEUNIT)
                .addTag(TAG_UPDATE_BY_CITY_ID)
                .setConstraints(CONSTRAINTS)
                .setBackoffCriteria(BACKOFF_POLICY, BACKOFF_DELAY_MILLISECONDS, BACKOFF_DELAY_TIMEUNIT)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(UNIQUE_WORK_NAME_UPDATE_FORECASTED_WEATHER, EXISTING_PERIODIC_WORK_POLICY, weatherUpdateWork);
    }

    private void cancelUniqueWork() {
        WorkManager.getInstance().cancelUniqueWork(UNIQUE_WORK_NAME_UPDATE_FORECASTED_WEATHER);
    }

    @Override
    public void updateFiveDayForecastByCoordinates() {
        cancelUniqueWork();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ForecastedWeatherUpdateWorker.class)
                .addTag(TAG_UPDATE_BY_COORDINATES)
                .setConstraints(CONSTRAINTS)
                .setBackoffCriteria(BACKOFF_POLICY, BACKOFF_DELAY_MILLISECONDS, BACKOFF_DELAY_TIMEUNIT)
                .build();

        WorkManager.getInstance().enqueueUniqueWork(UNIQUE_WORK_NAME_UPDATE_FORECASTED_WEATHER, EXISTING_WORK_POLICY, oneTimeWorkRequest);
    }
}
