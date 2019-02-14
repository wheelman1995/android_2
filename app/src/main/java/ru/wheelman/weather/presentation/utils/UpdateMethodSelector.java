package ru.wheelman.weather.presentation.utils;

public interface UpdateMethodSelector {
    void selectAndUpdate(int cityId);

    void selectAndUpdate();

    void updateByCoordinates();

    void permissionsRevoked();

    void onAppFirstStart(boolean permissionsGranted);

    void onAppNormalStart(boolean permissionsGranted);
}
