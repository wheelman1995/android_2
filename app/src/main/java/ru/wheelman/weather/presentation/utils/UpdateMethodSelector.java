package ru.wheelman.weather.presentation.utils;

import androidx.lifecycle.MutableLiveData;

public interface UpdateMethodSelector {
    MutableLiveData<Boolean> getInternetConnected();

    void selectAndUpdate(int cityId);

    void selectAndUpdate();

    void updateByCoordinates();

    void onAppFirstStart(boolean updateByLocationAllowed);

    void onAppNormalStart(boolean updateByLocationAllowed);

    void appRestoredWithoutLocationServices();
}
