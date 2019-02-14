package ru.wheelman.weather.presentation.view_model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import ru.wheelman.weather.BR;
import ru.wheelman.weather.R;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.CurrentWeatherViewModelScope;
import ru.wheelman.weather.di.scopes.MainActivityViewModelScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;
import ru.wheelman.weather.domain.interactors.CurrentWeatherInteractor;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import toothpick.Scope;
import toothpick.Toothpick;

public class CurrentWeatherViewModelImpl extends ViewModel implements CurrentWeatherViewModel {

    private static final String TAG = CurrentWeatherViewModelImpl.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM, HH:mm", Locale.UK);
    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    CurrentWeatherInteractor currentWeatherInteractor;
    @Inject
    Context context;
    @Inject
    UpdateMethodSelector updateMethodSelector;
    @Inject
    MainActivityViewModelImpl.ScreenState mainActivityScreenState;
    private PreferenceHelper.LatestCityIdListener latestCityIdListener;
    private LiveData<CurrentWeatherConditions> liveCurrentWeatherConditions;
    private Observer<CurrentWeatherConditions> currentWeatherConditionsObserver;
    private ScreenState screenState;
    private PreferenceHelper.CurrentWeatherUpdateStatusListener currentWeatherUpdateStatusListener;

    public CurrentWeatherViewModelImpl() {
        initToothpick();

        screenState = new ScreenState();
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());

