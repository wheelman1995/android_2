package ru.wheelman.weather.presentation.utils;

import ru.wheelman.weather.domain.entities.Units;

public interface PreferenceHelper {
    int LATEST_CITY_ID_DEFAULT_VALUE = -1;

    boolean currentWeatherIsBeingUpdated();

    void setCurrentWeatherIsBeingUpdated(boolean currentWeatherIsBeingUpdated);

    boolean forecastedWeatherIsBeingUpdated();

    void setForecastedWeatherIsBeingUpdated(boolean forecastedWeatherIsBeingUpdated);

    boolean isListeningToLocationChanges();

    void setListeningToLocationChanges(boolean isListeningToLocationChanges);

//    boolean isUsingCurrentLocation();

//    void setUsingCurrentLocation(boolean isUsingCurrentLocation);

//    int getCityId();

//    void setCityId(int cityId);

    boolean cityIdIsValid();

    int getLatestCityId();

    void setLatestCityId(int latestCityId);

    double getLatitude();

//    void setLocation(double latitude, double longitude);

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);

    Units getUnits();

    void setUnits(Units units);

//    void subscribeToChanges(CityIdListener cityIdListener);

//    void unsubscribe(CityIdListener cityIdListener);

//    void subscribeToLocationChanges(LocationListener listener);

//    void unsubscribeFromLocationChanges(LocationListener listener);

    void subscribeToLatestCityIdChanges(LatestCityIdListener latestCityIdListener);

    void subscribeToCurrentWeatherUpdateStatusChanges(CurrentWeatherUpdateStatusListener currentWeatherUpdateStatusListener);

    void unsubscribeFromLatestCityIdChanges(LatestCityIdListener latestCityIdListener);

    void unsubscribeFromCurrentWeatherUpdateStatusChanges(CurrentWeatherUpdateStatusListener currentWeatherUpdateStatusListener);

    void subscribeToForecastedWeatherUpdateStatusChanges(ForecastedWeatherUpdateStatusListener forecastedWeatherUpdateStatusListener);

    void unsubscribeFromForecastedWeatherUpdateStatusChanges(ForecastedWeatherUpdateStatusListener forecastedWeatherUpdateStatusListener);

    boolean isFirstStart();

    interface CityIdListener {
        void cityIdChanged();
    }

    interface UnitsListener {
        void unitsChanged();
    }

    interface LocationListener {
        void locationChanged();

//        void isUsingCurrentLocationChanged();
    }

    interface LatestCityIdListener {
        void latestCityIdChanged();
    }

    interface CurrentWeatherUpdateStatusListener {
        void currentWeatherUpdateStatusChanged();
    }

    interface ForecastedWeatherUpdateStatusListener {
        void forecastedWeatherUpdateStatusChanged();
    }

}
