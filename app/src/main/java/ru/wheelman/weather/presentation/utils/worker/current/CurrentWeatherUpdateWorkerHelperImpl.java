package ru.wheelman.weather.presentation.utils.worker.current;

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
public class CurrentWeatherUpdateWorkerHelperImpl implements CurrentWeatherUpdateWorkerHelper {

    private static final String UNIQUE_WORK_NAME_UPDATE_CURRENT_WEATHER = "update_current_weather";

    @Inject
    public CurrentWeatherUpdateWorkerHelperImpl() {
    }

    @Override
    public void updateCurrentWeatherConditionsByCityId() {
        cancelUniqueWork();

        PeriodicWorkRequest weatherUpdateWork = new PeriodicWorkRequest.Builder(
                CurrentWeatherUpdateWorker.class,
                SettingsFragment.UPDATE_INTERVAL_MILLISECONDS,
                REPEAT_INTERVAL_TIMEUNIT)
                .addTag(TAG_UPDATE_BY_CITY_ID)
                .setConstraints(CONSTRAINTS)
                .setBackoffCriteria(BACKOFF_POLICY, BACKOFF_DELAY_MILLISECONDS, BACKOFF_DELAY_TIMEUNIT)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(UNIQUE_WORK_NAME_UPDATE_CURRENT_WEATHER, EXISTING_PERIODIC_WORK_POLICY, weatherUpdateWork);
    }

    @Override
    public void updateCurrentWeatherConditionsByCoordinates() {
        cancelUniqueWork();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CurrentWeatherUpdateWorker.class)
                .addTag(TAG_UPDATE_BY_COORDINATES)
                .setConstraints(CONSTRAINTS)
                .setBackoffCriteria(BACKOFF_POLICY, BACKOFF_DELAY_MILLISECONDS, BACKOFF_DELAY_TIMEUNIT)
                .build();

        WorkManager.getInstance().enqueueUniqueWork(UNIQUE_WORK_NAME_UPDATE_CURRENT_WEATHER, EXISTING_WORK_POLICY, oneTimeWorkRequest);
    }

    private void cancelUniqueWork() {
        WorkManager.getInstance().cancelUniqueWork(UNIQUE_WORK_NAME_UPDATE_CURRENT_WEATHER);
    }
}
