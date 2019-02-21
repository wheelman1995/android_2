package ru.wheelman.weather.presentation.utils;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import ru.wheelman.weather.di.scopes.ApplicationScope;

@ApplicationScope
public class DisplayMetricsHelperImpl implements DisplayMetricsHelper {
    private static final String TAG = DisplayMetricsHelperImpl.class.getSimpleName();

    private final float density;
    private final Context context;

    @Inject
    public DisplayMetricsHelperImpl(Context context) {
        this.context = context;
        density = context.getResources().getDisplayMetrics().density;
        Log.d(TAG, "DisplayMetricsHelperImpl: " + density);
    }

    @Override
    public int dpToPx(int dp) {
        return (int) (dp * density);
    }

    @Override
    public int pxToDp(int px) {
        return (int) (px / density);
    }
}
