package ru.viktor.homework;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

public class WeatherUpdateService extends JobIntentService {

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WeatherUpdateService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }


}