        initListeners();

    }

    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, MainActivityViewModelScope.class, CurrentWeatherViewModelScope.class);
        Toothpick.inject(this, scope);
    }

    private void initListeners() {
        latestCityIdListener = this::updateWeather;

        currentWeatherConditionsObserver = this::processNewCurrentWeatherConditions;

        currentWeatherUpdateStatusListener = () -> {
            screenState.setCurrentWeatherIsBeingUpdated(preferenceHelper.currentWeatherIsBeingUpdated());
        };

    }

    private void processNewCurrentWeatherConditions(CurrentWeatherConditions currentWeatherConditions) {
        if (currentWeatherConditions != null) {

            String actionBarTitle = context.getString(
                    R.string.city_and_country,
                    currentWeatherConditions.getCityName(),
                    currentWeatherConditions.getCountry());

            if (preferenceHelper.isListeningToLocationChanges()) {
                actionBarTitle += " " + context.getString(R.string.location_symbol);
            }

            mainActivityScreenState.setActionBarTitle(actionBarTitle);

            long currentUTC = Calendar.getInstance().getTimeInMillis();
            long sunrise = currentWeatherConditions.getSunrise() * 1000L;// to milliseconds
            long sunset = currentWeatherConditions.getSunset() * 1000L;
//            Log.d(TAG, "cur: " + currentUTC + " sunset " + sunset + " sunrise " + sunrise);
            //day
            if (sunrise < currentUTC && currentUTC < sunset) {
                //            Log.d(TAG, "day");
                mainActivityScreenState.setNavDrawerHeaderBackgroundDrawableId(R.drawable.day);
                mainActivityScreenState.setNavDrawerHeaderForegroundDrawableId(R.drawable.sun);
            } else {
                //night
                //            Log.d(TAG, "night");
                mainActivityScreenState.setNavDrawerHeaderBackgroundDrawableId(R.drawable.night);
                mainActivityScreenState.setNavDrawerHeaderForegroundDrawableId(R.drawable.crescent);
            }

            Date date = new Date(currentWeatherConditions.getDataReceivingTime() * 1000);
            screenState.setDataReceivingTime(DATE_FORMAT.format(date));

            date.setTime(currentWeatherConditions.getUpdateTime());
            screenState.setUpdateTime(DATE_FORMAT.format(date));

            int temperatureRes = 0;
            switch (currentWeatherConditions.getUnits()) {
                case CELSIUS:
                    temperatureRes = R.string.celsius;
                    break;
                case FAHRENHEIT:
                    temperatureRes = R.string.fahrenheit;
                    break;
            }
            screenState.setTemperature(context.getString(temperatureRes, currentWeatherConditions.getTemperature()));


            downloadBitmap(currentWeatherConditions.getWeatherIconURL());

            screenState.setWeatherConditionDescription(currentWeatherConditions.getWeatherConditionDescription());

            Log.d(TAG, "original icon url " + currentWeatherConditions.getWeatherIconURL());
            Matcher matcher = Pattern.compile("\\d{2}[n,d]").matcher(currentWeatherConditions.getWeatherIconURL());
            if (matcher.find()) {
                String iconId = matcher.group();
                screenState.setBackgroundURI(Uri.parse(String.format(
                        Locale.UK,
                        "android.resource://%s/drawable/b%s",
                        context.getPackageName(),
                        iconId)));
                screenState.setBackgroundIsDark(iconId.endsWith("n"));
                Log.d(TAG, "icon id " + iconId);
            }
        }

    }

    private void downloadBitmap(String weatherIconURL) {
        Picasso.get().load(weatherIconURL).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                screenState.setWeatherIcon(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }


    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

//    private void createDb() {
//        db = Database.getDatabase(getApplication());
//    }

    private void updateWeather() {
        Log.d(TAG, "latest city id changed");
        removeCurrentWeatherConditionsObserver();

        liveCurrentWeatherConditions = currentWeatherInteractor.getCurrentWeatherConditionsByCityId();

        liveCurrentWeatherConditions.observeForever(currentWeatherConditionsObserver);
    }

    private void removeCurrentWeatherConditionsObserver() {
        if (liveCurrentWeatherConditions != null && liveCurrentWeatherConditions.hasObservers()) {
            liveCurrentWeatherConditions.removeObserver(currentWeatherConditionsObserver);
        }
    }

    @Override
    protected void onCleared() {
//        Database.destroyInstance();
        removeCurrentWeatherConditionsObserver();
        Toothpick.closeScope(CurrentWeatherViewModelScope.class);
        super.onCleared();
    }

    @Override
    public void onRefreshSwipeRefreshLayout() {
        updateMethodSelector.selectAndUpdate();
        preferenceHelper.setCurrentWeatherIsBeingUpdated(true);
    }

    @Override
    public void onStart() {
        preferenceHelper.subscribeToLatestCityIdChanges(latestCityIdListener);
        preferenceHelper.subscribeToCurrentWeatherUpdateStatusChanges(currentWeatherUpdateStatusListener);
    }

    @Override
    public void onStop() {
        preferenceHelper.unsubscribeFromLatestCityIdChanges(latestCityIdListener);
        preferenceHelper.unsubscribeFromCurrentWeatherUpdateStatusChanges(currentWeatherUpdateStatusListener);
    }

    public static class ScreenState extends BaseObservable {
        private boolean currentWeatherIsBeingUpdated;
        private String dataReceivingTime;
        private String temperature;
        private Bitmap weatherIcon;
        private String weatherConditionDescription;
        private Uri backgroundURI;
        private String updateTime;
        private boolean backgroundIsDark;

        @Bindable
        public boolean isBackgroundIsDark() {
            return backgroundIsDark;
        }

        public void setBackgroundIsDark(boolean backgroundIsDark) {
            this.backgroundIsDark = backgroundIsDark;
            notifyPropertyChanged(BR.backgroundIsDark);
        }

        @Bindable
        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
            notifyPropertyChanged(BR.updateTime);
        }

        @Bindable
        public String getDataReceivingTime() {
            return dataReceivingTime;
        }

        public void setDataReceivingTime(String dataReceivingTime) {
            this.dataReceivingTime = dataReceivingTime;
            notifyPropertyChanged(BR.dataReceivingTime);
        }

        @Bindable
        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
            notifyPropertyChanged(BR.temperature);
        }

        @Bindable
        public Bitmap getWeatherIcon() {
            return weatherIcon;
        }

        public void setWeatherIcon(Bitmap weatherIcon) {
            this.weatherIcon = weatherIcon;
            notifyPropertyChanged(BR.weatherIcon);
        }

        @Bindable
        public String getWeatherConditionDescription() {
            return weatherConditionDescription;
        }

        public void setWeatherConditionDescription(String weatherConditionDescription) {
            this.weatherConditionDescription = weatherConditionDescription;
            notifyPropertyChanged(BR.weatherConditionDescription);
        }

        @Bindable
        public Uri getBackgroundURI() {
            return backgroundURI;
        }

        public void setBackgroundURI(Uri backgroundURI) {
            this.backgroundURI = backgroundURI;
            notifyPropertyChanged(BR.backgroundURI);
        }

        @Bindable
        public boolean getCurrentWeatherIsBeingUpdated() {
            return currentWeatherIsBeingUpdated;
        }

        public void setCurrentWeatherIsBeingUpdated(boolean currentWeatherIsBeingUpdated) {
            this.currentWeatherIsBeingUpdated = currentWeatherIsBeingUpdated;
            notifyPropertyChanged(BR.currentWeatherIsBeingUpdated);
        }
    }
}
