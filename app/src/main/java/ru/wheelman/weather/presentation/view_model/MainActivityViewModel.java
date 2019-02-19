package ru.wheelman.weather.presentation.view_model;

import androidx.lifecycle.LiveData;

public interface MainActivityViewModel {
    LiveData<Boolean> isInternetConnected();

    MainActivityViewModelImpl.ScreenState getScreenState();

    void onRequestPermissionsResult(boolean permissionsGranted);

    void onStop();

    void onStart(boolean permissionsGranted);

    void onCreate(boolean permissionsGranted);

    void onDestroy(boolean isFinishing);
}
