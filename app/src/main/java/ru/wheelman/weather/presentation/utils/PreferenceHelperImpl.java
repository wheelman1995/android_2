package ru.wheelman.weather.presentation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.Units;

@ApplicationScope
public class PreferenceHelperImpl implements PreferenceHelper {
    private static final double INVALID_LONGITUDE = -181d;
    private static final double INVALID_LATITUDE = -91d;
    private static final String TEMPERATURE_UNITS_KEY = "units";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    //    private static final String CITY_ID_KEY = "city_id";
    private static final String LATEST_CITY_ID_KEY = "latest_city_id";
    //    private static final String IS_USING_CURRENT_LOCATION_KEY = "is_using_current_location";
    private static final String LOCATION_LISTENING_STATUS_KEY = "location_listening_status";
    private static final String CURRENT_WEATHER_IS_BEING_UPDATED_KEY = "current_weather_is_being_updated";
    private static final String FORECASTED_WEATHER_IS_BEING_UPDATED_KEY = "forecasted_weather_is_being_updated";
    private static final int DEFAULT_UNIT = Units.CELSIUS.getUnitIndex();
    private static final String TAG = PreferenceHelperImpl.class.getSimpleName();


    private SharedPreferences.OnSharedPreferenceChangeListener localListener;
    private SharedPreferences sharedPreferences;
    //    private List<CityIdListener> cityIdListeners;
    private List<LatestCityIdListener> latestCityIdListeners;
    //    private List<LocationListener> locationListeners;
    private List<CurrentWeatherUpdateStatusListener> currentWeatherUpdateStatusListeners;
    private List<ForecastedWeatherUpdateStatusListener> forecastedWeatherUpdateStatusListeners;
    private SharedPreferences.Editor editor;


    @Inject
    public PreferenceHelperImpl(Context context) {
        initVariables(context);

        initListeners();
    }

    private void initListeners() {
        localListener = (sharedPreferences, key) -> {
            switch (key) {
//                case CITY_ID_KEY:
//                    notifyCityIdChanged();
//                    break;
//                case LATITUDE_KEY:
//                case LONGITUDE_KEY:
//                    notifyLocationChanged();
//                    break;
//                case TEMPERATURE_UNITS_KEY:
//                    notifyUnitsChanged();
//                    break;
//                case IS_USING_CURRENT_LOCATION_KEY:
//                    notifyIsUsingCurrentLocationChanged();
//                    break;
                case LATEST_CITY_ID_KEY:
                    notifyLatestCityIdChanged();
                    break;
                case CURRENT_WEATHER_IS_BEING_UPDATED_KEY:
                    notifyCurrentWeatherIsBeingUpdatedChanged();
                    break;
                case FORECASTED_WEATHER_IS_BEING_UPDATED_KEY:
                    notifyForecastedWeatherIsBeingUpdatedChanged();
                    break;

            }
        };
    }

    private void notifyForecastedWeatherIsBeingUpdatedChanged() {
        for (int i = 0; i < forecastedWeatherUpdateStatusListeners.size(); i++) {
            forecastedWeatherUpdateStatusListeners.get(i).forecastedWeatherUpdateStatusChanged();
        }
    }

