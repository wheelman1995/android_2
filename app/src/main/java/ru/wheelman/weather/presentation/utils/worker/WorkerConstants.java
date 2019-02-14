package ru.wheelman.weather.presentation.utils.worker;

import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.WorkRequest;

public final class WorkerConstants {
    public static final String TAG_UPDATE_BY_CITY_ID = "update_by_city_id";
    public static final String TAG_UPDATE_BY_COORDINATES = "update_by_coordinates";
    //    public static final int REPEAT_INTERVAL = 60;
    public static final TimeUnit BACKOFF_DELAY_TIMEUNIT = TimeUnit.MILLISECONDS;
    public static final TimeUnit REPEAT_INTERVAL_TIMEUNIT = TimeUnit.MILLISECONDS;
    public static final BackoffPolicy BACKOFF_POLICY = BackoffPolicy.EXPONENTIAL;
    public static final ExistingPeriodicWorkPolicy EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.REPLACE;
    public static final ExistingWorkPolicy EXISTING_WORK_POLICY = ExistingWorkPolicy.REPLACE;
    public static final long BACKOFF_DELAY_MILLISECONDS = WorkRequest.MIN_BACKOFF_MILLIS;
    public static final Constraints CONSTRAINTS = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    private WorkerConstants() {
    }


}
