package ru.wheelman.weather.presentation.view_model;

public interface MainActivityViewModel {
    MainActivityViewModelImpl.ScreenState getScreenState();

    void onRequestPermissionsResult(boolean permissionsGranted);

    void onStop();

    void onStart(boolean permissionsGranted);

    void onCreate(boolean permissionsGranted);

    void onDestroy(boolean isFinishing);
}