    private void notifyCurrentWeatherIsBeingUpdatedChanged() {
        for (int i = 0; i < currentWeatherUpdateStatusListeners.size(); i++) {
            currentWeatherUpdateStatusListeners.get(i).currentWeatherUpdateStatusChanged();
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void initVariables(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
//        cityIdListeners = new ArrayList<>();
        latestCityIdListeners = new ArrayList<>();
//        locationListeners = new ArrayList<>();
        currentWeatherUpdateStatusListeners = new ArrayList<>();
        forecastedWeatherUpdateStatusListeners = new ArrayList<>();
    }

    private void notifyLatestCityIdChanged() {
        for (int i = 0; i < latestCityIdListeners.size(); i++) {
            latestCityIdListeners.get(i).latestCityIdChanged();
        }
    }

//    private void notifyIsUsingCurrentLocationChanged() {
//        for (int i = 0; i < cityIdListeners.size(); i++) {
//            locationListeners.get(i).isUsingCurrentLocationChanged();
//        }
//    }

//    private void notifyUnitsChanged() {
//        for (int i = 0; i < cityIdListeners.size(); i++) {
//            cityIdListeners.get(i).unitsChanged();
//        }
//    }
//
//    private void notifyCityIdChanged() {
//        for (int i = 0; i < cityIdListeners.size(); i++) {
//            cityIdListeners.get(i).cityIdChanged();
//        }
//    }
//
//    private void notifyLocationChanged() {
//        for (int i = 0; i < locationListeners.size(); i++) {
//            locationListeners.get(i).locationChanged();
//            Log.d(TAG, "notifyLocationChanged");
//        }
//    }

    @Override
    public boolean currentWeatherIsBeingUpdated() {
        return sharedPreferences.getBoolean(CURRENT_WEATHER_IS_BEING_UPDATED_KEY, false);
    }

    @Override
    public void setCurrentWeatherIsBeingUpdated(boolean currentWeatherIsBeingUpdated) {
        if (currentWeatherIsBeingUpdated != currentWeatherIsBeingUpdated()) {
            editor.putBoolean(CURRENT_WEATHER_IS_BEING_UPDATED_KEY, currentWeatherIsBeingUpdated).apply();
        }
    }

    @Override
    public boolean forecastedWeatherIsBeingUpdated() {
        return sharedPreferences.getBoolean(FORECASTED_WEATHER_IS_BEING_UPDATED_KEY, false);
    }

    @Override
    public void setForecastedWeatherIsBeingUpdated(boolean forecastedWeatherIsBeingUpdated) {
        if (forecastedWeatherIsBeingUpdated != forecastedWeatherIsBeingUpdated()) {
            editor.putBoolean(FORECASTED_WEATHER_IS_BEING_UPDATED_KEY, forecastedWeatherIsBeingUpdated).apply();
        }
    }

    @Override
    public boolean isListeningToLocationChanges() {
        return sharedPreferences.getBoolean(LOCATION_LISTENING_STATUS_KEY, false);
    }

    @Override
    public void setListeningToLocationChanges(boolean isListeningToLocationChanges) {
        if (isListeningToLocationChanges != isListeningToLocationChanges()) {
            editor.putBoolean(LOCATION_LISTENING_STATUS_KEY, isListeningToLocationChanges).apply();
        }
    }

    @Override
    public boolean cityIdIsValid() {
        return getLatestCityId() != LATEST_CITY_ID_DEFAULT_VALUE;
    }
//    @Override
//    public boolean isUsingCurrentLocation() {
//        return sharedPreferences.getBoolean(IS_USING_CURRENT_LOCATION_KEY, false);
//    }
//
//    @Override
//    public void setUsingCurrentLocation(boolean isUsingCurrentLocation) {
//        if (isUsingCurrentLocation != isUsingCurrentLocation()) {
//            editor.putBoolean(IS_USING_CURRENT_LOCATION_KEY, isUsingCurrentLocation).apply();
//        }
//    }

//    @Override
//    public int getCityId() {
//        return sharedPreferences.getInt(CITY_ID_KEY, 0);
//    }
//
//    @SuppressLint("ApplySharedPref")
//    @Override
//    public void setCityId(int cityId) {
//        if (cityId != getCityId()) {
//            editor.putInt(CITY_ID_KEY, cityId).commit();
//        }
//    }

    @Override
    public int getLatestCityId() {
        return sharedPreferences.getInt(LATEST_CITY_ID_KEY, LATEST_CITY_ID_DEFAULT_VALUE);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void setLatestCityId(int latestCityId) {
        if (latestCityId != getLatestCityId()) {
            editor.putInt(LATEST_CITY_ID_KEY, latestCityId).commit();
        }
    }

    @Override
    public double getLatitude() {
        String result = sharedPreferences.getString(LATITUDE_KEY, null);
        return result == null ? INVALID_LATITUDE : Double.valueOf(result);
    }


    @Override
    public void setLatitude(double latitude) {
        if (latitude != getLatitude()) {
            editor.putString(LATITUDE_KEY, Double.toString(latitude)).commit();
        }
    }

    @Override
    public double getLongitude() {
        String result = sharedPreferences.getString(LONGITUDE_KEY, null);
        return result == null ? INVALID_LONGITUDE : Double.valueOf(result);
    }
//    @Override
//    public void setLocation(double latitude, double longitude) {
//
//        if (latitude != getLatitude()) {
//            editor.putString(LATITUDE_KEY, Double.toString(latitude));
//        }
//        if (longitude != getLongitude()) {
//            editor.putString(LONGITUDE_KEY, Double.toString(longitude));
//        }
//
//        Log.d(TAG, "setLocation");
//    }

    @Override
    public void setLongitude(double longitude) {
        if (longitude != getLongitude()) {
            editor.putString(LONGITUDE_KEY, Double.toString(longitude)).commit();
        }
    }

    @Override
    public Units getUnits() {
        return Units.getUnitByIndex(sharedPreferences.getInt(TEMPERATURE_UNITS_KEY, DEFAULT_UNIT));
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void setUnits(Units units) {
        if (units.getUnitIndex() != getUnits().getUnitIndex()) {
            editor.putInt(TEMPERATURE_UNITS_KEY, units.getUnitIndex()).commit();
        }
    }

//    @Override
//    public void subscribeToChanges(CityIdListener cityIdListener) {
//        if (!cityIdListeners.contains(cityIdListener)) {
//            checkOnSharedPreferenceChangeListenerIsRegistered();
//            cityIdListeners.add(cityIdListener);
//        }
//    }

//    @Override
//    public void unsubscribe(CityIdListener cityIdListener) {
//        cityIdListeners.remove(cityIdListener);
//        checkOnSharedPreferenceChangeListenerNeedsToUnregister();
//    }

//    @Override
//    public void subscribeToLocationChanges(LocationListener listener) {
//        if (!locationListeners.contains(listener)) {
//            checkOnSharedPreferenceChangeListenerIsRegistered();
//            locationListeners.add(listener);
//            Log.d(TAG, "subscribedToLocationChanges");
//        }
//    }

    //    @Override
//    public void unsubscribeFromLocationChanges(LocationListener listener) {
//        locationListeners.remove(listener);
//        checkOnSharedPreferenceChangeListenerNeedsToUnregister();
//    }
//
    @Override
    public void subscribeToLatestCityIdChanges(LatestCityIdListener latestCityIdListener) {
        if (!latestCityIdListeners.contains(latestCityIdListener)) {
            checkOnSharedPreferenceChangeListenerIsRegistered();
            latestCityIdListeners.add(latestCityIdListener);
            latestCityIdListener.latestCityIdChanged();
        }
    }

    @Override
    public void unsubscribeFromLatestCityIdChanges(LatestCityIdListener latestCityIdListener) {
        latestCityIdListeners.remove(latestCityIdListener);
        checkOnSharedPreferenceChangeListenerNeedsToUnregister();
    }

    @Override
    public void subscribeToCurrentWeatherUpdateStatusChanges(CurrentWeatherUpdateStatusListener currentWeatherUpdateStatusListener) {
        if (!currentWeatherUpdateStatusListeners.contains(currentWeatherUpdateStatusListener)) {
            checkOnSharedPreferenceChangeListenerIsRegistered();
            currentWeatherUpdateStatusListeners.add(currentWeatherUpdateStatusListener);
            currentWeatherUpdateStatusListener.currentWeatherUpdateStatusChanged();
        }
    }

    @Override
    public void unsubscribeFromCurrentWeatherUpdateStatusChanges(CurrentWeatherUpdateStatusListener currentWeatherUpdateStatusListener) {
        currentWeatherUpdateStatusListeners.remove(currentWeatherUpdateStatusListener);
        checkOnSharedPreferenceChangeListenerNeedsToUnregister();
    }

    @Override
    public void subscribeToForecastedWeatherUpdateStatusChanges(ForecastedWeatherUpdateStatusListener forecastedWeatherUpdateStatusListener) {
        if (!forecastedWeatherUpdateStatusListeners.contains(forecastedWeatherUpdateStatusListener)) {
            checkOnSharedPreferenceChangeListenerIsRegistered();
            forecastedWeatherUpdateStatusListeners.add(forecastedWeatherUpdateStatusListener);
            forecastedWeatherUpdateStatusListener.forecastedWeatherUpdateStatusChanged();
        }
    }

    @Override
    public void unsubscribeFromForecastedWeatherUpdateStatusChanges(ForecastedWeatherUpdateStatusListener forecastedWeatherUpdateStatusListener) {
        forecastedWeatherUpdateStatusListeners.remove(forecastedWeatherUpdateStatusListener);
        checkOnSharedPreferenceChangeListenerNeedsToUnregister();
    }

    private void checkOnSharedPreferenceChangeListenerIsRegistered() {
        if (noListeners()) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(localListener);
        }
    }

    private void checkOnSharedPreferenceChangeListenerNeedsToUnregister() {
        if (noListeners()) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(localListener);
        }
    }

    @Override
    public boolean isFirstStart() {
        return getLatestCityId() == PreferenceHelper.LATEST_CITY_ID_DEFAULT_VALUE;
    }

    private boolean noListeners() {
        return latestCityIdListeners.isEmpty() &&
                currentWeatherUpdateStatusListeners.isEmpty() &&
                forecastedWeatherUpdateStatusListeners.isEmpty();
    }

}
