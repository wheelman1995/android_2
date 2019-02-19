package ru.wheelman.weather.presentation.data_mappers;

import android.content.Context;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import ru.wheelman.weather.R;
import ru.wheelman.weather.di.scopes.CurrentWeatherViewModelScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.view_model.CurrentWeatherViewModelImpl;
import ru.wheelman.weather.presentation.view_model.MainActivityViewModelImpl;

@CurrentWeatherViewModelScope
public class CurrentWeatherDataMapper implements DataMapper<CurrentWeatherConditions, CurrentWeatherViewModelImpl.ScreenState> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM, HH:mm", Locale.UK);
    @Inject
    Context context;
    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    MainActivityViewModelImpl.ScreenState mainActivityScreenState;
    private CurrentWeatherConditions from;
    private CurrentWeatherViewModelImpl.ScreenState to;


    @Inject
    public CurrentWeatherDataMapper() {
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public void map(CurrentWeatherConditions from, CurrentWeatherViewModelImpl.ScreenState to) {
        this.from = from;
        this.to = to;

        setActionBar();
        setNavDrawerHeader();
        setFragment();
    }

    private void setFragment() {
        to.setDataReceivingTime(DATE_FORMAT.format(from.getDataReceivingTime()));
        to.setUpdateTime(DATE_FORMAT.format(from.getUpdateTime()));

        int temperatureRes = 0;
        switch (from.getUnits()) {
            case CELSIUS:
                temperatureRes = R.string.celsius;
                break;
            case FAHRENHEIT:
                temperatureRes = R.string.fahrenheit;
                break;
        }
        to.setTemperature(context.getString(temperatureRes, from.getTemperature()));

        to.setWeatherIconURL(from.getWeatherIconURL());

        to.setWeatherConditionDescription(from.getWeatherConditionDescription());

        Matcher matcher = Pattern.compile("\\d{2}[n,d]").matcher(from.getWeatherIconURL());
        if (matcher.find()) {
            String iconId = matcher.group();
            to.setBackgroundURI(Uri.parse(String.format(
                    Locale.UK,
                    "android.resource://%s/drawable/b%s",
                    context.getPackageName(),
                    iconId)));
            to.setBackgroundIsDark(iconId.endsWith("n"));
        }

    }

    private void setNavDrawerHeader() {
        long currentUTC = Calendar.getInstance().getTimeInMillis();
        long sunrise = from.getSunrise() * 1000L;// to milliseconds
        long sunset = from.getSunset() * 1000L;
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
    }

    private void setActionBar() {
        String actionBarTitle = context.getString(
                R.string.city_and_country,
                from.getCityName(),
                from.getCountry());

        if (preferenceHelper.isListeningToLocationChanges()) {
            actionBarTitle += " " + context.getString(R.string.location_symbol);
        }

        mainActivityScreenState.setActionBarTitle(actionBarTitle);
    }
}
